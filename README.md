
# 📚 온라인 도서 관리 시스템

이 프로젝트는 **온라인 도서 대출 및 관리 시스템**으로, **Spring Boot 기반의 REST API**를 제공합니다.  
JWT 기반 인증, 도서 관리, 대출 기능을 포함하며, **Swagger API 문서, 캐싱, CI/CD**가 적용되었습니다.

GitHub 리포지토리 주소 : https://github.com/dahoon7151/QPBE-Library-Task

---

## 🛠 사용한 기술 스택
### **Backend**
- Spring Boot, Java, JPA, Spring Security
### **Database**
- MySQL, Redis
### **Server / Deployment**
- Docker, Docker-Compose, Github Actions, WSL2(Local)
### **Collaboration & Tools**
- Git/GitHub, Notion, IntelliJ, Apache Jmeter

---

## 🚀 주요 기능

### ✅ **사용자 관리 (User CRUD + JWT + Spring Security)**
- **회원가입** (`POST /api/users`)
- **로그인 및 JWT 토큰 발급** (`POST /api/users/token`)
- **모든 사용자 조회** (`GET /api/users`)
- **특정 사용자 조회** (`GET /api/users/{id}`)
- **토큰 재발급 (Refresh Token 검증 후 Access Token 갱신)** (`POST /api/users/token/refresh`)
- **로그아웃 (Refresh Token 제거)** (`DELETE /api/users/token`)

### ✅ **도서 관리 (Book CRUD)**
- **도서 등록** (`POST /api/books`)
- **도서 목록 조회 (페이지네이션, 정렬 포함)** (`GET /api/books`)
- **특정 도서 조회** (`GET /api/books/{id}`)
- **도서 삭제** (`DELETE /api/books/{id}`)
- **도서 수정** (`PATCH /api/books/{id}`)
- **도서 태그 추가** (`POST /api/books/{id}/tag`)
- **태그별 도서 필터링** (`GET /api/books/tag`)
- **도서 제목 검색** (`GET /api/books/title/{title}`)
- **저자명으로 도서 검색** (`GET /api/books/author/{author}`)

### ✅ **도서 대출 시스템 (Loan CRUD)**
- **도서 대출** (`POST /api/loans`)
- **도서 반납** (`PATCH /api/loans/{id}`)
- **도서 대출 여부 확인** (`GET /api/loans/{id}`)

### ✅ **기타**
- **Swagger API 문서 제공** (`/swagger-ui/index.html`)
- **JWT 기반 인증 적용**
- **캐싱 적용 (Redis)**
- **CI/CD 자동화 구축**


- **모니터링 시스템 추가 (Prometheus, Grafana)** (미적용)

---

## 캐시 적용 전 코드 (`before-cache` 브랜치)

**캐시 적용 전 코드**는 `before-cache` 브랜치에서 확인할 수 있습니다.

```sh
git checkout before-cache
```

캐시 적용 전후의 성능 비교는 **성능 테스트 보고서에서 자세히 확인할 수 있습니다.**

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

  
- Prometheus & Grafana (모니터링)  (미적용)

---

## 📜 **API 명세서**

📌 **Swagger 기반 API 문서 제공**
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  
- **OpenAPI YAML:** [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)  

---
