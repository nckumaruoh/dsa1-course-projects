import java.util.*;

/** CodeArena: Mini Online Judge — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("CodeArena: Mini Online Judge");
    System.out.println(
        "1. Verdict statistics: "
            + display(CoreBasics.frequent(new int[] {1, 0, 1, 2, 1, 0, 0, 1})));
    System.out.println(
        "2. Problem sets worth 100: "
            + display(W04CatalogueReference.countSubsets(new int[] {20, 30, 50, 40, 10, 60}, 100)));
    System.out.println(
        "3. Submissions per minute range: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {14L, 22L, 35L, 41L, 28L, 19L},
                    new int[][] {new int[] {0, 2}, new int[] {2, 5}})));
    System.out.println(
        "4. Plagiarism score: " + display(Week7ProjectReference.lcs("forijsum", "forjisum")));
    System.out.println(
        "5. Judge servers: "
            + display(Week9ProjectReference.min_capacity(new int[] {12, 30, 18, 25, 40, 15}, 3)));
    System.out.println(
        "6. Expression tests: " + display(Week12ProjectReference.evaluate("3+4*(2-1)*5")));
  }
}
