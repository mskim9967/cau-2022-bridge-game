package bridgegame.controller.gui;

import bridgegame.constant.BoardConstant;
import bridgegame.constant.ViewConstant;
import bridgegame.model.Coord;
import bridgegame.model.GameModel;
import bridgegame.model.PlayerModel;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static bridgegame.constant.ViewConstant.STATIC_PATH;

public class GameController implements Initializable {

  @FXML
  BorderPane pane;
  @FXML
  ImageView diceView;
  @FXML
  GridPane boardPane, scorePane;
  @FXML
  Button exitBtn, cmdBtn, passBtn, rollDiceBtn;
  @FXML
  TextField cmdTfd;
  @FXML
  Label turnLbl, bridgeCardLbl, movableCntLbl;

  Label[] playerScoreLbl;
  GridPane[][] playerPane;

  private GameModel game;
  private Coord startCoord;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      startCoord = new Coord(0, 3);
      game = new GameModel(getPlayerCntFromDialog(), getMapPathFromDialog(), startCoord);
      drawInit();
    } catch (IOException e) {
      System.out.println("view file not found");
    }


    /*---------------- handler ----------------*/

    exitBtn.setOnAction(ActionEvent -> {
      try {
        pane.getScene().setRoot(MenuController.getRoot());
      } catch (IOException e) {
        System.out.println("view file not found");
      }
    });

    passBtn.setOnAction(ActionEvent -> {
      game.passTurn();
      drawBeforeChangeTurn();
    });

    cmdTfd.setOnKeyPressed(event -> {
      // when command is written and enter key is pressed
      if (event.getCode() == KeyCode.ENTER) cmdBtn.fire();
    });

    cmdBtn.setOnAction(ActionEvent -> {
      // 1. move player
      // 2. if exception occurs, command is not valid, so do rollback
      try {
        game.movePlayer(cmdTfd.getText(), game.getDice() - game.getCurrentPlayer().getBridgeCard());
      } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        if (e.getMessage().equals("command"))
          alert.setContentText("Invalid command (R/L/U/D)");
        else if (e.getMessage().equals("direction"))
          alert.setContentText("You can't go to backward");
        else if (e.getMessage().equals("length"))
          alert.setContentText("Command length is invalid");
        else if (e.getMessage().equals("outofboard"))
          alert.setContentText("Command is not in board");
        alert.showAndWait();
        return;
      }

      BoardConstant currentCell = game.getBoard().getCell(game.getCurrentPlayer().getCoord());

      // alert item's score when player gets an item
      if (currentCell != BoardConstant.START && currentCell != BoardConstant.CELL && currentCell != BoardConstant.BRIDGE_START && currentCell != BoardConstant.BRIDGE_END) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        if (currentCell == BoardConstant.SAW)
          alert.setContentText("You get a saw(3pt)");
        else if (currentCell == BoardConstant.HAMMER)
          alert.setContentText("You get a hammer(2pt)");
        else if (currentCell == BoardConstant.PHILIPS)
          alert.setContentText("You get a philips driver(1pt)");
        else if (currentCell == BoardConstant.END)
          alert.setContentText(String.format("Finished! You get additional points (%spt)", PlayerModel.finishScore[PlayerModel.finishedPlayerCnt]));
        alert.showAndWait();
      }

      drawBeforeChangeTurn();
    });

    rollDiceBtn.setOnAction(ActionEvent -> {
      drawRollingDice(game.rollDice());

      movableCntLbl.setText(String.format("You can move %s times", Math.max(0, game.getDice() - game.getCurrentPlayer().getBridgeCard())));
      rollDiceBtn.setDisable(true);
      passBtn.setDisable(false);

      // if player can't move, disable ui
      if (game.getDice() > game.getCurrentPlayer().getBridgeCard()) {
        cmdBtn.setDisable(false);
        cmdTfd.setDisable(false);
      }
    });

    // run after all initialization
    Platform.runLater(() -> {
      pane.getScene().getWindow().setWidth(ViewConstant.GAME_WINDOW_WIDTH.getInt());
      pane.getScene().getWindow().setHeight(ViewConstant.GAME_WINDOW_HEIGHT.getInt());
      pane.getScene().getWindow().centerOnScreen();
    });
  }

  public static Parent getRoot() throws IOException {
    return FXMLLoader.load(new File("static/view/game.fxml").toURI().toURL());
  }

  private Integer getPlayerCntFromDialog() {

    TextInputDialog playerCntDg = new TextInputDialog();
    playerCntDg.setHeaderText("How many players? (2~4)");

    Integer userInput;
    do {
      try {
        userInput = Integer.parseInt(playerCntDg.showAndWait().get());
      } catch (Exception e) {
        userInput = 0;
      }
    } while (userInput < 2 || userInput > 4);

    return userInput;
  }

  private String getMapPathFromDialog() {

    String[] mapFileNames = GameModel.getMapFileNames();
    ChoiceDialog fileNameDg = new ChoiceDialog("default.map", mapFileNames);
    fileNameDg.setHeaderText("Select map file");
    return String.format("%s/map/%s", STATIC_PATH.getStr(), fileNameDg.showAndWait().get());
  }


  /*---------------- view functions ----------------*/

  private void drawInit() throws IOException {
    playerPane = new GridPane[ViewConstant.CELL_ROW_CNT.getInt()][ViewConstant.CELL_COL_CNT.getInt()];
    playerScoreLbl = new Label[5];

    turnLbl.setText(game.getTurn().toString());
    turnLbl.setStyle("-fx-font-weight: bold");
    turnLbl.setStyle("-fx-font-size: 50");

    bridgeCardLbl.setText(String.format("%s Bridge cards", game.getCurrentPlayer().getBridgeCard().toString()));

    drawBoard();
    drawWaitingDice();
    for (PlayerModel player : game.getPlayers().values()) {
      drawPlayer(player);
      drawPlayerScore(player);
    }

  }

  private void drawBoard() throws IOException {

    boardPane.setMaxSize(ViewConstant.BOARD_WIDTH.getInt(), ViewConstant.BOARD_HEIGHT.getInt());

    // set cell size
    RowConstraints rc = new RowConstraints();
    rc.setVgrow(Priority.ALWAYS);
    rc.setMaxHeight(ViewConstant.BOARD_HEIGHT.getInt().floatValue() / ViewConstant.CELL_ROW_CNT.getInt());
    for (int i = 0; i < ViewConstant.CELL_ROW_CNT.getInt(); i++) boardPane.getRowConstraints().add(rc);

    ColumnConstraints cc = new ColumnConstraints();
    cc.setHgrow(Priority.ALWAYS);
    cc.setMaxWidth(ViewConstant.BOARD_WIDTH.getInt().floatValue() / ViewConstant.CELL_COL_CNT.getInt());
    for (int i = 0; i < ViewConstant.CELL_COL_CNT.getInt(); i++) boardPane.getColumnConstraints().add(cc);


    for (int i = 0; i < ViewConstant.CELL_ROW_CNT.getInt(); i++) {
      for (int j = 0; j < ViewConstant.CELL_COL_CNT.getInt(); j++) {

        BoardConstant cell = game.getBoard().getCell(j, i);
        if (cell == null) continue;

        String imgName =
            cell == BoardConstant.START ? "start" :
                cell == BoardConstant.END ? "end" :
                    cell == BoardConstant.HAMMER ? "hammer" :
                        cell == BoardConstant.SAW ? "SAW" :
                            cell == BoardConstant.PHILIPS ? "philips" :
                                cell == BoardConstant.BRIDGE_START ? "threeWay" :
                                    cell == BoardConstant.BRIDGE ? "bridge" : "cell";

        FileInputStream imgFile = new FileInputStream(String.format("%s/static/image/%s.png", System.getProperty("user.dir"), imgName));
        Image img = new Image(imgFile);
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(ViewConstant.BOARD_WIDTH.getInt().floatValue() / ViewConstant.CELL_COL_CNT.getInt());
        imgView.setFitHeight(ViewConstant.BOARD_HEIGHT.getInt().floatValue() / ViewConstant.CELL_ROW_CNT.getInt());

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imgView);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(2, 2, 2, 2)); //margins around the whole grid

        playerPane[i][j] = gridPane;
        stackPane.getChildren().add(gridPane);

        boardPane.add(stackPane, j, i);
      }
    }
  }

  private void drawBeforeChangeTurn() {
    try {
      drawPlayer(game.getCurrentPlayer());
      cmdTfd.setText(null);
      redrawPlayerScore(game.getCurrentPlayer());

      if (!game.changeTurn()) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game End");

        alert.setContentText(String.format("Player %s Win!",
            game.getWinners().stream().map(String::valueOf).collect(Collectors.joining(", "))));

        StringBuilder result = new StringBuilder();
        for (PlayerModel player : game.getPlayers().values())
          result.append(String.format("player %s: %s(pt) \n", player.getId(), player.getScore()));

        TextArea textArea = new TextArea(result.toString());
        textArea.setWrapText(true);

        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
        exitBtn.fire();
      }

      rollDiceBtn.setDisable(false);
      passBtn.setDisable(true);
      cmdBtn.setDisable(true);
      cmdTfd.setDisable(true);

      drawWaitingDice();
      turnLbl.setText(game.getTurn().toString());
      bridgeCardLbl.setText(String.format("%s Bridge cards", game.getCurrentPlayer().getBridgeCard().toString()));
      movableCntLbl.setText("");

    } catch (FileNotFoundException e) {
      System.out.println("view file not found");
    }
  }

  private void drawPlayer(PlayerModel player) throws FileNotFoundException {
    playerPane[player.getCoordBefore().y][player.getCoordBefore().x].getChildren().removeIf(node -> GridPane.getColumnIndex(node) == (player.getId() - 1) % 2 && GridPane.getRowIndex(node) == (player.getId() - 1) / 2);

    FileInputStream imgFile = new FileInputStream(String.format("%s/static/image/player%s.png", System.getProperty("user.dir"), player.getId()));
    Image img = new Image(imgFile);
    ImageView imgView = new ImageView(img);
    imgView.setFitWidth(ViewConstant.BOARD_WIDTH.getInt().floatValue() / ViewConstant.CELL_COL_CNT.getInt() / 2.2);
    imgView.setFitHeight(ViewConstant.BOARD_HEIGHT.getInt().floatValue() / ViewConstant.CELL_ROW_CNT.getInt() / 2.2);

    playerPane[player.getCoord().y][player.getCoord().x].add(imgView, (player.getId() - 1) % 2, (player.getId() - 1) / 2);
  }

  private void drawPlayerScore(PlayerModel player) throws FileNotFoundException {

    VBox vBox = new VBox();
    vBox.setAlignment(Pos.CENTER);

    FileInputStream imgFile = new FileInputStream(String.format("%s/static/image/player%s.png", System.getProperty("user.dir"), player.getId()));
    Image img = new Image(imgFile);
    ImageView imgView = new ImageView(img);
    imgView.setFitWidth(55);
    imgView.setFitHeight(55);

    playerScoreLbl[player.getId()] = new Label(player.getScore().toString() + "/" + player.getBridgeCard());
    playerScoreLbl[player.getId()].setStyle("-fx-font-weight: bold");
    playerScoreLbl[player.getId()].setStyle("-fx-font-size: 30");

    vBox.getChildren().add(imgView);
    vBox.getChildren().add(new Label("Player " + player.getId()));
    vBox.getChildren().add(playerScoreLbl[player.getId()]);

    scorePane.add(vBox, (player.getId() - 1) % 2, (player.getId() - 1) / 2);
  }

  private void redrawPlayerScore(PlayerModel player) throws FileNotFoundException {
    playerScoreLbl[player.getId()].setText(player.getScore().toString() + "/" + player.getBridgeCard());
  }

  private void drawWaitingDice() throws FileNotFoundException {
    diceView.setImage(new Image(new FileInputStream(String.format("%s/static/image/dice.png", System.getProperty("user.dir")))));
  }

  private void drawRollingDice(Integer number) {
    Task<Void> task = new Task<Void>() {
      @Override
      protected Void call() throws InterruptedException, FileNotFoundException {
        for (int i = 1; i <= 6; i++) {
          diceView.setImage(new Image(new FileInputStream(String.format("%s/static/image/dice%s.png", System.getProperty("user.dir"), i))));
          Thread.sleep(50);
        }
        diceView.setImage(new Image(new FileInputStream(String.format("%s/static/image/dice%s.png", System.getProperty("user.dir"), number))));
        return null;
      }
    };
    new Thread(task).start();
  }
}