import java.util.*;

/** CareQueue: Hospital OPD Tokens — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("CareQueue: Hospital OPD Tokens");
    System.out.println(
        "1. Duplicate tokens: "
            + display(CoreBasics.repeats(new String[] {"17", "18", "19", "18", "20", "21", "17"})));
    System.out.println(
        "2. Department totals: "
            + display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        0,
                        new CoreBasics.Node(40, new CoreBasics.Node(25)),
                        new CoreBasics.Node(35)))));
    System.out.println(
        "3. Patients in any hours: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {30L, 45L, 20L, 38L, 52L, 26L},
                    new int[][] {new int[] {0, 2}, new int[] {3, 5}})));
    System.out.println(
        "4. Busiest two hours: "
            + display(Week7ProjectReference.bestWindow(new int[] {30, 45, 20, 38, 52, 26}, 2)));
    System.out.println(
        "5. Consultation capacity: "
            + display(
                Week9ProjectReference.min_capacity(
                    new int[] {30, 45, 20, 38, 52, 26, 18, 12, 9}, 5)));
    System.out.println(
        "6. Token queue: "
            + display(Week12ProjectReference.queueSim(new int[] {2, 1, 1, 0, 2, 1, 0, 0}, 4, 2)));
  }
}
