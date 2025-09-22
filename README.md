# Training CRM

職業訓練で作成した Spring Boot 学習用プロジェクトです。  
学生（Student）の登録・一覧・更新・削除を行うシンプルな CRM 風アプリケーションで、REST API と Thymeleaf ベースの画面の両方を備えています。

---

## 主な機能

- 学生情報（名前・メール・電話・クラス）の CRUD
- 学生と成績レコード（Record）の 1:N 関連管理
- REST API を通じた JSON ベースの操作
- Thymeleaf テンプレートを用いた簡易 Web UI
- Spring Security 設定済み（現在は全許可）

---

## 技術スタック

- Java 17
- Spring Boot 3.5.6
  - Spring Web / Data JPA / Security / Thymeleaf
- PostgreSQL
- Maven（Maven Wrapper 同梱）
- Lombok

---

## プロジェクト構成
```
training-crm/
├── pom.xml
├── src/
│ ├── main/java/com/example/trainingcrm/
│ │ ├── TrainingCrmApplication.java
│ │ ├── config/SecurityConfig.java
│ │ ├── controller/
│ │ │ ├── StudentController.java # REST API
│ │ │ └── StudentWebController.java # Thymeleaf 画面用
│ │ ├── entity/
│ │ │ ├── Student.java
│ │ │ └── Record.java
│ │ ├── repository/
│ │ │ ├── StudentRepository.java
│ │ │ └── RecordRepository.java
│ │ └── service/StudentService.java
│ └── main/resources/
│ ├── application.properties
│ ├── templates/
│ │ ├── student-list.html
│ │ └── student-form.html
│ └── static/ # 今後のアセット置場
└── target/ # ビルド生成物
```

---

## REST API

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

画面アクセス
一覧: http://localhost:8080/students/list
新規登録フォーム: http://localhost:8080/students/new
保存後は一覧へリダイレクトされます。
事前準備
Java 17
PostgreSQL（training_crm データベースを作成）
プロジェクトルートの application.properties で接続先を設定
（既定値: jdbc:postgresql://localhost:5432/training_crm, ユーザー postgres, パスワード 0727）
起動方法
bash

# 初回は依存解決を含め Maven Wrapper を利用
./mvnw spring-boot:run
起動後は http://localhost:8080 でアクセスできます。

開発メモ
ビルド: ./mvnw clean package
テスト: 現状テストクラス未作成のため、./mvnw test はスキップ予定
Lombok を利用しているため、IDE で注釈処理を有効化してください。
SecurityConfig で全リクエストを許可しており、CSRF も無効化しています。認証を導入する場合はこの設定を変更します。
今後の TODO
DTO 層の整備と入力バリデーション追加
成績 Record の CRUD 画面/API 拡張
テストコード整備（ユニット・統合）
Thymeleaf テンプレートのレイアウト改善
