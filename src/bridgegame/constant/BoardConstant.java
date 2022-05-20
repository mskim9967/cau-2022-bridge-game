package bridgegame.constant;

public enum BoardConstant {
  BRIDGE_START,
  BRIDGE_END,
  SAW,
  HAMMER,
  PHILIPS,
  START,
  CELL,
  BRIDGE,
  END;

  private final String value;

  public static BoardConstant get(Character c) {
    if(c == '$') return START;
    if(c == 'E') return END;
    if(c == 'C') return CELL;
    if(c == 'B') return BRIDGE_START;
    if(c == 'b') return BRIDGE_END;
    if(c == 'H') return HAMMER;
    if(c == 'S') return SAW;
    return PHILIPS;
  }

  BoardConstant() {
    value = name();
  }

}
