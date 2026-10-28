import java.util.*;

/** StepUp: Fitness Tracker Coach — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("StepUp: Fitness Tracker Coach");
    System.out.println(
        "1. Favourite activity: "
            + display(CoreBasics.frequent(new int[] {2, 1, 2, 3, 2, 1, 1, 2})));
    System.out.println(
        "2. Exactly 60 minutes: "
            + display(W04CatalogueReference.countSubsets(new int[] {20, 30, 10, 40, 30}, 60)));
    System.out.println(
        "3. Best streak above goal: "
            + display(
                W05CatalogueReference.bestInterval(
                    new long[] {-800L, 1450L, -2700L, 3100L, 800L, 6020L, -4700L})));
    System.out.println(
        "4. Best 7-day week: "
            + display(
                Week7ProjectReference.bestWindow(
                    new int[] {8200, 10450, 6300, 12100, 9800, 15020, 4300, 7600, 11200, 9900},
                    7)));
    System.out.println(
        "5. A fair daily goal: "
            + display(
                Week9ProjectReference.max_goal(
                    new int[] {8200, 10450, 6300, 12100, 9800, 15020, 4300}, 5)));
    System.out.println(
        "6. Peak heart rate: "
            + display(
                Week12ProjectReference.slidingMax(
                    new int[] {72, 80, 95, 110, 104, 98, 120, 90}, 3)));
  }
}
