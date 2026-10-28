import java.util.*;

/** TypeRight: Spell Checker and Autocomplete — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("TypeRight: Spell Checker and Autocomplete");
    System.out.println(
        "1. Letter frequencies: "
            + display(CoreBasics.frequent(new int[] {4, 0, 4, 19, 4, 0, 18})));
    System.out.println(
        "2. Edit distance: " + display(W04CatalogueReference.distance("recieve", "receive")));
    System.out.println(
        "3. Merge two dictionaries: "
            + display(W06CatalogueReference.merge(new int[] {3, 8, 15, 21}, new int[] {5, 8, 30})));
    System.out.println(
        "4. Similarity score: " + display(Week7ProjectReference.lcs("klconnect", "kluniversity")));
    System.out.println(
        "5. Autocomplete range: "
            + display(
                W08CatalogueReference.count(
                    new String[] {"bread", "chai", "chakli", "chana", "cheese", "curd"},
                    "cha",
                    "chb")));
    System.out.println(
        "6. Most common typos: "
            + display(Week10ProjectReference.topKLinear(new int[] {5, 2, 5, 8, 2, 5, 9, 2, 2}, 2)));
  }
}
