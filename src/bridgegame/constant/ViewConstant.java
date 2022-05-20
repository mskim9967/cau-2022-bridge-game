package bridgegame.constant;

public enum ViewConstant {
  GAME_WINDOW_WIDTH("1400"),
  GAME_WINDOW_HEIGHT("1000"),

  MENU_WINDOW_WIDTH("300"),
  MENU_WINDOW_HEIGHT("200"),

  BOARD_WIDTH("1000"),
  BOARD_HEIGHT("1000"),

  CELL_ROW_CNT("20"),
  CELL_COL_CNT("20"),

  WINDOW_TITLE("Bridge Game"),

  STATIC_PATH(String.format("%s/static", System.getProperty("user.dir")));


  private final String value;

  public Integer getInt() {
    return Integer.parseInt(value);
  }
  public String getStr() {
    return value;
  }

  ViewConstant(String value) {
    this.value = value;
  }
}
