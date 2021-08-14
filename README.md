# Springboot-JWT-Server
1. Basic Knowledge related to Web Communication (Session, TCP, CIA, RSA, JWT)
2. Source Code to create JWT Server

-----------------

## Contents
1. [Using](#using)
2. [Basic Knowledge related to Web Communication](#basic-knowledge-related-to-web-communication)
3. [Concept of JWT](#concept-of-jwt)
4. [Http Basic과 Bearer](#http-basic과-bearer)
5. [Security Filter Chain보다 빠른 필터](#security-filter-chain보다-빠른-필터)
6. [프로젝트와 관련된 Security Filter](#프로젝트와-관련된-security-filter)
7. [Licence](#license)

-----------------

## Using
1. **BackEnd** - Java(JDK 1.8), MySQL(v8.0.25), Spring Boot(2.3.12.RELEASE), JPA
2. **Library&API** - Spring Security, Lombok, **java JWT**
    - JWT Library : JWT 토큰을 생성해주는 라이브러리
3. **IDE** - STS (Spring Tool Suite 3.9.12.RELEASE), MySQL Workbench 8.0 CE, Postman

-----------------

## Basic Knowledge related to Web Communication
1. **Session ID 개념**
    - **Session ID 부여 과정**
        1. 사용자가 Web Browser에서 깃허브 서버에 https://github.com/ (GET 방식)을 요청
        2. 서버는 해당 주소에 맞는 Controller 메소드를 찾고 (이때 **Http Header**를 달아서) 메인페이지에 맞는 .html 파일을 리턴
        3. **Header에 Cookie**라는 것을 만든다.
            - **Cookie : 웹 브라우저의 저장 영역, 세션 ID를 Cookie에 담아서 보낸다.**
            - **세션 ID는 최초 요청시에 만들어 진다.**
            - **최초 요청이 아닐 경우 요청에 세션 ID를 헤더에 담아서 요청한다.**
            - **서버는 세션 ID 목록을 가지고 있다. 유효한 요청인지 확인할 수 있다.**
    - **로그인 요청** (**인증**)에 자주 사용된다.
    - **로그인 요청 과정과 이후 요청 순서**
        1. 클라이언트가 **최초 서버에 접속** (Request)
        2. **서버쪽 세션 ID 목록에 새로운 세션 ID를 생성** (세션 ID와 **관련된 추가 저장 공간도 생성**)
        3. **해당 세션 ID를 함께 Response**
        4. **클라이언트 Cookie에 세션 ID 저장**
        5. 클라이언트가 서버에 **로그인 요청**
        6. **서버에서 받은 ID와 PASSWORD를 가지고 DB 조회**
        7. **정상**일 경우, **세션 목록에서 세션 ID와 관련되어 추가로 생성된 공간에 DB에서 가져온 유저 정보를 저장**
        8. 메인 페이지 (.html) 리턴
        9. 클라이언트가 유저 정보를 **요청하면 서버가 세션이 있는지 확인**
        10. **세션 ID를 확인해서 유저 정보가 있으면 로그인을 한 사람이기 때문에 DB에 응답을 받아서 클라이언트로 돌려준다.**
        11. 위 과정 반복
    - **세션의 단점**
        1. 클라이언트가 서버에 Request할 때 서버에서 Response 해주는데 동접자 수가 늘어나면 기다려야 한다.
        2. 동접자 수를 처리 가능 사용자 수로 나눠서 그만큼 **서버를 만든다.** (**Load Balancing**)
        3. 같은 서버에 계속 요청을 할 경우에는 세션 ID를 서버에서 가지고 있기 때문에 문제가 되지 않지만, **Load Balancing을 통해 다른 서버로 들어갈 경우 세션이 있어도 최초 요청으로 인식하게 된다.**
    - **해결 방법**
        1. 최초 요청은 무조건 **Sticky Session**으로 처리
        2. 세션 ID 목록을 **각 서버에 복제** (**중복**)
        3. 세션값을 **DB에 넣고 공유**해서 사용 (HDD 사용 -> **IO 발생**, 느려짐)
        4. **메모리 공유 서버** (IO 발생 X, HDD가 아니라 **RAM**만 사용), 대표적으로 Redis, 레디스
- **Session 삭제**
    1. **서버 쪽에서 Session 삭제** (세션 ID 목록에서 해당 ID를 지운다.)
    2. **사용자가 브라우저를 전부 종료** (들고있는 세션 ID가 삭제) -> 서버에 요청하면 새로운 ID를 부여받음. -> **서버 세션 ID 목록에 있던 과거 ID는 특정 시간 이후에 사라진다.**
    3. **특정 시간이 지나서 서버 세션 ID 목록에서 사라진다.** 
2. **TCP**
    - **웹은 TCP 통신을 한다.**
    - **OSI 7 Layer**
        1. **물리** 계층 : 실제 **케이블**
        2. **데이터 링크** 계층 : IP로 찾아간 **내부망** (LAN)에서 목적지를 찾음
        3. **네트워크** 계층 : **IP** 결정
        4. **트랜스포트** 계층 : TCP / UDP (**WAN**)
            - **TCP** : **신뢰성** (ACK 사용, 느림)
            - **UDP** : **비신뢰성** (전화, 스트리밍->사람이 이해, 추측할 수 있는 통신)
        5. **세션 계층** : **인증** 체크
        6. **프리젠테이션 계층** : **암호화, 압축**
        7. **응용 계층** : **메시지**
3. **CIA**
    - 보안의 3요소 : **기밀성** (Confidentiality), **무결성** (Integrity), **가용성** (Availability)
    - 인증 문제 (무결성), 암호화 문제 (기밀성)가 존재한다.
4. **RSA 암호화**
    - **RSA** : Public Key (공개키), Private Key (개인키)
    - **암호화 과정** (**공개키로 암호화**)
        1. A 공개키, 개인키 / B 공개키, 개인키 존재
        2. A가 B에게 데이터를 보낼 때 B 공개키로 암호화해서 보냄
        3. B가 데이터를 받았을 때 B 개인키로 암호화를 풀 수 있음
        4. B가 A에게 데이터를 보낼 때 A 공개키로 암호화해서 보냄
        5. A는 받은 데이터를 A 개인키로 열어볼 수 있음.
    - **전자 문서 서명** (인증, **개인키로 암호화**)
        1. A가 A 개인키로 암호화해서 B에게 보냄
        2. B는 A 공개키를 이용해서 암호를 풀 수 있는 것을 보고 확실히 A가 보낸 데이터라는 것을 알 수 있다.
    - **정리**
        1. A가 B에게 데이터를 보낼 때 B의 공개키로 먼저 암호화하고 A의 개인키로 암호화하여 전송
        2. B는 받은 메시지를 A의 공개키로 복호화 (풀리면 **인증 성공**)
        3. 인증이 성공하면 한번 더 B의 개인키로 복호화 (**암호화 성공**)

-----------------

## Concept of JWT
1. **JWT** (**JSON Web Token**)
    - **정보를 JSON 객체로 안전하게 전송하기 위한 방식**
    - **서명에 사용** (인증, 신뢰)
    - **데이터 암호화도 의미가 있지만 서명된 토큰에 중점을 둔다.**
2. **JWT 구조**
    1. 일반적으로 xxxxx.yyyyy.zzzzz 형태 (Header.Payload.Signature)
    2. **Header** : 토큰의 타입, 암호화 방식
        ```json
        {
            "alg": "HS256",
            "typ": "JWT"
        }
        ```
    3. **Payload** : 데이터, 예를 들어 {"username": "ssar"}
        1. **Registered Claim** (클레임은 Payload에 담는 정보의 한 조각으로 name, value 한쌍을 의미) : 서비스에 필요한 정보들이 아닌, 토큰에 대한 정보로 모두 Optional
            - iss : 토큰 발급자 (issuer)
            - sub : 토큰 제목 (subject)
            - aud : 토큰 대상자 (audience)
            - exp : 토큰의 만료시간 (expiration)
            - nbf : Not Before
            - iat : 토큰이 발급된 시간 (issued at)
            - jti : JWT의 고유 식별자
        2. **Public Claim** : 충돌이 방지된 이름을 가지고 있고, 충돌을 방지하기 위해서 클레임 이름을 URI 형식으로 저장
        3. **Private Calim** : 클라이언트와 서버 사이에 협의 하에 사용되는 클레임 이름
    4. **Signature** : (Header + Payload + 서버만 알고있는 Secret 각각)을 암호화
3. **JWT의 주요 이점**
    1. 사용자 인증에 필요한 모든 정보는 토큰 자체에 포함하기 때문에 **별도의 인증 저장소가 필요 없다.**
    2. 서버의 확장성이나 쿠키를 사용하는 방식보다 안전하다.
4. **로그인 과정**
    1. **클라이언트가 서버에 로그인 시도**
    2. **로그인이 올바른 시도일 경우 JWT를 만들어서 클라이언트로 보내준다.**
    3. 웹브라우저 (클라이언트)의 로컬 스토리지에 JWT 저장
    4. **클라이언트가 다시 요청을 할 때 JWT을 함께 전송**
    5. **서버에서 JWT을 받으면** Header+Payload+Secret을 **암호화 해본다. 그 결과를 전송 받은 JWT와 비교해서 같으면 인증이 되었다고 판단한다**
    6. **서버 키만 알고 있으면 Load Balancing에 의해 다른 서버에 들어가도 새로운 JWT 토큰을 생성하지않고, JWT 비교를 통해 인증이 가능해진다.**
    
-----------------

## Http Basic과 Bearer
1. **Http Basic 방식**
    - **요청 Headers 안에 있는 Authorization 에 ID, PW를 담아서 전송**
    - 서버 확장성은 좋으나 ID, PW 가 노출될 가능성이 있다.
2. **Bearer 방식**
    - **Authorization 에 Token을 담아서 전송**
    - Token 이 노출될 가능성은 있으나 ID, PW 가 노출되지는 않는다.
    - Token 은 ID, PW 를 기반으로 만들어진다. 유효 시간이 존재한다.
    - 이때 사용하는 것이 JWT 토큰
    
-----------------

## Security Filter Chain보다 빠른 필터
- Security Filter Chain은 우리가 추가한 필터(MyFilter1, MyFilter2)보다 먼저 동작한다.
- 우리의 필터를 먼저 동작하게 하기 위해서는 **addFilterBefore** 를 사용한다.
    ```java
        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
    ```
- **Security Filter Chain의 첫번째 필터** : **SecurityContextPersistenceFilter**
- MyFilter3는 Security Filter Chain 보다 먼저 실행된다.

-----------------

## 프로젝트와 관련된 Security Filter
1. **BasicAuthenticationFilter** : **권한이나 인증이 필요한 특정 주소를 요청했을 때 이 필터에 걸린다.** 권한이나 인증이 필요한 주소가 아니라면 이 필터를 무시한다.
    ```java
    public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
        public JwtAuthorizationFilter (AuthenticationManager authenticationManager, UserRepository userRepository){ }

        protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException{ }
    }
    ```
2. **UsernamePasswordAuthenticationFilter** : /login 요청해서 username, password를 전송하면 (post) UsernamePasswordAuthenticationFilter가 동작한다.
    ```java
    public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter
    ```
    1. **attemptAuthentication 함수** : /login 요청을 하면 로그인 시도를 위해서 실행되는 함수
        ```java
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException
        ```
    2. **successfulAuthentication 함수** : attemptAuthentication 실행 후 인증이 정상적으로 되었으면 실행
        ```java
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException
        ```
-----------------

## License
- **Source Code** based on [codingspecialist'lecture](https://github.com/codingspecialist)