import java.util.*;

/** TickerTrail: Stock Watchlist Analytics — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("TickerTrail: Stock Watchlist Analytics");
    System.out.println(
        "1. Duplicate trade confirmations: "
            + display(
                CoreBasics.repeats(new String[] {"9001", "9002", "9003", "9001", "9004", "9001"})));
    System.out.println(
        "2. Spend a budget: "
            + display(
                W04CatalogueReference.bestTotal(new int[] {1850, 2420, 980, 1460, 3100}, 5000)));
    System.out.println(
        "3. Best single trade: "
            + display(
                W05CatalogueReference.bestTrade(
                    new long[] {2841L, 2856L, 2830L, 2812L, 2847L, 2869L, 2858L, 2875L})));
    System.out.println(
        "4. Merge two exchanges: "
            + display(
                W06CatalogueReference.merge(new int[] {1, 4, 6, 9}, new int[] {2, 3, 7, 10})));
    System.out.println(
        "5. Price on a date: "
            + display(W08CatalogueReference.lower(new int[] {2, 3, 6, 8, 13, 15, 16}, 15)));
    System.out.println(
        "6. Stock span: "
            + display(Week12ProjectReference.span(new int[] {100, 80, 60, 70, 60, 75, 85})));
  }
}
