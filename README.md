# Training CRM

職業訓練で作成した Spring Boot 学習用プロジェクトです。  
学生（Student）の登録・一覧・更新・削除と、学習成績（Record）、案件（Project）を扱うシンプルな CRM 風アプリケーションで、REST API と Thymeleaf ベースの画面をどちらも備えています。管理者ログインとユーザー管理機能を通じて、権限制御の学習にも利用できます。

---

## 主な機能

- 学生情報（名前・メール・電話・クラス）の CRUD
- 学生と成績レコード（Record）の 1:N 関連管理
- `/students` / `/projects` を軸にした REST + 画面 UI
- Thymeleaf テンプレートによる学生・成績・案件・ユーザー管理の CRUD 画面
- Spring Security（フォームログイン & HTTP Basic）、BCrypt ハッシュ済み管理者アカウントを同梱

---

## 技術スタック

- Java 17
- Spring Boot 3.5.6
  - Spring Web / Spring Data JPA / Spring Security / Thymeleaf
- PostgreSQL
- Maven（Maven Wrapper 同梱）
- Lombok

---

## プロジェクト構成

```
training-crm/
├── pom.xml
├── src/
│   ├── main/java/com/example/trainingcrm/
│   │   ├── TrainingCrmApplication.java
│   │   ├── config/
│   │   │   ├── DataInitializer.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── ProjectController.java      # 案件画面
│   │   │   ├── RecordController.java       # 成績 REST API
│   │   │   ├── RecordWebController.java    # 成績画面
│   │   │   ├── StudentController.java      # 学生 REST API
│   │   │   ├── StudentWebController.java   # 学生画面
│   │   │   └── UserController.java         # 管理者向けユーザー画面
│   │   ├── dto/
│   │   │   ├── ProjectForm.java
│   │   │   └── UserForm.java
│   │   ├── entity/
│   │   │   ├── Project.java
│   │   │   ├── Record.java
│   │   │   ├── Student.java
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   ├── ProjectRepository.java
│   │   │   ├── RecordRepository.java
│   │   │   ├── StudentRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/
│   │       ├── CustomUserDetailsService.java
│   │       ├── ProjectService.java
│   │       ├── RecordService.java
│   │       ├── StudentService.java
│   │       └── UserService.java
│   └── main/resources/
│       ├── application.properties
│       ├── templates/
│       │   ├── login.html
│       │   ├── projects/
│       │   │   ├── form.html
│       │   │   └── list.html
│       │   ├── records/
│       │   │   ├── form.html
│       │   │   └── list.html
│       │   ├── students/
│       │   │   ├── form.html
│       │   │   └── list.html
│       │   └── users/
│       │       ├── form.html
│       │       └── list.html
│       └── static/                        # 静的アセット置き場（未使用）
└── target/                                # ビルド生成物
```

---

## REST API

### 学生 API

| メソッド | パス             | 内容                 |
|----------|------------------|----------------------|
| GET      | `/students`      | 学生一覧取得         |
| GET      | `/students/{id}` | ID 指定で学生取得    |
| POST     | `/students`      | 学生新規登録         |
| PUT      | `/students/{id}` | 学生情報更新         |
| DELETE   | `/students/{id}` | 学生削除             |

POST/PUT のリクエスト JSON 例:

```json
{
  "name": "山田太郎",
  "email": "taro@example.com",
  "phone": "090-0000-0000",
  "className": "Java-01"
}
```

### 成績 API

| メソッド | パス                                       | 内容                         |
|----------|--------------------------------------------|------------------------------|
| GET      | `/students/{studentId}/records`            | 指定学生の成績一覧取得       |
| GET      | `/students/{studentId}/records/{recordId}` | 成績詳細取得                 |
| POST     | `/students/{studentId}/records`            | 成績登録（ボディで student を関連付け） |
| PUT      | `/students/{studentId}/records/{recordId}` | 成績更新                     |
| DELETE   | `/students/{studentId}/records/{recordId}` | 成績削除                     |

成績登録時は `Record` の JSON に `student`（少なくとも `studentId`）フィールドを含めて学生と紐づけてください。

---

### 案件（Project） UI/REST

- 一覧: `/projects`
- 新規作成: `/projects/new`
- 編集: `/projects/{id}/edit`
- 削除: POST `/projects/{id}/delete`

現状は Web 画面による CRUD を提供しています。REST API は未実装ですが、`ProjectService` を利用して容易に拡張可能です。

---

## Salesforce との概念対応

