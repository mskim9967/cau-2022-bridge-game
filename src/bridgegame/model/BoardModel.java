package bridgegame.model;

import bridgegame.constant.BoardConstant;
import lombok.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static bridgegame.constant.BoardConstant.*;
import static bridgegame.constant.ViewConstant.*;

@Data
public class BoardModel {

  private BoardConstant[][] board;
  private Integer[][] priority;
  private Integer priorityCnt = 0;

  public BoardModel(Coord startCoord, String mapFilePath) throws IOException {

    board = new BoardConstant[CELL_ROW_CNT.getInt()][CELL_COL_CNT.getInt()];
    priority = new Integer[CELL_ROW_CNT.getInt()][CELL_COL_CNT.getInt()];
    Coord coord = new Coord(startCoord);

    FileReader fileReader = new FileReader(mapFilePath);
    BufferedReader bufReader = new BufferedReader(fileReader);
    String line = null;
    Character nextDir = null;
    boolean isFirstLine = true;

    while ((line = bufReader.readLine()) != null) {

      if(isFirstLine) line = '$' + line.substring(1);
      isFirstLine = false;

      setCell(coord, BoardConstant.get(line.charAt(0)));

      // make bridge
      if(board[coord.y][coord.x] == BRIDGE_START) {
        Coord bridgeCoord = new Coord(coord);
        if(bridgeCoord.isValid('R') && getCell(bridgeCoord, 'R') == null)
          bridgeCoord.move('R');
        setCell(bridgeCoord, BRIDGE);
      }

      if (line.charAt(0) == 'E') break;
      else {
        if(coord.isValid(line.charAt(2)) && getCell(coord, line.charAt(2)) == null)
          nextDir = line.charAt(2);
        else if(coord.isValid(line.charAt(4)) && getCell(coord, line.charAt(4)) == null)
          nextDir = line.charAt(4);
        else
          throw new IOException("File Format Error");
      }
      coord.move(nextDir);
    }
  }

  public void setCell(Coord coord, BoardConstant mapConstant) {
    board[coord.y][coord.x] = mapConstant;
    priority[coord.y][coord.x] = ++priorityCnt;
  }

  public BoardConstant getCell(Coord coord) {
    return board[coord.y][coord.x];
  }

  public BoardConstant getCell(Integer x, Integer y) {
    return board[y][x];
  }

  public BoardConstant getCell(Coord coord, Character c) {
    Coord next = new Coord(coord.x, coord.y);
    next.move(c);
    return getCell(next);
  }

  public Integer getCellPriority(Coord coord) {
    return priority[coord.y][coord.x];
  }

  public Integer getCellPriority(Coord coord, Character c) {
    Coord next = new Coord(coord.x, coord.y);
    next.move(c);
    return getCellPriority(next);
  }
}
