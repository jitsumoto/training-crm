# 新規エンティティ & マイグレーション案

ER 図（`docs/er-diagram.md`）を基に、今後追加する `USERS` / `PROJECTS` / `TASKS` / `ACTIVITIES` のエンティティ設計と、データベースマイグレーションの草案をまとめます。現状のコードベースに直接は反映していませんが、実装時の下書きとして利用できます。

---

## 1. エンティティ設計（Java）

以下は `com.example.trainingcrm.entity` パッケージ配下に追加する想定クラスの雛形です。監査列や関連の fetch 戦略などは必要に応じて調整してください。

### 1.1 `User`
```java
package com.example.trainingcrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role; // ADMIN / STAFF など

    @Column(length = 100)
    private String email;

    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private Boolean enabled = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
```

### 1.2 `Project`
```java
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String clientName;

    @Column(nullable = false, length = 30)
    private String status; // PLANNING / IN_PROGRESS / CLOSED

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(columnDefinition = "text")
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
```

### 1.3 `Task`
```java
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(columnDefinition = "text")
    private String detail;

    private LocalDate dueDate;

    @Column(nullable = false, length = 20)
    private String status; // TODO / DOING / DONE

    private Integer progressPercent;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activities = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
```

### 1.4 `Activity`
```java
@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(length = 30)
    private String activityType; // MEETING / CALL / VISIT etc.

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime activityAt;

    @Column(length = 100)
    private String location;

    private LocalDateTime createdAt;
}
```

> **補足**
> - 監査列 (`createdAt` / `updatedAt`) は Spring Data JPA の `@CreatedDate`, `@LastModifiedDate` と `AuditingEntityListener` で自動化する想定。
> - 双方向関連が不要であれば `tasks` や `activities` の `List` は省略しても可。
> - 役割の列挙は Enum 化（`@Enumerated(EnumType.STRING)`）する選択肢もあります。

---

## 2. リポジトリ雛形

エンティティ毎に `JpaRepository` を用意します。例:

```java
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(String status);
}

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectProjectId(Long projectId);
    List<Task> findByAssigneeUserId(Long userId);
}

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTaskTaskId(Long taskId);
}

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

---

## 3. マイグレーション草案（PostgreSQL）

現状 Flyway/Liquibase は導入していないため、手動適用または将来的なマイグレーションツール導入を想定した SQL を用意します。`src/main/resources/db/migration/V1__init.sql` などに配置する構成を想定しています。

```sql
-- USERS
CREATE TABLE users (
    user_id        BIGSERIAL PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    email          VARCHAR(100),
    last_login_at  TIMESTAMP,
    enabled        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

-- PROJECTS
CREATE TABLE projects (
    project_id   BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    client_name  VARCHAR(100) NOT NULL,
    status       VARCHAR(30)  NOT NULL,
    start_date   DATE,
    end_date     DATE,
    description  TEXT,
    budget       NUMERIC(12,2),
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP
);

-- TASKS
CREATE TABLE tasks (
    task_id          BIGSERIAL PRIMARY KEY,
    project_id       BIGINT       NOT NULL REFERENCES projects(project_id) ON DELETE CASCADE,
    assignee_id      BIGINT       REFERENCES users(user_id) ON DELETE SET NULL,
    title            VARCHAR(120) NOT NULL,
    detail           TEXT,
    due_date         DATE,
    status           VARCHAR(20)  NOT NULL,
    progress_percent INTEGER,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);

-- ACTIVITIES
CREATE TABLE activities (
    activity_id   BIGSERIAL PRIMARY KEY,
    task_id       BIGINT       NOT NULL REFERENCES tasks(task_id) ON DELETE CASCADE,
    activity_type VARCHAR(30),
    description   TEXT         NOT NULL,
    activity_at   TIMESTAMP    NOT NULL,
    location      VARCHAR(100),
    created_at    TIMESTAMP
);

-- パフォーマンス向上用インデックス例
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_tasks_project ON tasks(project_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_activities_task ON activities(task_id);
```

### 3.1 既存テーブル拡張（任意）
- `students` / `records` に監査列 (`created_at`, `updated_at`) を追加する場合の SQL:
  ```sql
  ALTER TABLE students ADD COLUMN created_at TIMESTAMP;
  ALTER TABLE students ADD COLUMN updated_at TIMESTAMP;
  ALTER TABLE records ADD COLUMN created_at TIMESTAMP;
  ALTER TABLE records ADD COLUMN updated_at TIMESTAMP;
  ```
- `records` に合否カラムを追加する場合:
  ```sql
  ALTER TABLE records ADD COLUMN result VARCHAR(20);
  ```

---

## 4. 実装順序メモ

1. **エンティティ追加**: `User`, `Project`, `Task`, `Activity` を作成し、リポジトリを定義。
2. **DB マイグレーション**: 上記 SQL を適用（もしくは Flyway/Liquibase を導入して実行）。
3. **サービス層整備**: 案件/タスク/活動/ユーザ用サービスと DTO を実装。
4. **コントローラ & 画面**: REST API → 画面（Thymeleaf）の順で拡張。
5. **認証導入**: `User` を利用した Spring Security 設定へ段階的に移行。

ドメイン設計やバリデーションポリシーは今後の要件整理に応じて調整してください。