| アプリ機能               | Java / PostgreSQL テーブル | Salesforce の概念             | 説明                                               |
|--------------------------|----------------------------|--------------------------------|----------------------------------------------------|
| 生徒（スクール生）管理   | `students`                 | Contact（取引先責任者）       | 個人顧客に相当。名前・連絡先・所属クラスを保持     |
| 成績（級認定・記録会）   | `records`                  | Custom Object（カスタム）     | 生徒と紐づく試験結果・級認定情報                   |
| 案件（自治体新規事業）   | `projects`                 | Opportunity（商談）           | 自治体案件の進捗や金額、ステータスを保持          |
| 案件タスク管理           | `tasks`                    | Task（タスク）                 | 案件に紐づく担当者・期限・進捗を管理              |
| 行動履歴（打合せ・活動） | `activities`               | Event（イベント）             | 打ち合わせや訪問などの活動ログ                     |
| 管理者ログイン           | `users`                    | User（ユーザ）                 | システムを操作する管理者・スタッフ情報            |

---

## 将来のデータモデル案

```
+-----------------+            +-----------------+
|   students      |            |    records      |
+-----------------+            +-----------------+
| student_id (PK) | 1        ∞ | record_id (PK)  |
| name            |------------| student_id (FK) |
| email           |            | test_date       |
| phone           |            | level           |
| class_name      |            | score           |
+-----------------+            +-----------------+

+-----------------+            +-----------------+            +-----------------+
|   projects      | 1        ∞ |     tasks       | 1        ∞ |   activities    |
+-----------------+------------+-----------------+------------+-----------------+
| project_id (PK) |            | task_id (PK)    |            | activity_id (PK)|
| name            |            | project_id (FK) |            | task_id (FK)    |
| client_name     |            | assignee        |            | description     |
| status          |            | due_date        |            | activity_date   |
| start_date      |            | status          |            +-----------------+
| end_date        |            +-----------------+
+-----------------+

+-----------------+
|     users       |
+-----------------+
| user_id (PK)    |
| username        |
| password        |
| role            |
+-----------------+
```

---

## 開発ステップ案

1. **要件定義**: 生徒管理モジュールと案件管理モジュールに分けた機能整理
2. **DB 設計**: 上記テーブルを前提に ER 図を作成し、スキーマ詳細を詰める
3. **最小機能の実装**: 学生 CRUD と案件 CRUD を優先してリリース
4. **拡張機能**: 成績のグラフ化、案件進捗ダッシュボード、タスク/活動の UI 整備
5. **成果物整理**: GitHub、画面キャプチャ、ER 図、画面遷移図でまとめる

---

## ドキュメント

- `docs/er-diagram.md` : 実装済み・拡張予定テーブルを網羅した ER 図とカラム定義。
- `docs/ui-design.md` : 学生/成績モジュールおよび案件モジュールの画面構成・遷移案。
- `docs/entity-migration-plan.md` : 新規エンティティ雛形と PostgreSQL マイグレーション SQL。

---

## 画面アクセス

- 学生一覧: `http://localhost:8080/students/list`
- 学生登録フォーム: `http://localhost:8080/students/new`
- 成績一覧（学生 ID 指定）: `http://localhost:8080/students/{studentId}/records/list`
- 成績登録フォーム: `http://localhost:8080/students/{studentId}/records/new`
- 案件一覧: `http://localhost:8080/projects`
- ユーザー一覧（管理者のみ）: `http://localhost:8080/admin/users`

フォーム送信後は一覧にリダイレクトされます。

---

## 事前準備

- Java 17 のインストール
- PostgreSQL（`training_crm` データベースを作成）
- `src/main/resources/application.properties` で接続設定を確認／変更
  - 既定値: `jdbc:postgresql://localhost:5432/training_crm`
  - ユーザー: `postgres`
  - パスワード: `0727`

---

## 起動方法

```bash
# プロジェクトルートで実行
./mvnw spring-boot:run
```

起動後は `http://localhost:8080` にアクセスしてください。既に 8080 番ポートを使用している場合は、該当プロセスを停止するか `application.properties` に `server.port` を指定して別ポートで起動します。

---

## ログイン情報（サンプル）

- ユーザー名: `admin`
- パスワード: `admin123`

初回起動時に `DataInitializer` が自動作成します。パスワードは `BCrypt` で保存されるため、運用時は速やかに変更してください。

---

## 開発メモ

- ビルド: `./mvnw clean package`
- テスト: 現状 `src/test/java` は未作成（整備予定）
- Lombok を利用しているため、IDE で注釈処理を有効化してください。
- `SecurityConfig` で CSRF を無効化し全リクエストを許可しています。認証導入時は適宜設定を見直します。

---

## 今後の TODO

- DTO 層と入力バリデーションの追加
- Record API の studentId 受け渡し改善とテストケース整備
- projects / tasks / activities / users ドメインのエンティティ・API 実装
- 成績・案件ダッシュボードの可視化（Thymeleaf + グラフライブラリ検討）
- ユーザ管理と認証/認可の本格導入（Spring Security）
