package bridgegame.controller.cli;

import bridgegame.utill.MyScanner;

import static bridgegame.constant.ViewConstant.*;

public class MenuController {
  private MyScanner scanner = new MyScanner();

  public Boolean show() {
    System.out.println();
    System.out.println(String.format("[%s]", WINDOW_TITLE.getStr()));
    System.out.print("(Q) Quit / (ELSE) Play : ");

    return !scanner.read().equals("Q");
  }
}