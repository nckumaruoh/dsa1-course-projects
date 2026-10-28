import java.util.*;

/** BusBuddy: Route and Timetable Planner — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("BusBuddy: Route and Timetable Planner");
    System.out.println(
        "1. Busiest boarding stop: "
            + display(CoreBasics.frequent(new int[] {5, 2, 5, 9, 2, 5, 1})));
    System.out.println(
        "2. Routes through the grid: "
            + display(W04CatalogueReference.paths(new String[] {"....", ".#..", "...#", "#..."})));
    System.out.println(
        "3. Passengers on board: "
            + display(
                W05CatalogueReference.apply(
                    6,
                    new int[][] {
                      new int[] {0, 2, 12}, new int[] {1, 4, 8}, new int[] {3, 5, 15}
                    })));
    System.out.println(
        "4. Most trips for one bus: "
            + display(
                W06CatalogueReference.maxSlots(
                    new int[][] {
                      new int[] {0, 40},
                      new int[] {30, 70},
                      new int[] {45, 90},
                      new int[] {95, 130},
                      new int[] {70, 100}
                    })));
    System.out.println(
        "5. Depot spacing: "
            + display(Week9ProjectReference.max_min_gap(new int[] {0, 4, 9, 15, 22, 30}, 3)));
    System.out.println(
        "6. Operating hours: "
            + display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {6, 10},
                      new int[] {9, 12},
                      new int[] {14, 18},
                      new int[] {17, 21},
                      new int[] {12, 13}
                    })));
  }
}
