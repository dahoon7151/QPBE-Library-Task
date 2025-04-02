
# 📚 온라인 도서 관리 시스템

이 프로젝트는 **온라인 도서 대출 및 관리 시스템**으로, **Spring Boot 기반의 REST API**를 제공합니다.  
JWT 기반 인증, 도서 관리, 대출 기능을 포함하며, **Swagger API 문서, 캐싱, CI/CD**가 적용되었습니다.

GitHub 리포지토리 주소 : https://github.com/dahoon7151/QPBE-Library-Task

---

### 🔧 프로젝트에서 맡은 작업

- **백엔드 기능 구현**
    - 사용자 인증 및 JWT 기반 로그인 기능 (Access Token, Refresh Token)
    - 도서 관리 (등록, 조회, 수정, 삭제, 태그 추가 및 태그별 필터링)
    - 도서 대출 관리 (대출 등록, 대출 상태 확인, 반납 처리)
- **캐시 적용**
    - Redis 캐싱 적용 및 성능 최적화
    - 다중 인스턴스 환경에서의 캐시 일관성 유지 (Redis Pub/Sub 활용)
- **CI/CD 구축 및 배포**
    - GitHub Actions 기반 CI/CD 파이프라인 구축
    - Docker 컨테이너 기반 배포 설정

---

## 🛠 사용한 기술 스택

### **Backend**

- Spring Boot, JPA, Spring Security, Java

### **Database**

- MySQL, Redis

### **Server / Deployment**

- GitHub Actions, Docker

### **Collaboration**

- Git/GitHub, Notion, Swagger

### **Tools**

- IntelliJ, Apache JMeter

---

## 📌 초기 기획 및 설계

- **API 설계 및 데이터베이스 모델링** → ERD(Entity-Relationship Diagram) 작성
- **캐싱 전략 설계** → Write-Around + Read Aside 캐싱 적용 및 Redis Pub/Sub을 활용한 다중 인스턴스 환경 대응
- **CI/CD 파이프라인 설계** → CI/CD 전략 문서 작성

---

## 🔧 개발 단계

### 🏗 백엔드 기능 구현

### **사용자 관리**

- **JWT 인증 방식 로그인 구현**
- **Spring Security 필터 적용** → JWT 기반 인증 및 보안 강화
- **회원 관련 기능 구현**: 회원가입, 로그아웃, 토큰 재발급, 회원정보 조회

### **도서 관리**

- 태그 기능을 위한 **BookTag 중간 테이블**을 활용하여 다대다 관계 설정
- **태그별 필터링을 위한 JPA 복합 쿼리 작성**
    - `Book`, `BookTag`, `Tag` 테이블을 조인하여 특정 태그가 포함된 도서 조회
- **데이터 정합성 유지**를 위해 트랜잭션 및 고아 객체 삭제 적용
- **도서 관련 기능 구현**: 도서 등록, 조회(페이지네이션), 상세 조회(ID, 제목, 저자별 검색), 수정, 삭제, 태그 추가 및 필터링

### **대출 관리**

- **Loan 중간 테이블**을 활용하여 `User`와 `Book` 간의 다대다 관계를 설정하고 대출 기록 관리
- 대출 기록을 기반으로 반납 처리 및 연체 검토 기능 구현
- **대출 관련 기능 구현**: 도서 대출, 대출 여부 조회, 반납

### **Redis 기반 캐싱 적용**

- 하이브리드 캐싱 전략 적용 (Write-Around + Redis Pub/Sub 활용)
- **POST, PATCH, DELETE 요청 시 Write-Around 캐싱 적용 (데이터 변경 시 캐시를 갱신하지 않음)**
- **데이터 일관성을 유지하기 위해 Redis Pub/Sub을 활용하여 변경 시 캐시 무효화 후 Read-Aside 방식으로 캐싱 재적용**
- 캐시 데이터의 유효성을 보장하기 위해 **TTL(Time-To-Live) 설정***

