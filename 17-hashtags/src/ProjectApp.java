import java.util.*;

/** TrendTag: Social Hashtag Trends — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("TrendTag: Social Hashtag Trends");
    System.out.println(
        "1. Most used tag: " + display(CoreBasics.frequent(new int[] {3, 1, 3, 5, 1, 3, 7})));
    System.out.println(
        "2. Thread engagement: "
            + display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        120,
                        new CoreBasics.Node(30, new CoreBasics.Node(12)),
                        new CoreBasics.Node(45)))));
    System.out.println(
        "3. Posts in any hours: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {540L, 820L, 1310L, 2200L, 1900L, 760L},
                    new int[][] {new int[] {2, 4}, new int[] {0, 5}})));
    System.out.println(
        "4. Variant grouping: "
            + display(
                Week7ProjectReference.groups(
                    new String[] {"iplfinal", "finalipl", "goat", "toga", "rcb", "bcr", "csk"})));
    System.out.println(
        "5. Posts in a time window: "
            + display(
                W08CatalogueReference.count(
                    new int[] {20, 75, 100, 130, 155, 199, 200, 260}, 100, 200)));
    System.out.println(
        "6. Top trends: "
            + display(
                Week10ProjectReference.topKLinear(new int[] {3, 1, 3, 5, 1, 3, 7, 5, 5, 3}, 3)));
  }
}
