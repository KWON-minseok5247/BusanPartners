2024년 3월 6일 개발 시작

https://www.2024tourapi.com/
2024년 5월 21일 한국관광공사 x 카카오 2024 관광데이터 활용 공모전 예비 심사 합격


![image](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/10a9ddcd-fefd-465e-9e6f-2e449d849ba6)
로그인 플로우 차트

![부산 파트너스 플로우차트-서비스 플로우 drawio](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/26916072-7c56-48bd-be23-e1388cb50497)
서비스 플로우 차트

-간단한 개요-

BusanPartners는 부산지역 내 대학생과 외국인 관광객을 매칭시켜주는 앱입니다.

부산파트너스 앱은 외국인 관광객들이 언어 장벽 없이 부산을 쉽게 여행할 수 있도록 도와줍니다.

이 앱은 대학생들에게는 무료로 외국어를 연습할 기회를 제공하고, 관광객들에게는 정확한 정보와 현지 경험을 제공합니다. 이를 통해 부산의 매력을 널리 알리고, 지역 경제에도 긍정적인 영향을 미칠 것입니다.

관광객들은 이 앱을 통해 현지인이 추천하는 명소를 방문할 수 있어, 더 풍부한 관광 경험을 할 수 있습니다. 또한, 이러한 인적 네트워크는 지속적인 교류와 장기적인 관계 형성의 기반이 됩니다.







![start](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/094d0988-d198-465e-85d1-c257e1589843)


2024년 5월 4일 기준
디자인은 아직 제작하지 못한 상태입니다. ui 위주가 아닌, 빠르게 앱을 개발하기 위해 필요한 기능 위주로 제작하고 있으며 MVP 단계를 밟고 있습니다.

2024년 7월 3일
디자인 1차 제작 완료. 세부 프래그먼트 추가 완료










![ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/089516ab-2a08-470f-b110-cf9e60ebfba4)

처음 접속할 때 간단한 예시, 및 이 앱이 어떤 방식으로 운영이 되는 지에 대해 알려줄 수 있는 게스트 모드입니다. 아무런 채팅을 입력할 수 없습니다.








![uni](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/2b7c05b7-4338-4361-b649-9d5b97b62c52)
![traveler](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/d901ea1d-b38a-4695-aa67-68c80d19ff7a)

대학생일 때 인증을 하여 채팅을 할 수 있도록 하는 절차입니다. 대학교 이메일 인증이 필요하며, 학생증 사진을 촬영하여 첨부하면 loading으로 넘어갑니다.

관광객도 인증이 필요하며, 비행기 티켓이나 배 티켓, 그리고 숙박 예정이거나 숙박 증명이 필요합니다. 모두 이름이 들어가 있으면 됩니다. 

인증된 대학생은 한 번 인증하면 계속 앱을 이용할 수 있으나 관광객에게는 7일의 채팅 토큰을 부여합니다. 

확인은 제가 직접 진행합니다. 










![exceptfor](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/049dbca9-4b87-41b3-85ae-165e84d2d08a)

대학생은 이곳에서 자신이 관광객에게 공개해도 될 만한 정보를 기입합니다. 작성하지 않아도 무방하며 이곳에서 채팅을 하고 싶지 않다면 태그를 눌러 해제합니다.

2024-05-31 DeepL api를 가져와 번역을 진행하였습니다. 

해당 정보들을 영어, 중국어 간체, 일본어로 번역하여 서버에 저장을 하고, 해당 언어가 필요할 때 바로 제공할 수 있습니다.

(현재 무료로 이용하기 위해 DeepL을 사용하였습니다. 중국어 번체, 베트남어 이용자가 어느정도 된다면 네이버 papago api로 변경해 유료 이용을 할 계획입니다.)








![connect](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/077811b9-093f-41e8-b3a2-0bf622b94499)

관광객은 자신의 위치를 기준으로 근처 대학교의 학생들과 연락을 할 수 있습니다. 인증되지 않은 사용자는 연락하기를 누를 수 없습니다.

관광객은 자국어로 번역이 된 상태로 대학생의 간단한 인적사항을 읽을 수 있으며 연락을 할 수 있습니다.

대학생들이 직접 채팅을 시작할 수 없으며, 오직 관광객만이 대학생과 채팅을 시작할 수 있습니다.









![attachment-ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/bae9980c-7ee5-4de5-9669-bb54ef2e1a57)

해당 대화가 시작된다면 텍스트 대화, 지도 공유 기능, 이미지 및 동영상을 상대방에게 보낼 수 있습니다. 

기본적으로 GetStream.io 메시지 기능을 이용하고 있기 때문에 메시지 신고, 좋아요 및 싫어요 등의 리액션을 제공하고 있습니다.


![메인](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/a117aaaa-1417-421a-9d09-0f684bd73562)
![축제](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/309485e1-19a3-4a1b-b893-933c7992a691)
![인증 선택](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/051392e7-55f7-44df-a0eb-51d61077be3d)
![학생증](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/3c4b47ac-2f56-47c2-8850-7647e8c50c7a)
![지도](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/f64c893c-fd12-4302-aca8-6bfb16bacff3)
![대학 클릭](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/8e989c34-fd5c-445d-a6a8-f50773e97ae3)
![대학생 리스트](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/c8cdc3e0-76e3-43b2-9031-fa1f8ed0e349)
![세부 대학생](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/6b286849-0c94-465a-be15-866504adccf1)


추후 부산 내 축제, 관광지 정보를 제공할 수 있도록 할 예정입니다. 자신의 위치를 기준으로 관광지를 추천합니다.

세부적인 절차는 지속해서 업데이트를 할 예정이며 8월이 끝나기 전에 완성을 하는 것으로 목표를 두고 있습니다.
