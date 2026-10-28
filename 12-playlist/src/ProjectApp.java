import java.util.*;

/** TuneLoop: Music Playlist Engine — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("TuneLoop: Music Playlist Engine");
    System.out.println(
        "1. Most played song: " + display(CoreBasics.frequent(new int[] {4, 2, 4, 7, 9, 2, 4, 7})));
    System.out.println("2. Party elimination game: " + display(CoreBasics.winner(7, 3)));
    System.out.println(
        "3. Queue duration: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {210L, 185L, 240L, 200L, 260L, 195L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 5}})));
    System.out.println(
        "4. At most two artists: "
            + display(Week7ProjectReference.longest(new int[] {3, 3, 1, 2, 1, 1, 2, 3, 3, 4}, 2)));
    System.out.println(
        "5. Song at a given time: "
            + display(
                W08CatalogueReference.lower(new int[] {210, 395, 635, 835, 1095, 1290}, 700)));
    System.out.println(
        "6. Offline cache: "
            + display(Week11ProjectReference.simulate(new int[] {4, 2, 4, 7, 9, 2, 4, 7}, 3)));
  }
}
