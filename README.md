# Training CRM

職業訓練で作成した Spring Boot 学習用プロジェクトです。  
学生（Student）の登録・一覧・更新・削除と、学習成績（Record）の管理を行うシンプルな CRM 風アプリケーションで、REST API と Thymeleaf ベースの画面をどちらも備えています。

---

## 主な機能

- 学生情報（名前・メール・電話・クラス）の CRUD
- 学生と成績レコード（Record）の 1:N 関連管理
- `/students` および `/{studentId}/records` を軸にした REST API
- Thymeleaf テンプレートによる学生・成績の一覧／登録／編集 UI
- Spring Security 設定済み（現在は全リクエスト許可）

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
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── StudentController.java      # 学生 REST API
│   │   │   ├── RecordController.java       # 成績 REST API
│   │   │   ├── StudentWebController.java   # 学生画面
│   │   │   └── RecordWebController.java    # 成績画面
│   │   ├── entity/
│   │   │   ├── Student.java
│   │   │   └── Record.java
│   │   ├── repository/
│   │   │   ├── StudentRepository.java
│   │   │   └── RecordRepository.java
│   │   └── service/
│   │       ├── StudentService.java
│   │       └── RecordService.java
│   └── main/resources/
│       ├── application.properties
│       ├── templates/
│       │   ├── students/
│       │   │   ├── list.html
│       │   │   └── form.html
│       │   └── records/
│       │       ├── list.html
│       │       └── form.html
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

## 画面アクセス

- 学生一覧: `http://localhost:8080/students/list`
- 学生登録フォーム: `http://localhost:8080/students/new`
- 成績一覧（学生 ID 指定）: `http://localhost:8080/students/{studentId}/records/list`
- 成績登録フォーム: `http://localhost:8080/students/{studentId}/records/new`

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

## 開発メモ

- ビルド: `./mvnw clean package`
- テスト: 現状 `src/test/java` は未作成（整備予定）
- Lombok を利用しているため、IDE で注釈処理を有効化してください。
- `SecurityConfig` で CSRF を無効化し全リクエストを許可しています。認証導入時は適宜設定を見直します。

---

## 今後の TODO

- DTO 層と入力バリデーションの追加
- 成績 Record の REST API／画面ロジックの改善（studentId の取り扱いなど）
- テストコード整備（ユニット・統合）
- Thymeleaf テンプレートのレイアウト／スタイル改善
