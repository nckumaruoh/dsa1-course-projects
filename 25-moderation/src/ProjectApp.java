import java.util.*;

/** SafeChat: Message Moderation — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("SafeChat: Message Moderation");
    System.out.println(
        "1. Repeated messages: "
            + display(CoreBasics.repeats(new String[] {"h1", "h2", "h1", "h1", "h3", "h2"})));
    System.out.println(
        "2. Disguised words: " + display(W04CatalogueReference.distance("sp4mmer", "spammer")));
    System.out.println(
        "3. Merge moderator flags: "
            + display(
                W06CatalogueReference.merge(new int[] {5, 12, 30}, new int[] {7, 12, 18, 40})));
    System.out.println(
        "4. Phrase filter: "
            + display(Week7ProjectReference.count("buy now buy now limited buy now", "buy now")));
    System.out.println(
        "5. Messages in a window: "
            + display(
                W08CatalogueReference.count(new int[] {2, 5, 10, 11, 12, 19, 20, 27}, 10, 20)));
    System.out.println(
        "6. Markup validation: "
            + display(Week12ProjectReference.validBrackets("*(bold [link])* {quote}")));
  }
}
