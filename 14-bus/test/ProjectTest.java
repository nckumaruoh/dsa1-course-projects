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
        ProjectApp.display(CoreBasics.frequent(new int[] {5, 2, 5, 9, 2, 5, 1}))
            .equals("most frequent=5 (count 3), distinct=4"));
    ck(
        ProjectApp.display(
                W04CatalogueReference.paths(new String[] {"....", ".#..", "...#", "#..."}))
            .equals("3"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.apply(
                    6,
                    new int[][] {new int[] {0, 2, 12}, new int[] {1, 4, 8}, new int[] {3, 5, 15}}))
            .equals("[12, 20, 20, 23, 23, 15]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(
        ProjectApp.display(
                W06CatalogueReference.maxSlots(
                    new int[][] {
                      new int[] {0, 40},
                      new int[] {30, 70},
                      new int[] {45, 90},
                      new int[] {95, 130},
                      new int[] {70, 100}
                    }))
            .equals("3"));
    ck(W06CatalogueReference.maxSlots(new int[][] {{1, 3}, {3, 5}}) == 2);
    ck(W06CatalogueReference.maxSlots(new int[][] {}) == 0);
    ck(
        ProjectApp.display(Week9ProjectReference.max_min_gap(new int[] {0, 4, 9, 15, 22, 30}, 3))
            .equals("15"));
    ck(Week9ProjectReference.max_min_gap(new int[] {0, 4, 9, 15, 22, 30}, 3) == 15);
    ck(Week9ProjectReference.max_min_gap(new int[] {0, 30}, 2) == 30);
    rejects(() -> Week9ProjectReference.max_min_gap(new int[] {0, 30}, 1));
    ck(
        ProjectApp.display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {6, 10},
                      new int[] {9, 12},
                      new int[] {14, 18},
                      new int[] {17, 21},
                      new int[] {12, 13}
                    }))
            .equals("[[6, 13], [14, 21]]"));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 2}, {2, 3}}),
            new int[][] {{1, 3}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 4}, {2, 3}}),
            new int[][] {{1, 4}}));
    ck(Week10ProjectReference.mergeIntervals(new int[][] {}).length == 0);
    rejects(() -> Week10ProjectReference.mergeIntervals(new int[][] {{3, 1}}));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
