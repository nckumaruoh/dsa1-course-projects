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
                CoreBasics.repeats(new String[] {"2201", "2202", "2203", "2201", "2204", "2202"}))
            .equals("repeats=2, distinct=4, first repeat=2201"));
    ck(
        ProjectApp.display(
                W04CatalogueReference.combinations(new int[] {30, 45, 60, 15, 30, 90}, 120))
            .equals("[[15, 30, 30, 45], [15, 45, 60], [30, 30, 60], [30, 90]]"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.apply(
                    8,
                    new int[][] {
                      new int[] {0, 2, 1},
                      new int[] {1, 3, 1},
                      new int[] {2, 5, 1},
                      new int[] {4, 7, 1}
                    }))
            .equals("[1, 2, 3, 2, 2, 2, 1, 1]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(
        ProjectApp.display(
                W06CatalogueReference.maxSlots(
                    new int[][] {
                      new int[] {9, 10},
                      new int[] {9, 12},
                      new int[] {10, 11},
                      new int[] {11, 13},
                      new int[] {12, 14},
                      new int[] {13, 15}
                    }))
            .equals("4"));
    ck(W06CatalogueReference.maxSlots(new int[][] {{1, 3}, {3, 5}}) == 2);
    ck(W06CatalogueReference.maxSlots(new int[][] {}) == 0);
    ck(
        ProjectApp.display(
                W08CatalogueReference.count(new int[] {62, 68, 71, 75, 78, 81, 84, 90, 93}, 75, 90))
            .equals("4"));
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(
        ProjectApp.display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {9, 11},
                      new int[] {10, 12},
                      new int[] {13, 15},
                      new int[] {14, 17},
                      new int[] {18, 19}
                    }))
            .equals("[[9, 12], [13, 17], [18, 19]]"));
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
