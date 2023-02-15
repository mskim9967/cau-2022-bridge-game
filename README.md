# Bridge Game

## 기본 개발 및 실행 환경
- Java 17
- JavaFX 18.0.1
- M1 Mac (Apple Silicon)

## GUI Interface
<img alt="image" src="https://user-images.githubusercontent.com/54897403/218950978-578239b2-3078-44dd-854c-18612c90ede4.png">

## CLI Interface
<img alt="image" src="https://user-images.githubusercontent.com/54897403/218951057-824439e3-27c3-4c55-9a65-6eb30119702a.png">


## 폴더 구조
```
.
├── BridgeGame
│   ├── src  // 소스코드
│   │   ├── CliMain.java  // CLI entry point
│   │   ├── GuiMain.java  // GUI entry point
│   │   ├── Launcher.java  // javafx jar 실행을 위한 class
│   │   ├── META-... // jar 빌드 시 entry point 정의를 위한 manifest
│   │   │   └── ...
│   │   └── bridgegame
│   │       ├── constant
│   │       │   ├── BoardConstant.java  // 보드의 각 셀을 정의하는 enum
│   │       │   └── ViewConstant.java  // 게임 창에 필요한 정적 변수들을 관리하는 enum
│   │       ├── controller
│   │       │   ├── cli  // CLI controller
│   │       │   │   ├── GameController.java
│   │       │   │   └── MenuController.java
│   │       │   └── gui  // GUI controller
│   │       │       ├── GameController.java
│   │       │       └── MenuController.java
│   │       ├── model  // model package
│   │       │   ├── BoardModel.java
│   │       │   ├── Coord.java
│   │       │   ├── GameModel.java
│   │       │   └── PlayerModel.java
│   │       └── utill
│   │           └── MyScanner.java  // 명령어 입력 시 자동 대문자 변환 가능하도록 scanner class를 상속
│   └── static
│       ├── image  // 이미지 파일 경로
│       │   └── ...
│       ├── map  // 맵 파일 경로
│       │   └── ...
│       └── view  // javafx view파일 경로 
│           ├── game.fxml
│           └── menu.fxml
```
