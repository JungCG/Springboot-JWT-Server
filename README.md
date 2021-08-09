# springboot-jwt
1. Basic Knowledge related to Web Communication (Session, TCP, CIA, RSA)
2. Source Code to create JWT Server

-----------------

## Contents
1. [Using](#using)
2. [Basic Knowledge related to Web Communication](#basic-knowledge-related-to-web-communication)
3. [JWT](#jwt)
4. [Licence](#license)

-----------------

## Basic Knowledge related to Web Communication
1. Session ID 개념
    - Session ID 부여 과정
        1. 유저가 Web Browser에서 네이버 서버에 www.naver.com (GET 방식)을 요청
        2. 서버는 해당 주소에 맞는 Controller의 메소드를 찾고 (이때 http header를 달아서) 메인페이지에 맞는 .html 파일을 리턴
        3. header에 쿠키라는 것을 만든다.
            - 쿠키 : 웹 브라우저의 저장 영역, 세션 ID를 쿠키에 담아서 보낸다.
            - 세션 ID는 최초 요청시에 만들어 진다.
            - 최초 요청이 아닐 경우 요청에 세션 ID를 헤더에 담아서 요청한다.
            - 세션 ID의 역할 : 
            - 서버는 세션 ID 목록을 가지고 있다. 유효한 요청인지 확인할 수 있다.
    - 로그인 요청 (인증)에 자주 사용된다.
    - 로그인 요청 과정 + 이후 요청
        1. 클라이언트가 최초 서버에 접속 (Request)
        2. 서버쪽 세션 ID 목록에 새로운 세션 ID를 생성 (세션 ID와 관련된 추가 저장 공간도 생성)
        3. 해당 세션 ID를 함께 Response
        4. 클라이언트 쿠키에 세션 ID 저장
        5. 클라이언트가 서버에 로그인 요청
        6. 서버에서 받은 ID와 PASSWORD를 가지고 DB 조회
        7. 정상일 경우, 세션 목록에서 세션 ID와 관련되어 추가로 생성된 공간에 DB에서 가져온 유저 정보를 저장
        8. 메인 페이지 (.html) 리턴
        9. 클라이언트가 유저 정보를 요청하면 서버가 세션이 있는지 확인
        10. 세션 ID를 확인해서 유저 정보가 있으면 로그인을 한 사람이기 때문에 DB에 응답을 받아서 클라이언트로 돌려준다.
        11. 위 과정 반복
    - 단점 : 클라이언트가 서버에 Request할 때 서버에서 Response 해주는데 동접자 수가 늘어나면 기다려야 한다. -> 우리 서버가 동접자 100명을 처리할 수 있다면 그 수만큼 서버를 만들어야 한다. (Load Balancing) -> 같은 서버에 계속 요청을 할 경우에는 세션 ID를 서버에서 가지고 있기 때문에 문제가 되지 않지만, Load Balancing을 통해 다른 서버로 들어갈 경우 최초 요청으로 인식하게 됨. -> 최초 요청은 무조건 스티키 서버로 가게끔 처리 또는 세션 ID 목록을 각 서버에 복제 또는 세션값을 DB에 넣고 공유해서 사용 (HDD 사용 -> IO 발생, 느려짐) -> 메모리 공유 서버 (IO 발생 X, HDD가 아니라 RAM만 사용), 대표적으로 Redis, 레디스
- Session 삭제 방법
    1. 서버 쪽에서 Session 삭제 (세션 ID 목록에서 해당 ID를 지운다.)
    2. 사용자가 브라우저를 전부 종료 (들고있는 세션 ID가 삭제) -> 서버에 요청하면 새로운 ID를 부여받음. -> 서버 세션 ID 목록에 있던 과거 ID는 특정 시간 이후에 사라진다.
    3. 특정 시간이 지나서 서버 세션 ID 목록에서 사라진다. 
2. TCP
    - 웹은 TCP 통신을 한다.
    - OSI 7 Layer
        1. 물리 계층 : 실제 케이블
        2. 데이터 링크 계층 : IP로 찾아간 내부망 (LAN)에서 목적지를 찾음
        3. 네트워크 계층 : IP 결정
        4. 트랜스포트 계층 : TCP/UDP (WAN)
            - TCP : 신뢰성 (ACK 사용, 느림)
            - UDP : 비신뢰성, (전화, 스트리밍->사람이 이해, 추측할 수 있는 통신)
        5. 세션 계층 : 인증 체크
        6. 프리젠테이션 계층 : 암호화, 압축
        7. 응용 계층 : 메시지
3. CIA
    - 기밀성 (Confidentiality), 무결성 (Integrity), 가용성 (Availability)
    - 가용성 유지 방법 : 데이터 
    - 무결성 유지 방법 : 
    - 기밀성 유지 방법 : 암호화
        1. 열쇠 전달 문제
        2. 문서를 보낸 사람, 인증 문제

4. RSA 암호화
    - RSA : Public Key (공개키), Private Key (개인키)
    - 암호화 과정 (공개키로 암호화)
        1. A 공개키, 개인키 / B 공개키, 개인키
        2. A가 B에게 데이터를 보낼 때 B 공개키로 암호화해서 보냄
        3. B가 데이터를 받았을 때 B 개인키로 암호화를 풀 수 있음
        4. B가 A에게 데이터를 보낼 때 A 공개키로 암호화해서 보냄
        5. A는 받은 데이터를 A 개인키로 열어볼 수 있음.
    - 전자 문서 서명 (인증, 개인키로 암호화)
        1. A가 A 개인키로 암호화해서 B에게 보냄
        2. B는 A 공개키를 이용해서 암호를 풀 수 있는 것을 보고 확실히 A가 보낸 데이터라는 것을 알 수 있다.
    - 정리
        1. A가 B에게 데이터를 보낼 때 B의 공개키로 먼저 암호화하고 A의 개인키로 암호화하여 전송
        2. B는 받은 메시지를 A의 공개키로 복호화 (풀리면 인증 성공)
        3. 인증이 성공하면 한번 더 B의 개인키로 복호화 (암호화 성공)
    
-----------------

## JWT
- JWT (JSON Web Token)

-----------------

## License
- **Source Code** based on [codingspecialist'lecture](https://github.com/codingspecialist)