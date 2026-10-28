import java.util.*;

/** PlaceMe: Campus Placement Scheduler — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("PlaceMe: Campus Placement Scheduler");
    System.out.println(
        "1. Duplicate registrations: "
            + display(
                CoreBasics.repeats(new String[] {"2201", "2202", "2203", "2201", "2204", "2202"})));
    System.out.println(
        "2. Round plans: "
            + display(W04CatalogueReference.combinations(new int[] {30, 45, 60, 15, 30, 90}, 120)));
    System.out.println(
        "3. Rooms needed per hour: "
            + display(
                W05CatalogueReference.apply(
                    8,
                    new int[][] {
                      new int[] {0, 2, 1},
                      new int[] {1, 3, 1},
                      new int[] {2, 5, 1},
                      new int[] {4, 7, 1}
                    })));
    System.out.println(
        "4. Most interviews for one student: "
            + display(
                W06CatalogueReference.maxSlots(
                    new int[][] {
                      new int[] {9, 10},
                      new int[] {9, 12},
                      new int[] {10, 11},
                      new int[] {11, 13},
                      new int[] {12, 14},
                      new int[] {13, 15}
                    })));
    System.out.println(
        "5. Eligibility cut-off: "
            + display(
                W08CatalogueReference.count(
                    new int[] {62, 68, 71, 75, 78, 81, 84, 90, 93}, 75, 90)));
    System.out.println(
        "6. Merge room bookings: "
            + display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {9, 11},
                      new int[] {10, 12},
                      new int[] {13, 15},
                      new int[] {14, 17},
                      new int[] {18, 19}
                    })));
  }
}
