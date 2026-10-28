import java.util.*;

/** FairFare: Ride-Share Surge Analyzer — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("FairFare: Ride-Share Surge Analyzer");
    System.out.println(
        "1. Duplicate ride requests: "
            + display(CoreBasics.repeats(new String[] {"88", "91", "88", "93", "95", "91", "88"})));
    System.out.println(
        "2. Shift plans: " + display(W04CatalogueReference.ways(new int[] {2, 3, 4}, 10)));
    System.out.println(
        "3. Worst shortage stretch: "
            + display(
                W05CatalogueReference.bestInterval(
                    new long[] {-3L, 4L, 6L, -2L, 5L, -8L, 3L, 2L})));
    System.out.println(
        "4. Shortest stretch reaching 50 rides: "
            + display(Week7ProjectReference.minLength(new int[] {8, 12, 5, 20, 9, 14, 3}, 50)));
    System.out.println(
        "5. Driver rate needed: "
            + display(Week9ProjectReference.min_rate(new int[] {120, 45, 300, 80}, 8)));
    System.out.println(
        "6. Surge cap: "
            + display(
                Week12ProjectReference.slidingMax(new int[] {10, 12, 15, 13, 18, 11, 10}, 3)));
  }
}
