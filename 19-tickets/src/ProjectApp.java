import java.util.*;

/** SeatSure: Event Ticket Booking — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("SeatSure: Event Ticket Booking");
    System.out.println(
        "1. Double bookings: "
            + display(CoreBasics.repeats(new String[] {"14", "15", "16", "15", "17", "14"})));
    System.out.println(
        "2. Fill a row exactly: "
            + display(W04CatalogueReference.combinations(new int[] {2, 3, 5, 5, 7, 8}, 10)));
    System.out.println(
        "3. Segment occupancy: "
            + display(
                W05CatalogueReference.apply(
                    7,
                    new int[][] {
                      new int[] {0, 6, 3},
                      new int[] {1, 3, 2},
                      new int[] {2, 5, 1},
                      new int[] {5, 6, 1}
                    })));
    System.out.println(
        "4. Most shows in one venue: "
            + display(
                W06CatalogueReference.maxSlots(
                    new int[][] {
                      new int[] {10, 12},
                      new int[] {11, 13},
                      new int[] {12, 14},
                      new int[] {14, 16},
                      new int[] {13, 17}
                    })));
    System.out.println(
        "5. Price tier: " + display(W08CatalogueReference.lower(new int[] {5, 10, 20, 30}, 17)));
    System.out.println(
        "6. Merge holds: "
            + display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {1, 4},
                      new int[] {2, 6},
                      new int[] {8, 10},
                      new int[] {9, 12},
                      new int[] {15, 18}
                    })));
  }
}
