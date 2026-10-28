import java.util.*;

/** AirWatch: City AQI Monitor — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("AirWatch: City AQI Monitor");
    System.out.println(
        "1. Hours per category: "
            + display(CoreBasics.frequent(new int[] {2, 3, 3, 4, 3, 2, 4, 3})));
    System.out.println(
        "2. Polluted zone size: "
            + display(CoreBasics.region(new String[] {"..#.", ".##.", "...#", "#..."}, 0, 0)));
    System.out.println(
        "3. Pollution in a city block: "
            + display(
                W05CatalogueReference.rectangle(
                    new long[][] {
                      new long[] {120L, 145L, 180L, 90L},
                      new long[] {210L, 260L, 230L, 110L},
                      new long[] {190L, 150L, 140L, 95L}
                    },
                    0,
                    1,
                    1,
                    2)));
    System.out.println(
        "4. Worst 3-hour window: "
            + display(
                Week7ProjectReference.bestWindow(
                    new int[] {120, 145, 180, 210, 260, 230, 190, 150}, 3)));
    System.out.println(
        "5. Hours in a band: "
            + display(
                W08CatalogueReference.count(
                    new int[] {120, 145, 150, 180, 190, 210, 230, 260, 310}, 200, 300)));
    System.out.println(
        "6. Hours until it gets worse: "
            + display(
                Week12ProjectReference.waitDays(new int[] {120, 145, 130, 110, 160, 150, 170})));
  }
}
