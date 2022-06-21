package bridgegame.model;

import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static bridgegame.constant.BoardConstant.*;
import static bridgegame.constant.ViewConstant.STATIC_PATH;


@Data
public class GameModel {

  private Integer playerCnt, turn, dice;
  private Map<Integer, PlayerModel> players;
  private BoardModel board;

  /**
   * Create game
   *
   * @param playerCnt   player numbers
   * @param mapFilePath start coord position
   * @param startCoord
   * @throws IOException
   */
  public GameModel(Integer playerCnt, String mapFilePath, Coord startCoord) throws IOException {
    this.playerCnt = playerCnt;

    board = new BoardModel(startCoord, mapFilePath);
    players = new HashMap<>();
    turn = 1;

    PlayerModel.initialize();
    for (int i = 1; i <= playerCnt; i++)
      players.put(i, new PlayerModel(i, startCoord));
  }

  public void movePlayer(String cmd, Integer cmdLength) throws Exception {
    // convert command to uppercase
    if (cmd != null) cmd = cmd.toUpperCase();

    // when command is not u/d/r/l
    if (cmd == null || cmd.equals("") || !cmd.replace("R", "").replace("L", "").replace("D", "").replace("U", "").equals(""))
      throw new Exception("command");
    // when command length is not valid
    if (cmd.length() != cmdLength) throw new Exception("length");


    PlayerModel player = players.get(turn);
    Coord coordCp = new Coord(player.getCoord()), coord = player.getCoord();
    Integer turnCp = turn;

    // check command's validation
    for (Character c : cmd.toCharArray()) {

      // if movement is in cell
      if (coord.isValid(c) && board.getCell(coord, c) != null) {

        // if finished player exist and move backward
        if (playerCnt > PlayerModel.leftPlayerCnt
            && board.getCellPriority(coord, c) < board.getCellPriority(coord)) {
          backup(player, coordCp, turnCp);
          throw new Exception("direction");
        }

        coord.move(c);

        if (board.getCell(coord) == BRIDGE) {
          player.addBridgeCard();
          coord.move('R');
        }

      } else {
        backup(player, coordCp, turnCp);
        throw new Exception("outofboard");
      }

      if (board.getCell(coord) == END) {
        player.finish();
        break;
      }
    }
    player.updateScore(board.getCell(player.getCoord()));
    player.setCoordBefore(coordCp);
  }

  public PlayerModel getCurrentPlayer() {
    return players.get(turn);
  }

  private void backup(PlayerModel player, Coord coord, Integer turn) {
    player.setCoord(coord);
    this.turn = turn;
  }

  public void passTurn() {
    PlayerModel player = players.get(turn);
    player.deleteBridgeCard();
  }

  public Boolean changeTurn() {
    if (PlayerModel.leftPlayerCnt == 1) return false;
    do {
      if (++turn > playerCnt) turn = 1;
    } while (players.get(turn).getIsFinished());
    return true;
  }

  public Integer rollDice() {
    return dice = new Random().nextInt(6) + 1;
  }

  /**
   * winner can be more than 2
   * @return winner's number
   */
  public List<Integer> getWinners() {
    List<Integer> winners = new ArrayList<>();
    winners.add(1);
    Integer max = -1;
    for (PlayerModel player : players.values())
      if (player.getScore() >= max) {
        if (player.getScore() != max) {
          winners.clear();
          max = player.getScore();
        }
        winners.add(player.getId());
      }
    return winners;
  }

  public static String[] getMapFileNames() {
    File mapDir = new File(String.format("%s/map", STATIC_PATH.getStr()));
    return Arrays.stream(mapDir.list()).filter(s -> s.matches("(.*/)*.+\\.map$")).toArray(String[]::new);
  }
}
