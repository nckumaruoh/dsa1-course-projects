import java.util.*;

/** PackRight: Warehouse Order Packing — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("PackRight: Warehouse Order Packing");
    System.out.println(
        "1. Pairs for a box: " + display(CoreBasics.pairs(new int[] {3, 7, 5, 5, 2, 8, 6}, 10)));
    System.out.println(
        "2. Best box fill: "
            + display(W04CatalogueReference.bestTotal(new int[] {6, 4, 9, 3, 7}, 15)));
    System.out.println(
        "3. Units in any hour range: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {140L, 220L, 310L, 280L, 190L, 120L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 5}})));
    System.out.println(
        "4. Fill a truck fastest: "
            + display(Week7ProjectReference.minLength(new int[] {7, 2, 5, 10, 8, 3, 6}, 30)));
    System.out.println(
        "5. Truck capacity: "
            + display(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8, 3, 6}, 3)));
    System.out.println(
        "6. Dock slots: "
            + display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {8, 10},
                      new int[] {9, 11},
                      new int[] {13, 14},
                      new int[] {11, 12},
                      new int[] {15, 17}
                    })));
  }
}
