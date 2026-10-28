import java.util.*;

/** NoteNest: Text Editor Core — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("NoteNest: Text Editor Core");
    System.out.println(
        "1. Most used word: " + display(CoreBasics.frequent(new int[] {7, 3, 7, 1, 3, 7, 2})));
    System.out.println(
        "2. Autocorrect distance: "
            + display(W04CatalogueReference.distance("algoritm", "algorithm")));
    System.out.println(
        "3. Words in any paragraphs: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {120L, 85L, 240L, 60L, 175L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 4}})));
    System.out.println(
        "4. Find all: " + display(Week7ProjectReference.count("abracadabra abracadabra", "abra")));
    System.out.println(
        "5. Jump to an offset: "
            + display(W08CatalogueReference.lower(new int[] {40, 95, 130, 180, 260}, 140)));
    System.out.println(
        "6. Bracket matching: "
            + display(Week12ProjectReference.validBrackets("if (a[i] > b) { x = (y]; }")));
  }
}
