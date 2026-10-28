import java.util.*;

/** DashRoute: Food-Delivery Rider Batching — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("DashRoute: Food-Delivery Rider Batching");
    System.out.println(
        "1. Duplicate orders: "
            + display(CoreBasics.repeats(new String[] {"41", "42", "43", "42", "44", "45", "41"})));
    System.out.println(
        "2. Fill a rider\u2019s bag: "
            + display(W04CatalogueReference.bestTotal(new int[] {3, 4, 5, 7, 2}, 10)));
    System.out.println(
        "3. Pair short trips: "
            + display(W06CatalogueReference.pair(new int[] {2, 7, 4, 5, 3, 8}, 9)));
    System.out.println(
        "4. Fastest 50 orders: "
            + display(Week7ProjectReference.minLength(new int[] {8, 12, 5, 20, 9, 14, 3}, 50)));
    System.out.println(
        "5. Minimum delivery rate: "
            + display(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6)));
    System.out.println(
        "6. Merge rider shifts: "
            + display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {9, 13},
                      new int[] {12, 15},
                      new int[] {18, 22},
                      new int[] {21, 23},
                      new int[] {14, 16}
                    })));
  }
}
