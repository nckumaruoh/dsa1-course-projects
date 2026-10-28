import java.util.*;

/** MetroPulse: Smart-Card Ridership — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("MetroPulse: Smart-Card Ridership");
    System.out.println(
        "1. Busiest station: "
            + display(CoreBasics.frequent(new int[] {3, 7, 3, 12, 7, 3, 5, 7, 3})));
    System.out.println(
        "2. Cheapest fare products: "
            + display(W04CatalogueReference.minCoins(new int[] {10, 25, 60}, 135)));
    System.out.println(
        "3. Load on each track segment: "
            + display(
                W05CatalogueReference.apply(
                    6,
                    new int[][] {
                      new int[] {0, 3, 120}, new int[] {2, 5, 80}, new int[] {1, 2, 60}
                    })));
    System.out.println(
        "4. Peak 15-minute window: "
            + display(
                Week7ProjectReference.bestWindow(new int[] {40, 65, 120, 180, 150, 90, 60}, 3)));
    System.out.println(
        "5. Train capacity needed: "
            + display(
                Week9ProjectReference.min_capacity(new int[] {300, 450, 200, 380, 520, 260}, 4)));
    System.out.println(
        "6. Next busier hour: "
            + display(
                Week12ProjectReference.waitDays(new int[] {820, 1100, 950, 700, 1300, 1250, 900})));
  }
}
