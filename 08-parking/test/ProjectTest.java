import java.util.*;

public class ProjectTest {
  static int checks;

  static void ck(boolean condition) {
    checks++;
    if (!condition) throw new AssertionError("project check " + checks);
  }

  static void rejects(Runnable r) {
    checks++;
    try {
      r.run();
    } catch (IllegalArgumentException expected) {
      return;
    }
    throw new AssertionError("expected rejection");
  }

  static void pairCheck(int[] input, int target, int[] actual) {
    boolean exists = false;
    for (int i = 0; i < input.length; i++)
      for (int j = i + 1; j < input.length; j++)
        if ((long) input[i] + input[j] == target) exists = true;
    ck(exists ? actual.length == 2 && (long) actual[0] + actual[1] == target : actual.length == 0);
    if (actual.length == 2) {
      int first = 0, second = 0;
      for (int x : input) {
        if (x == actual[0]) first++;
        if (x == actual[1]) second++;
      }
      ck(actual[0] == actual[1] ? first >= 2 : first > 0 && second > 0);
    }
  }

  static String canonical(List<List<String>> groups) {
    List<String> out = new ArrayList<>();
    for (List<String> group : groups) {
      List<String> copy = new ArrayList<>(group);
      Collections.sort(copy);
      out.add(copy.toString());
    }
    Collections.sort(out);
    return out.toString();
  }

  static void topCheck(int[] input, int k, int[] actual) {
    Map<Integer, Integer> f = new HashMap<>();
    for (int id : input) f.merge(id, 1, Integer::sum);
    ck(actual.length == k);
    Set<Integer> selected = new HashSet<>();
    int minimum = Integer.MAX_VALUE;
    for (int id : actual) {
      ck(f.containsKey(id) && selected.add(id));
      minimum = Math.min(minimum, f.get(id));
    }
    for (var e : f.entrySet()) if (!selected.contains(e.getKey())) ck(e.getValue() <= minimum);
  }

  public static void main(String[] args) {
    ck(
        ProjectApp.display(
                CoreBasics.repeats(new String[] {"3301", "4410", "3301", "5120", "4410", "6008"}))
            .equals("repeats=2, distinct=4, first repeat=3301"));
    ck(
        ProjectApp.display(W04CatalogueReference.paths(new String[] {"...", ".#.", "..."}))
            .equals("2"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rectangle(
                    new long[][] {
                      new long[] {1L, 0L, 1L, 1L},
                      new long[] {0L, 1L, 1L, 0L},
                      new long[] {1L, 1L, 0L, 1L}
                    },
                    0,
                    1,
                    1,
                    2))
            .equals("3"));
    ck(W05CatalogueReference.rectangle(new long[][] {{1, 2}, {3, 4}}, 0, 1, 1, 1) == 6);
    ck(W05CatalogueReference.rectangle(new long[][] {{5}}, 0, 0, 0, 0) == 5);
    ck(
        ProjectApp.display(
                W06CatalogueReference.maxSlots(
                    new int[][] {
                      new int[] {1, 3},
                      new int[] {2, 5},
                      new int[] {4, 6},
                      new int[] {6, 8},
                      new int[] {5, 9},
                      new int[] {8, 10}
                    }))
            .equals("4"));
    ck(W06CatalogueReference.maxSlots(new int[][] {{1, 3}, {3, 5}}) == 2);
    ck(W06CatalogueReference.maxSlots(new int[][] {}) == 0);
    ck(
        ProjectApp.display(W08CatalogueReference.lower(new int[] {60, 120, 180, 240, 360}, 135))
            .equals("2"));
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(
        ProjectApp.display(Week12ProjectReference.queueSim(new int[] {1, 2, 2, 1, 0, 1, 0}, 2, 1))
            .equals("served=7, total wait=28 min, longest wait=7 min"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
