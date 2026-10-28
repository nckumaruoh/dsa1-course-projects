import java.util.*;

/** RainCatch: Rooftop Water Harvesting — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("RainCatch: Rooftop Water Harvesting");
    System.out.println(
        "1. Rainfall bands: "
            + display(CoreBasics.frequent(new int[] {0, 1, 2, 3, 1, 0, 2, 3, 0})));
    System.out.println(
        "2. Flooded region: "
            + display(CoreBasics.region(new String[] {"..#.", ".##.", "...#", "#..."}, 0, 0)));
    System.out.println(
        "3. Rainfall over a period: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {0L, 12L, 35L, 60L, 8L, 0L, 22L, 41L},
                    new int[][] {new int[] {1, 4}, new int[] {0, 7}})));
    System.out.println(
        "4. Water trapped: "
            + display(
                W06CatalogueReference.water(new int[] {12, 4, 7, 2, 9, 3, 3, 15, 6, 8, 1, 10})));
    System.out.println(
        "5. Pump capacity: "
            + display(
                Week9ProjectReference.min_capacity(new int[] {12, 35, 60, 8, 22, 41, 18}, 4)));
    System.out.println(
        "6. Largest flat roof area: "
            + display(Week12ProjectReference.maxRectangle(new int[] {2, 1, 5, 6, 2, 3})));
  }
}
