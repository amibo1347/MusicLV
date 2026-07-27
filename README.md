# MusicLV — 악기 쇼핑몰 · 공연 예매

밴드 악기를 파는 쇼핑몰. 스크롤 연출이 있는 랜딩 페이지에서 상품과 공연 예매로 이어진다.

**운영 주소:** https://musiclv.p-e.kr

## 이 프로젝트의 출발점

부트캠프 프론트엔드 수업에서 만든 **스크롤 인터랙션 랜딩 페이지**(Apple 제품 페이지를 참고해
`canvas` 이미지 시퀀스와 `sticky` 로 스크롤 연출을 구현한 과제)를 그대로 버리지 않고,
**그 랜딩 위에 실제로 물건을 파는 백엔드를 붙여 확장한 프로젝트다.**

- 사과 이미지 시퀀스 → **드럼 연주 451장 시퀀스**로 교체하고 배경 영상을 추가
- 정적 HTML 이던 랜딩을 Thymeleaf 템플릿으로 옮겨, 연출 아래에 상품 · 공연 영역이 이어지도록 재구성
- 그 아래로 회원 · 장바구니 · 주문 · 예매 · 관리자까지 Spring Boot 로 구현

즉 **프론트 과제 → 풀스택 서비스**로 이어붙인 결과물이다.

## 스택

Spring Boot 3.5.3 / Java 21 / Gradle 8.14.5 / Thymeleaf / Spring Security / JPA / MySQL(MariaDB)

## 기능

- 회원가입 · 로그인 (BCrypt, `USER` / `ADMIN`)
- 상품 목록 · 상세 · 검색(상품명/브랜드) · 카테고리 · 정렬 · 페이징
- 장바구니 담기 / 수량변경 / 삭제
- 주문 (재고 차감, 주문 시점 가격 보존) · 주문 취소 (재고 복구)
- **비회원 주문** — 로그인 없이 주문하고, 주문번호 + 연락처로 조회
- **공연 예매** — 공연 목록 · 상세 · 관람일/매수 선택 예매 · 예매 취소 (본인 예매만 조회 가능)
- 마이페이지: 최근 주문 · 최근 예매 요약과 전체 내역
- 관리자: 상품 CRUD(이미지 업로드), 공연 CRUD, 주문 상태 관리, 예매 상태 관리

## 로컬 실행

MySQL에 DB와 계정을 먼저 만든다.

```sql
CREATE DATABASE musiclv DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'musiclv'@'localhost' IDENTIFIED BY 'musiclv1234';
GRANT ALL PRIVILEGES ON musiclv.* TO 'musiclv'@'localhost';
```

```bash
gradlew bootRun
```

DB 없이 화면만 보려면 H2 프로파일을 쓴다. 메모리 DB라 종료하면 데이터가 사라진다.

```bash
gradlew bootRun --args='--spring.profiles.active=h2'
```

첫 구동 시 샘플 상품 20건과 관리자 계정(`admin@musiclv.com` / `admin1234`)이 생성된다.

## 미디어 파일

랜딩 페이지의 드럼 스크롤 시퀀스(451장)와 배경 영상은 **저장소에 없다.** 용량이 커서
`.gitignore` 로 제외하고 배포 서버에 직접 둔다.

```
assets/
├── frames/   drum_0.jpg ~ drum_450.jpg   (1280x720, 52MB)
└── video/    hanlolo.mp4                 (8.5MB)
```

`musiclv.media-dir` 설정이 가리키는 경로를 `/media/**` 로 서빙한다(`WebConfig`).
로컬은 `./assets`, 서버는 `/opt/musiclv/assets`.

## 설정 (환경변수)

| 변수 | 기본값 | 설명 |
|---|---|---|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | `localhost` / `3306` / `musiclv` | DB 접속 |
| `DB_USER` / `DB_PASSWORD` | `musiclv` / `musiclv1234` | DB 계정 |
| `SERVER_PORT` | `8080` | 서버 포트 (운영은 8081) |
| `MEDIA_DIR` | `./assets` | 미디어 디렉터리 |
| `JPA_DDL_AUTO` | `update` | 운영은 `validate` |
| `THYMELEAF_CACHE` | `false` | 운영은 `true` |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | `admin@musiclv.com` / `admin1234` | **운영에서는 반드시 지정** |

## 배포

`main` 브랜치에 푸시하면 GitHub Actions가 서버에 SSH로 접속해 `/opt/musiclv/deploy.sh` 를 실행한다.
스크립트는 `git pull → gradlew bootJar → 서비스 재시작` 순으로 동작한다.

```
인터넷 → nginx :443 (musiclv.p-e.kr, Let's Encrypt)
              └→ 127.0.0.1:8081  musiclv.service
                                  ├→ MariaDB 127.0.0.1:3306
                                  └→ /opt/musiclv/assets
```

## 개발 정보

- 기간: 2026.07
- 인원: 1명
- 환경: Windows · VS Code
- 랜딩 페이지의 스크롤 연출은 부트캠프 프론트엔드 과제에서 직접 작성한 코드를 가져와 드럼 시퀀스로 바꾼 것이고,
  그 위에 올린 백엔드(회원 · 상품 · 주문 · 예매 · 관리자)와 화면은 **Claude Code** 와 함께 구현했다.
