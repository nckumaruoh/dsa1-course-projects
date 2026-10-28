import java.util.*;

/** FlashCart: Flash-Sale Inventory — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("FlashCart: Flash-Sale Inventory");
    System.out.println(
        "1. Bot detection: "
            + display(CoreBasics.repeats(new String[] {"7", "3", "7", "9", "7", "3", "11", "7"})));
    System.out.println(
        "2. Fewest coupons: "
            + display(W04CatalogueReference.minCoins(new int[] {50, 100, 200}, 450)));
    System.out.println(
        "3. Units sold in any minute range: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {120L, 340L, 560L, 410L, 220L, 90L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 5}})));
    System.out.println(
        "4. Bundle offers: "
            + display(W06CatalogueReference.pair(new int[] {499, 650, 250, 750, 300, 820}, 1000)));
    System.out.println(
        "5. Packing rate: "
            + display(Week9ProjectReference.min_rate(new int[] {120, 45, 300, 80}, 6)));
    System.out.println(
        "6. Product page cache: "
            + display(Week11ProjectReference.simulate(new int[] {1, 2, 3, 1, 4, 2, 1, 5, 1}, 3)));
  }
}
