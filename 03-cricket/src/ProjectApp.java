import java.util.*;

/** ScoreLens: Ball-by-Ball Cricket Analyzer — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("ScoreLens: Ball-by-Ball Cricket Analyzer");
    System.out.println(
        "1. Scoring-shot profile: "
            + display(CoreBasics.frequent(new int[] {1, 4, 1, 0, 6, 1, 4, 1, 2, 0})));
    System.out.println(
        "2. Ways to make a target: "
            + display(W04CatalogueReference.ways(new int[] {1, 2, 4, 6}, 8)));
    System.out.println(
        "3. Best momentum phase: "
            + display(
                W05CatalogueReference.bestInterval(
                    new long[] {-2L, 1L, -4L, 4L, -1L, 7L, -5L, 2L})));
    System.out.println(
        "4. Hottest 3-over spell: "
            + display(Week7ProjectReference.bestWindow(new int[] {6, 9, 4, 12, 7, 15, 3, 10}, 3)));
    System.out.println(
        "5. When did they reach 50?: "
            + display(W08CatalogueReference.lower(new int[] {6, 15, 19, 31, 38, 53, 56, 66}, 50)));
    System.out.println(
        "6. Leaderboard: "
            + display(
                Week10ProjectReference.rank(
                    new String[] {"Rohit", "Gill", "Kohli", "Pant"},
                    new int[] {45, 52, 45, 38},
                    new int[] {30, 41, 28, 22},
                    false)));
  }
}
