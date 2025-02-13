# QPBE-Library-Task

# 📚 온라인 도서 관리 시스템

이 프로젝트는 **온라인 도서 대출 및 관리 시스템**으로, **Spring Boot 기반의 REST API**를 제공합니다.  
JWT 기반 인증 및 Swagger API 문서를 포함하며, 캐싱 및 CI/CD도 적용되었습니다.

---

## 🚀 주요 기능

### ✅ **사용자 관리**
- 회원가입 (`POST /api/users`)
- 로그인 및 JWT 토큰 발급 (`POST /api/users/tokens`)
- 사용자 목록 조회 (`GET /api/users`)
- 로그아웃 (`DELETE /api/users/token`)

### ✅ **도서 관리**
- 도서 등록 (`POST /api/books`)
- 도서 조회 (전체 목록, 개별 조회, 태그 기반 필터링) (`GET /api/books`)
- 도서 삭제 (`DELETE /api/books/{id}`)
- 도서 수정 (`PATCH /api/books/{id}`)
- 도서 태그 추가 및 조회 (`POST /api/books/{id}/tag`, `GET /api/books/{id}/tag`)

### ✅ **도서 대출 시스템**
- 도서 대출 (`POST /api/loans`)
- 도서 반납 (`PATCH /api/loans/{id}`)
- 대출 여부 확인 (`GET /api/loans/{id}`)

### ✅ **기타**
- **Swagger API 문서 제공** (`/swagger-ui/index.html`)
- **JWT 기반 인증 적용**
- **캐싱 적용 (Redis)**
- **CI/CD 자동화 구축**
- **모니터링 시스템 추가 (Prometheus, Grafana)**

---

## 💻 프로젝트 실행 방법

### **1️⃣ 필수 요구 사항**
- **Java 17 이상**
- **Gradle 8.x 이상**
- **MySQL (또는 Docker)**
- **환경 변수 설정 (`.env` 또는 `application.yml`)**

---

### **2️⃣ 프로젝트 클론**
```bash
git clone https://github.com/your-repo.git
cd your-repo
```

---

### **3️⃣ 환경 설정 (`application.yml` 또는 `.env` 설정)**

#### **🔹 `application.yml` 사용**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_db
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: update
```

#### **🔹 `.env` 파일 사용**
```bash
DB_URL=jdbc:mysql://localhost:3306/book_db
DB_USERNAME=root
DB_PASSWORD=password
```

---

### **4️⃣ MySQL 실행 및 테이블 생성**
```bash
docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=book_db -p 3306:3306 -d mysql:latest
```

---

### **5️⃣ 프로젝트 실행**
```bash
./gradlew clean build
./gradlew bootRun
```

📌 **Swagger API 문서 확인:**  
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 🔥 테스트 실행 방법

📌 **Mock을 사용한 테스트 실행**
```bash
./gradlew test
```

📌 **실제 DB를 사용한 통합 테스트**
```bash
./gradlew integrationTest
```

---

## 🔧 **기술 스택**

✅ **Backend**
- Java 17  
- Spring Boot 3.2.2  
- Spring Security & JWT  
- Spring Data JPA  
- MySQL  
- Redis (캐싱)  

✅ **DevOps & Infra**
- Docker  
- GitHub Actions (CI/CD)  
- Prometheus & Grafana (모니터링)  

---

## 📜 **API 명세서**

📌 **Swagger 기반 API 문서 제공**
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  
- **OpenAPI YAML:** [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)  

---

## 📌 **기능 설명 및 예제**

✅ **회원가입 예제 (`POST /api/users`)**
```json
{
  "username": "testuser",
  "password": "Test123!"
}
```

✅ **로그인 예제 (`POST /api/users/tokens`)**
```json
{
  "username": "testuser",
  "password": "Test123!"
}
```
응답:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

✅ **도서 대출 예제 (`POST /api/loans`)**
```json
{
  "bookId": 1,
  "userId": 2
}
```

✅ **도서 반납 예제 (`PATCH /api/loans/1`)**
```json
{
  "message": "반납되었습니다."
}
```
