import java.util.*;

/** MessVoice: Hostel Feedback Analyzer — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("MessVoice: Hostel Feedback Analyzer");
    System.out.println(
        "1. Most common complaint word: "
            + display(CoreBasics.frequent(new int[] {12, 4, 12, 7, 4, 12, 9})));
    System.out.println(
        "2. Palindrome coupon codes: " + display(CoreBasics.palindrome("Mess-Code: edoc ssem")));
    System.out.println(
        "3. Merge two feedback forms: "
            + display(W06CatalogueReference.merge(new int[] {1, 3, 4, 8}, new int[] {2, 3, 6, 9})));
    System.out.println(
        "4. Keyword alerts: "
            + display(
                Week7ProjectReference.count("rice was cold food cold food again", "cold food")));
    System.out.println(
        "5. Ratings in a band: "
            + display(W08CatalogueReference.count(new int[] {1, 1, 2, 2, 3, 3, 3, 4, 5, 5}, 2, 4)));
    System.out.println(
        "6. Clean formatting: "
            + display(Week12ProjectReference.validBrackets("[veg](spicy){menu}[x(y])")));
  }
}
