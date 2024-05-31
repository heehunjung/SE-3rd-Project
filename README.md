#### 소프트웨어공학 프로젝트 - 금융 플랫폼 구현
## 1. Node.js 설치
Node.js 공식 사이트에서 최신 LTS 버전(현재 Node.js 18.x)을 다운로드하여 설치합니다.
### Node.js 및 npm 버전 확인
설치가 완료되면 명령 프롬프트를 열고 다음 명령어를 입력하여 버전을 확인합니다.
```sh
node -v
npm -v
```
Node.js와 npm 버전이 출력되면 설치가 정상적으로 완료된 것입니다.
## 2. 프로젝트 실행
### 프론트엔드 실행
1. 프로젝트 디렉토리로 이동:
```sh
cd src/main/frontend
```
2. 필요한 패키지 설치:
```sh
npm install
```
3. 개발 서버 시작:
```sh
npm start
```
### 백엔드 실행 (Spring Boot)
1. MySQL 설정:
MySQL 서버를 실행하고 picture_board 데이터베이스를 생성합니다:
```sql
CREATE DATABASE picture_board;
```
2. Spring Boot 애플리케이션 실행

```
서버가 시작되며, 브라우저에서 http://localhost:3000으로 접속하여 애플리케이션을 확인할 수 있습니다.
```

