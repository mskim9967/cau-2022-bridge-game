package bridgegame.controller.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static bridgegame.constant.ViewConstant.*;


public class MenuController implements Initializable {

  @FXML
  Pane pane;
  @FXML
  Button gameStartBtn, exitBtn;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    gameStartBtn.setOnAction((ActionEvent) -> {
      try {
        pane.getScene().setRoot(GameController.getRoot());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    exitBtn.setOnAction((ActionEvent) -> {
      Platform.exit();
    });

    // run after all initialization
    Platform.runLater(()->{
      pane.getScene().getWindow().setWidth(MENU_WINDOW_WIDTH.getInt());
      pane.getScene().getWindow().setHeight(MENU_WINDOW_HEIGHT.getInt());
      pane.getScene().getWindow().centerOnScreen();
    });
  }

  public static Parent getRoot() throws IOException {
    return FXMLLoader.load(new File("static/view/menu.fxml").toURI().toURL());
  }
}