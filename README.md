<div id="top"></div>

<div align="center">

# 🚀 Talk Haven </b>

![Image](https://github.com/user-attachments/assets/b3dba348-797e-400b-a877-3b296ddc6d32)
</div>

# 👋 프로젝트 소개

“TALK HAVEN”은 대화(Talk)와 안식처(Haven)의 결합어로, **안전하고 편안한 대화를 나눌 수 있는 공간**이라는 의미를 담고 있습니다.

---
1. 안전한 공간 (Safe Haven)
  - 외부 방해 없이 자유롭게 소통할 수 있는 안전한 공간을 지향합니다.
  - 개인정보 보호, 보안 기능, 익명성 보장을 통해 사용자에게 심리적 안정감을 제공합니다.
2. 편안한 대화 (Comfortable Talk)
  - 부담 없이 진솔한 대화가 가능하도록 설계된 공간입니다.
  - 친근한 UI/UX와 감성적인 디자인으로 사용자들이 편안하게 대화를 이어갈 수 있습니다.
3. 실시간 소통 (Real-Time Communication)
  - STOMP 기반 실시간 메시지 전송으로 끊김 없는 소통을 보장합니다.
  - SSE 실시간 알림을 통해 직관적이고 자연스러운 커뮤니케이션을 지원합니다.
4. 커뮤니티 중심 (Community-Oriented)
  - 소규모 커뮤니티 및 그룹 대화 기능을 통해 사용자들 간의 유대감을 형성할 수 있습니다.
  - 신뢰할 수 있는 대화 공간을 제공하여 온라인에서도 따뜻한 교류가 이루어지도록 합니다
---

# 📌 🏗️ 아키텍처
![Image](https://github.com/user-attachments/assets/6296456a-a063-444d-b8eb-024d052d01d1)

# ✨ 주요 기능

### 🔐 1. 회원 관리

#### ✅ 회원가입 및 로그인 (JWT 기반 인증)

#### ✅ 회원 목록 조회

#### ✅ Mypage에서 내 채팅 목록 조회

### 💬 2. 채팅 기능

#### ✅ 1:1 채팅

  - 회원 목록에서 원하는 사용자와 1:1 채팅 가능

  - 두 사람만 참여 가능하도록 제한

#### ✅ 그룹 채팅 (단체 채팅방)

  - 누구든 채팅방 개설 가능

  - 채팅방 리스트에서 원하는 방 자유롭게 참여 가능

  - 모든 사용자가 퇴장하면 채팅방 자동 삭제

#### ✅ 채팅 메시지 관리

  - 실시간 채팅 메시지 전송

  - 채팅 메시지 저장 및 조회

  - 이전 메시지 내역 불러오기

  - 읽지 않은 메시지 개수 표시

#### ✅ 채팅방 관리

  - 채팅방 목록 조회

  - 채팅방 나가기 (그룹 채팅만 해당)

  - 채팅방을 나가면 목록에서 제거

# ⚡ 실시간 기능 및 성능 최적화

  - WebSocket / STOMP 기반 실시간 채팅

  - Redis Pub/Sub 활용 (다중 서버 확장성 지원)

  - SSE(Server-Sent Events) 활용 (실시간 알림 전송)

# 🔧 프로젝트 환경

# 🖥️ 백엔드

  - 언어: Java 17

  - 프레임워크: Spring Boot 3.4

  - 데이터베이스: MySQL

  - 캐시: Redis

  - 보안: Spring Security, JWT

  - 웹 소켓: Spring WebSocket

  - 검증 및 모니터링: Spring Validation, Actuator, Prometheus

# 🎨 프론트엔드

  - 언어: JavaScript (ES6+)

  - 프레임워크: Vue3

  - UI 라이브러리: Vuetify

# ☁️ 인프라 및 배포 환경

  - 컨테이너화: Docker, Docker Compose

  - 클라우드 서비스: AWS EC2
