package bridgegame.model;

import lombok.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static bridgegame.constant.BoardConstant.*;


@Data
public class GameModel {

  private Integer playerCnt, turn, dice;
  private Map<Integer, PlayerModel> players;
  private BoardModel board;

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
    if (cmd != null) cmd = cmd.toUpperCase();

    if (cmd == null || cmd.equals("") || !cmd.replace("R", "").replace("L", "").replace("D", "").replace("U", "").equals(""))
      throw new Exception("command");
    if (cmd.length() != cmdLength) throw new Exception("length");


    PlayerModel player = players.get(turn);
    Coord coordCp = new Coord(player.getCoord()), coord = player.getCoord();
    Integer turnCp = turn;

    for (Character c : cmd.toCharArray()) {

      if (coord.isValid(c) && board.getCell(coord, c) != null) {
        if (playerCnt > PlayerModel.leftPlayerCnt &&
            board.getCellPriority(coord, c) < board.getCellPriority(coord)) {
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
}
