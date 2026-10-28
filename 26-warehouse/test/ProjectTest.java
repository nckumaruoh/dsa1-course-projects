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
    ck(ProjectApp.display(CoreBasics.pairs(new int[] {3, 7, 5, 5, 2, 8, 6}, 10)).equals("3"));
    ck(
        ProjectApp.display(W04CatalogueReference.bestTotal(new int[] {6, 4, 9, 3, 7}, 15))
            .equals("15"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rangeSums(
                    new long[] {140L, 220L, 310L, 280L, 190L, 120L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 5}}))
            .equals("[810, 1260]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        ProjectApp.display(Week7ProjectReference.minLength(new int[] {7, 2, 5, 10, 8, 3, 6}, 30))
            .equals("5"));
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    ck(
        ProjectApp.display(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8, 3, 6}, 3))
            .equals("17"));
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(
        ProjectApp.display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {8, 10},
                      new int[] {9, 11},
                      new int[] {13, 14},
                      new int[] {11, 12},
                      new int[] {15, 17}
                    }))
            .equals("[[8, 12], [13, 14], [15, 17]]"));
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
