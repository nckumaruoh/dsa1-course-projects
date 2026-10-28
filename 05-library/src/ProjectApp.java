import java.util.*;

/** ShelfSense: Library Circulation — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("ShelfSense: Library Circulation");
    System.out.println(
        "1. Double scans at the gate: "
            + display(
                CoreBasics.repeats(new String[] {"1450", "2210", "1450", "3999", "2210", "1450"})));
    System.out.println(
        "2. Category tree totals: "
            + display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        120,
                        new CoreBasics.Node(45, new CoreBasics.Node(30)),
                        new CoreBasics.Node(60)))));
    System.out.println(
        "3. Books out each day: "
            + display(
                W05CatalogueReference.apply(
                    8,
                    new int[][] {
                      new int[] {0, 3, 1},
                      new int[] {2, 6, 1},
                      new int[] {5, 7, 1},
                      new int[] {1, 1, 1}
                    })));
    System.out.println(
        "4. Keyword search: "
            + display(
                Week7ProjectReference.count(
                    "data structures; data science; big data analytics", "data")));
    System.out.println(
        "5. Find a book: "
            + display(
                W08CatalogueReference.lower(
                    new int[] {1002, 1450, 2210, 3999, 4417, 5120, 7004}, 4417)));
    System.out.println(
        "6. Return-counter queue: "
            + display(Week12ProjectReference.queueSim(new int[] {2, 1, 0, 2, 1, 1, 0, 0}, 2, 1)));
  }
}
