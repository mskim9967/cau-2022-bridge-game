package bridgegame.utill;

import java.util.Locale;
import java.util.Scanner;

public class MyScanner {
  private Scanner scanner = new Scanner(System.in);

  public String read() {
    return scanner.nextLine().toUpperCase(Locale.ROOT);
  }
}
