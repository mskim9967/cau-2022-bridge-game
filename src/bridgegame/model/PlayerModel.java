package bridgegame.model;

import bridgegame.constant.BoardConstant;
import lombok.Data;

import static bridgegame.constant.BoardConstant.*;


@Data
public class PlayerModel {

  public static Integer leftPlayerCnt = 0, finishedPlayerCnt = 0;
  public final static Integer[] finishScore = {0, 7, 4, 1, 0};

  private Coord coord, coordBefore;
  private Integer id, score, bridgeCard;
  private Boolean isFinished;

  public static void initialize() {
    leftPlayerCnt = 0;
    finishedPlayerCnt = 0;
  }

  public PlayerModel(Integer id, Coord startCoord) {
    this.id = id;
    coord = new Coord(startCoord);
    coordBefore = new Coord(startCoord);
    isFinished = false;
    leftPlayerCnt++;
    score = 0;
    bridgeCard = 0;
  }

  public void finish() {
    isFinished = true;
    leftPlayerCnt--;
    finishedPlayerCnt++;
  }

  public void updateScore(BoardConstant cell) {
    if (cell == PHILIPS) score += 1;
    else if (cell == HAMMER) score += 2;
    else if (cell == SAW) score += 3;
    else if (cell == END) score += finishScore[finishedPlayerCnt];
  }

  public void addBridgeCard() {
    bridgeCard++;
  }

  public void deleteBridgeCard() {
    bridgeCard = Math.max(0, bridgeCard - 1);
  }
}
