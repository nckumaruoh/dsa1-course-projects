import java.util.*;

/** SafePay: UPI Fraud Monitor — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("SafePay: UPI Fraud Monitor");
    System.out.println(
        "1. Duplicate transactions: "
            + display(
                CoreBasics.repeats(new String[] {"7001", "7002", "7003", "7002", "7004", "7002"})));
    System.out.println(
        "2. Matching a flagged amount: "
            + display(W04CatalogueReference.countSubsets(new int[] {20, 30, 50, 40, 10, 60}, 100)));
    System.out.println(
        "3. Exact-amount runs: "
            + display(
                W05CatalogueReference.count(
                    new long[] {2000L, 3000L, 5000L, 4000L, 1000L, 5000L, 5000L}, 10000L)));
    System.out.println(
        "4. Fastest Rs 50 000: "
            + display(Week7ProjectReference.minLength(new int[] {5, 12, 8, 20, 15, 3, 25}, 50)));
    System.out.println(
        "5. Transactions in a window: "
            + display(
                W08CatalogueReference.count(new int[] {5, 18, 30, 31, 44, 59, 60, 75}, 30, 60)));
    System.out.println(
        "6. Rolling maximum amount: "
            + display(
                Week12ProjectReference.slidingMax(
                    new int[] {500, 1200, 300, 9000, 400, 350, 8000}, 3)));
  }
}
