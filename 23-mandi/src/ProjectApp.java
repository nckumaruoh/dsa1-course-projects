import java.util.*;

/** KisanRate: Mandi Crop-Price Tracker — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("KisanRate: Mandi Crop-Price Tracker");
    System.out.println(
        "1. Most traded crop: " + display(CoreBasics.frequent(new int[] {2, 5, 2, 7, 5, 2, 1})));
    System.out.println(
        "2. Load a truck: "
            + display(W04CatalogueReference.bestTotal(new int[] {350, 420, 280, 160, 510}, 1000)));
    System.out.println(
        "3. Best store-and-sell: "
            + display(
                W05CatalogueReference.bestTrade(
                    new long[] {1800L, 1650L, 1700L, 2100L, 1900L, 2400L, 2250L})));
    System.out.println(
        "4. Merge two mandis: "
            + display(
                W06CatalogueReference.merge(new int[] {1, 4, 6, 9}, new int[] {2, 3, 7, 10})));
    System.out.println(
        "5. Price on a date: "
            + display(W08CatalogueReference.lower(new int[] {1, 2, 3, 4, 6, 7, 9, 10}, 7)));
    System.out.println(
        "6. Days until a better price: "
            + display(
                Week12ProjectReference.waitDays(
                    new int[] {1800, 1650, 1700, 2100, 1900, 2400, 2250})));
  }
}
