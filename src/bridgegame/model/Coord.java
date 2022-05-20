package bridgegame.model;

import lombok.AllArgsConstructor;

import static bridgegame.constant.ViewConstant.*;


@AllArgsConstructor
public class Coord {
  public Integer x;
  public Integer y;

  public Coord(Coord coord) {
    y = coord.y;
    x = coord.x;
  }

  public Boolean isValid() {
    return (x < CELL_COL_CNT.getInt() && y < CELL_ROW_CNT.getInt() && x >= 0 && y >= 0);
  }

  public Boolean isValid(Character c) {
    Coord next = new Coord(x, y);
    next.move(c);
    return next.isValid();
  }

  public void move(Character c) {
    if (c == 'R') x++;
    else if (c == 'L') x--;
    else if (c == 'U') y--;
    else if (c == 'D') y++;
  }
}