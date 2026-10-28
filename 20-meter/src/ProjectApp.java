import java.util.*;

/** WattWise: Smart Electricity Billing — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("WattWise: Smart Electricity Billing");
    System.out.println(
        "1. Duplicate readings: "
            + display(CoreBasics.repeats(new String[] {"301", "302", "303", "302", "304", "302"})));
    System.out.println(
        "2. Recharge packs: "
            + display(W04CatalogueReference.minCoins(new int[] {50, 120, 200}, 370)));
    System.out.println(
        "3. Bill divisibility: "
            + display(W05CatalogueReference.count(new long[] {4L, 5L, 0L, 3L, 2L, 1L}, 5)));
    System.out.println(
        "4. Peak demand: "
            + display(Week7ProjectReference.bestWindow(new int[] {12, 15, 9, 22, 18, 30, 14}, 3)));
    System.out.println(
        "5. Slab lookup: "
            + display(W08CatalogueReference.lower(new int[] {100, 200, 300, 500}, 260)));
    System.out.println(
        "6. Rolling maximum load: "
            + display(Week12ProjectReference.slidingMax(new int[] {12, 15, 9, 22, 18, 30, 14}, 3)));
  }
}
