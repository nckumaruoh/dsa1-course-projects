import java.util.*;

/** DriveDiet: Cloud Storage Cleaner — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("DriveDiet: Cloud Storage Cleaner");
    System.out.println(
        "1. Duplicate files: "
            + display(CoreBasics.repeats(new String[] {"a9", "c4", "a9", "f1", "c4", "a9"})));
    System.out.println(
        "2. Folder sizes: "
            + display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        5,
                        new CoreBasics.Node(
                            1200, new CoreBasics.Node(800), new CoreBasics.Node(950)),
                        new CoreBasics.Node(300)))));
    System.out.println(
        "3. Merge device listings: "
            + display(W06CatalogueReference.merge(new int[] {2, 5, 9, 14}, new int[] {3, 5, 11})));
    System.out.println(
        "4. Find a name pattern: "
            + display(
                Week7ProjectReference.count("/img/2025/img_01.jpg /img/2026/img_02.jpg", "img")));
    System.out.println(
        "5. Sync bandwidth: "
            + display(
                Week9ProjectReference.min_capacity(new int[] {450, 200, 380, 120, 260, 300}, 4)));
    System.out.println(
        "6. Thumbnail cache: "
            + display(Week11ProjectReference.simulate(new int[] {1, 2, 1, 3, 2, 1, 3}, 2)));
  }
}
