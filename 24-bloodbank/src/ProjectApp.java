import java.util.*;

/** LifeLink: Blood Bank Inventory — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("LifeLink: Blood Bank Inventory");
    System.out.println(
        "1. Most requested group: "
            + display(CoreBasics.frequent(new int[] {0, 2, 0, 1, 0, 2, 3})));
    System.out.println(
        "2. Exact volume: "
            + display(
                W04CatalogueReference.countSubsets(new int[] {350, 450, 350, 250, 200}, 800)));
    System.out.println(
        "3. Units usable each day: "
            + display(
                W05CatalogueReference.apply(
                    7,
                    new int[][] {
                      new int[] {0, 4, 1},
                      new int[] {1, 2, 1},
                      new int[] {2, 6, 1},
                      new int[] {3, 5, 1}
                    })));
    System.out.println(
        "4. Merge centre stocks: "
            + display(
                W06CatalogueReference.merge(new int[] {3, 8, 11, 21}, new int[] {10, 11, 14, 15})));
    System.out.println(
        "5. Units expiring soon: "
            + display(
                W08CatalogueReference.count(new int[] {3, 8, 10, 11, 11, 14, 15, 21}, 10, 15)));
    System.out.println(
        "6. Request queue: "
            + display(Week12ProjectReference.queueSim(new int[] {1, 2, 0, 1, 2, 0}, 3, 2)));
  }
}
