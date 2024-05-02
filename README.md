2024년 3월 6일 개발 시작
![image](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/10a9ddcd-fefd-465e-9e6f-2e449d849ba6)


![부산 파트너스 플로우차트-서비스 플로우 drawio](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/26916072-7c56-48bd-be23-e1388cb50497)

-간단한 개요-

BusanPartners는 부산지역 내 대학생과 외국인 관광객을 매칭시켜주는 앱입니다.


외국어를 할 수 있거나 직접 대화를 해보고 싶은 대학생들은 실제 외국인과 소통하며 실력을 쌓을 수 있고, 


부산에 온 외국인들은 한국에 왔을 때 가장 큰 문제점인 의사소통의 어려움을 없애고 현지인이 추천하는 음식이나 관광지를 방문할 수 있습니다.


추후 인적 네트워크를 구축하여 대학생들이 향후 해외로 나가더라도 다시 만날 수 있는 기회를 제공할 지도 모릅니다.





2024년 4월 14일 기준
디자인은 아직 제작하지 못한 상태입니다. ui 위주가 아닌, 빠르게 앱을 개발하기 위해 필요한 기능 위주로 제작하고 있는 상태입니다.

![ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/089516ab-2a08-470f-b110-cf9e60ebfba4)

처음 접속할 때 간단한 예시, 및 이 앱이 어떤 방식으로 운영이 되는 지에 대해 알려줄 수 있는 게스트 모드입니다. 아무런 채팅을 입력할 수 없습니다.


![KakaoTalk_20240417_134806024-ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/83c016c3-e0be-4bdd-94e9-67441151ee03)

04/17/2024 (온보딩 페이지 업데이트)
움직이는 벡터 이미지와 간단한 설명을 통해 더욱 이해하기 쉽도록 합니다.


![authenticatio-ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/36efdbf5-9993-4f14-94ab-88f8ec333c8a)

대학생일 때 인증을 하여 채팅을 할 수 있도록 하는 절차입니다. 대학교 이메일 인증이 필요하며, 학생증 사진을 촬영하여 첨부하면 loading으로 넘어갑니다.

관광객도 인증이 필요하며, 비행기 티켓이나 배 티켓, 그리고 숙박 예정이거나 숙박 증명이 필요합니다. 모두 이름이 들어가 있으면 됩니다. 

인증된 대학생은 한 번 인증하면 계속 앱을 이용할 수 있으나 관광객에게는 7일의 채팅 토큰을 부여합니다. 

확인은 제가 직접 진행합니다. 






![tag-ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/bde8d7d0-bd0c-4534-8f07-584a11f0f2fc)

대학생은 이곳에서 자신이 관광객에게 공개해도 될 만한 정보를 기입합니다. 작성하지 않아도 무방하며 이곳에서 채팅을 하고 싶지 않다면 태그를 눌러 해제합니다.
추후 네이버 번역 API를 가져와 이용할 예정입니다.







![map-ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/eef49096-0927-463c-9a08-ff802d922020)

관광객은 자신의 위치를 기준으로 근처 대학교의 학생들과 연락을 할 수 있습니다. 인증되지 않은 사용자는 연락하기를 누를 수 없습니다.

관광객은 자국어로 번역이 된 상태로 대학생의 간단한 인적사항을 읽을 수 있으며 연락을 할 수 있습니다.

대학생들이 직접 채팅을 시작할 수 없으며, 오직 관광객만이 대학생과 채팅을 시작할 수 있습니다.






![attachment-ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/bae9980c-7ee5-4de5-9669-bb54ef2e1a57)

해당 대화가 시작된다면 텍스트 대화, 지도 공유 기능, 이미지 및 동영상을 상대방에게 보낼 수 있습니다. 

기본적으로 GetStream.io 메시지 기능을 이용하고 있기 때문에 메시지 신고, 좋아요 및 싫어요 등의 리액션을 제공하고 있습니다.



![KakaoTalk_20240417_140641942-ezgif com-resize](https://github.com/KWON-minseok5247/BusanPartners/assets/63951789/c2cc3d21-06ae-4bb7-94ab-c5823b72ad6d)

추후 부산 내 축제, 관광지 정보를 제공할 수 있도록 할 예정입니다. 자신의 위치를 기준으로 관광지를 추천합니다.

세부적인 절차는 지속해서 업데이트를 할 예정이며 6월이 끝나기 전에 완성을 하는 것으로 목표를 두고 있습니다.
