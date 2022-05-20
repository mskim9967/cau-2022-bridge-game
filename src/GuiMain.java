import bridgegame.controller.gui.MenuController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static bridgegame.constant.ViewConstant.WINDOW_TITLE;


public class GuiMain extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle(WINDOW_TITLE.getStr());
    stage.setScene(new Scene(MenuController.getRoot()));
    stage.show();
  }
}
