import java.util.*;

/** ParkSmart: Mall Parking Occupancy — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("ParkSmart: Mall Parking Occupancy");
    System.out.println(
        "1. Re-entry without exit: "
            + display(
                CoreBasics.repeats(new String[] {"3301", "4410", "3301", "5120", "4410", "6008"})));
    System.out.println(
        "2. Routes to a free slot: "
            + display(W04CatalogueReference.paths(new String[] {"...", ".#.", "..."})));
    System.out.println(
        "3. Free slots in a zone: "
            + display(
                W05CatalogueReference.rectangle(
                    new long[][] {
                      new long[] {1L, 0L, 1L, 1L},
                      new long[] {0L, 1L, 1L, 0L},
                      new long[] {1L, 1L, 0L, 1L}
                    },
                    0,
                    1,
                    1,
                    2)));
    System.out.println(
        "4. EV charger bookings: "
            + display(
                W06CatalogueReference.maxSlots(
                    new int[][] {
                      new int[] {1, 3},
                      new int[] {2, 5},
                      new int[] {4, 6},
                      new int[] {6, 8},
                      new int[] {5, 9},
                      new int[] {8, 10}
                    })));
    System.out.println(
        "5. Fee slab lookup: "
            + display(W08CatalogueReference.lower(new int[] {60, 120, 180, 240, 360}, 135)));
    System.out.println(
        "6. Entry gate queue: "
            + display(Week12ProjectReference.queueSim(new int[] {1, 2, 2, 1, 0, 1, 0}, 2, 1)));
  }
}