**캐시 전략 문서** : 🔗[캐시 전략 문서 - 강다훈](https://www.notion.so/195fb23c68528010aa45ca2912013df2?pvs=21)

---

### 📄 API 명세서 작성

- Swagger 사용

🔗[Swagger API명세 구글드라이브](https://drive.google.com/file/d/1vaHX6R_Cx4EOuPSUVhKDMmD8CBUt_SyS/view?usp=drive_link)

---

## 📈 성능 및 정합성 테스트

**테스트 요약 보고서** : 🔗[캐시 성능 및 정합성 테스트 보고서](https://www.notion.so/19cfb23c685280aaae0cfd77f02442ff?pvs=21)

**성능 테스트 보고서** : 🔗[캐시 미적용](https://drive.google.com/file/d/1I_Zarsl238AgiQPQhfwcnuRED7C2HzPW/view?usp=drive_link) 🔗[캐시 적용](https://drive.google.com/file/d/1pxQv05YplwR9f1aFZEnZD6a6q_RlsONK/view?usp=drive_link)   -> 캐시 적용 이전 커밋의 브랜치 생성 

**정합성 테스트 보고서** : 🔗[캐시 정합성](https://drive.google.com/file/d/1XW4TAhJ4xQVMTonN6sNl43jzvtw6Cftq/view?usp=drive_link)

### **1. 테스트 개요**

- **캐시 적용 전후 API 성능 비교** (응답 시간, 처리량, 정합성).
- **테스트 환경**: Apache JMeter, 도서(2000), 사용자(100), 대출(500), **Redis Read-Aside + Write-Around + Pub/Sub 적용**.

### **2. 테스트 결과**

- **응답 시간 감소**: 캐시 적용 후 평균 응답 시간 단축, 100% 백분위 응답 시간 **70ms → 20ms 이하**.
- **처리량 증가**: TPS(초당 처리량) 상승, **DB 부하 감소**.
- **정합성 보장**: **Pub/Sub을 통한 캐시 무효화 후 Read-Aside로 최신 데이터 반영 성공**.
- **캐시 최적화 필요**: TTL 설정 및 **부하 증가 시 캐시 적중률 유지 여부 추가 검토**.

---

## ☁️ CI/CD 파이프라인 구축 및 배포

**CI/CD 문서** : 🔗[파이프라인 설계 및 구현 - 강다훈](https://www.notion.so/CI-CD-19dfb23c685280f7a751f62fa79ccfa1?pvs=21)

### **1. 개요**

- **GitHub Actions, Docker, Docker-Compose 기반의 CI/CD 구축**.
- 코드 자동 빌드, 테스트, 배포 자동화를 목표로 설계.

### **2. CI (Continuous Integration)**

- **자동 빌드**: GitHub Actions에서 코드 변경 시 **Gradle 기반 빌드 자동화**.
- **테스트 자동화**: **SpringBootTest**를 활용한 단위 및 통합 테스트 수행, 실패 시 배포 중단.
- **테스트 환경 구축**: GitHub Actions에서 **Redis, MySQL 컨테이너 실행** 후 테스트 수행.

### **3. CD (Continuous Deployment)**

- **Docker 기반 배포**: GitHub Actions에서 **Docker 이미지 빌드 및 Docker Hub 푸시**.
- **자동 배포**: **docker-compose를 활용해 MySQL, Redis와 함께 애플리케이션 실행**.
- **헬스 체크**: `/actuator/health` 엔드포인트로 배포 후 서비스 정상 작동 여부 검증.
- **환경 분리**: `.env` 파일을 활용한 **환경별 설정 적용**.

---

## 🛠 이슈 및 트러블슈팅

### **Redis Pub/Sub을 활용한 캐시 일관성 유지**

- **문제:** 다중 인스턴스 환경에서 캐시 일관성 유지 문제 발생
- **해결:** Redis Pub/Sub을 활용하여 데이터 변경 시 캐시 무효화 및 최신 데이터 반영

### **태그별 도서 필터링 최적화 문제**

- **문제:** 태그별 도서 필터링 시 JPA 쿼리 성능 저하 발생
- **해결:** 다중 조건을 처리하기 위해 `JOIN` 및 `WHERE EXISTS`를 활용하여 쿼리 최적화

### **GenericJackson2JsonRedisSerializer 와 RedisSerializer 커스텀**

- **문제:** Redis 캐시 직렬화 시 `GenericJackson2JsonRedisSerializer`로 인해 데이터 불일치 발생
- **해결:** `RedisSerializer`를 커스텀하여 객체 변환 방식 개선 및 데이터 정합성 유지

### **GitHub Actions에서의 통합 테스트 문제**

- **문제:** GitHub Actions에서 MySQL, Redis 컨테이너 설치 후 포트 할당 문제 발생
- **해결:** GitHub Actions에서 `services` 설정을 활용하여 MySQL과 Redis 컨테이너를 올바르게 실행하도록 구성

---

## 🎯 후기

이번 프로젝트를 통해 **백엔드 개발뿐만 아니라 성능 최적화, 캐싱 전략, CI/CD 구축 등 다양한 경험을 쌓을 수 있었습니다.**
특히, **Redis Pub/Sub을 활용한 캐시 일관성 유지**, **JMeter를 활용한 성능 테스트**, **Docker 컨테이너 기반 배포** 등의 경험은 앞으로의 개발에 큰 도움이 될 것이라 생각합니다.

또한, 지금까지는 비교적 여유롭게 시간을 갖고 프로젝트를 진행해왔지만 이번 프로젝트에서는 **제한된 짧은 시간 내에** **핵심 기능을 구현하고 최적화, CI/CD, 모니터링 구축까지 진행하며** 문제 해결 능력을 키울 수 있었습니다.

이번 프로젝트를 진행하며 시간과 열정을 갈아넣은 만큼 매우 급속도로 성장했음을 느꼈고 한단계 더 레벨업했다고 생각합니다.
