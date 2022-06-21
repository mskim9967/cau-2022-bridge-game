package bridgegame.controller.cli;

import bridgegame.constant.BoardConstant;
import bridgegame.model.Coord;
import bridgegame.model.GameModel;
import bridgegame.model.PlayerModel;
import bridgegame.utill.MyScanner;

import java.io.IOException;
import java.util.stream.Collectors;

import static bridgegame.constant.BoardConstant.*;
import static bridgegame.constant.ViewConstant.*;

public class GameController {

  private final MyScanner scanner = new MyScanner();
  private GameModel game;

  public void play() throws IOException {

    Integer playerCnt = 0;
    String mapFilePath, line;
    Coord startCoord = new Coord(0, 3);
    System.out.println();
    do {
      System.out.print("How many players? (2~4) : ");
      try {
        playerCnt = Integer.parseInt(scanner.read());
      } catch (Exception ignored) {
      }
    } while (playerCnt < 2 || playerCnt > 4);


    while (true) {
      System.out.println();
      String[] mapFileNames = GameModel.getMapFileNames();
      Integer idx = 1;
      for (String name : mapFileNames)
        System.out.printf("(%s) %s%n", idx++, name);
      System.out.print("Select map file : ");
      try {
        idx = Integer.parseInt(scanner.read());
      } catch (Exception ignored) {
      }

      if (idx > 0 && idx <= mapFileNames.length) {
        mapFilePath = String.format("%s/map/%s", STATIC_PATH.getStr(), mapFileNames[idx - 1]);
        break;
      }
    }

    game = new GameModel(playerCnt, mapFilePath, startCoord);

    do {
      drawBoard();

      System.out.printf("Player %s's turn (%spt)\n", game.getTurn(), game.getCurrentPlayer().getScore());
      System.out.printf("Your dice number is %s, you have %s bridge cards\n",
          game.rollDice(), game.getCurrentPlayer().getBridgeCard());
      System.out.printf("You can move %s times\n\n", game.getDice() - game.getCurrentPlayer().getBridgeCard());
      System.out.print("(M) Move / (P) Pass turn / (Q) Quit game : ");

      line = scanner.read();
      if (line.equals("Q")) return;
      else if (line.equals("P")) {
        game.passTurn();
        continue;
      } else if (line.equals("M") && game.getDice() <= game.getCurrentPlayer().getBridgeCard()) {
        System.out.println("You can't move!");
        game.passTurn();
        continue;
      }

      while (true) {
        System.out.print("Press command (R/L/U/D) : ");

        try {
          game.movePlayer(scanner.read(), game.getDice() - game.getCurrentPlayer().getBridgeCard());
          break;
        } catch (Exception e) {
          if (e.getMessage().equals("command"))
            System.out.println("Invalid command (R/L/U/D)");
          else if (e.getMessage().equals("direction"))
            System.out.println("You can't go to backward");
          else if (e.getMessage().equals("length"))
            System.out.println("Command length is invalid");
          else if (e.getMessage().equals("outofboard"))
            System.out.println("Command is not in board");
          System.out.println();
        }
      }

    } while (game.changeTurn());

    System.out.printf("\n[Player %s Win!]\n",
        game.getWinners().stream().map(s -> String.valueOf(s)).collect(Collectors.joining(", ")));

    for (PlayerModel player : game.getPlayers().values())
      System.out.printf("player %s: %s(pt) \n", player.getId(), player.getScore());

    System.out.println();
  }

  private void drawBoard() {
    System.out.println();
    for (int i = 0; i < CELL_ROW_CNT.getInt(); i++) {
      for (int j = 0; j < CELL_COL_CNT.getInt(); j++) {

        BoardConstant cell = game.getBoard().getCell(j, i);
        String s = "";

        if (game.getCurrentPlayer().getCoord().equals(new Coord(j, i))) s = "🟣";
        else if (cell == START) s = "🟥";
        else if (cell == CELL || cell == BRIDGE_END || cell == BRIDGE_START || cell == BRIDGE) s = "⬜";
        else if (cell == END) s = "🏁";
        else if (cell == SAW) s = "🟦";
        else if (cell == HAMMER) s = "🟩";
        else if (cell == PHILIPS) s = "🟨";
        else s = "⬛️️";
        System.out.printf("%s", s);
      }
      System.out.println();
    }
    System.out.println();
  }
}