import bridgegame.controller.cli.GameController;
import bridgegame.controller.cli.MenuController;

import java.io.IOException;

public class CliMain {

  public static void main(String[] args) throws IOException {
    MenuController menu = new MenuController();

    while(menu.show())
      new GameController().play();
  }
}
