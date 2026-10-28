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
                CoreBasics.repeats(new String[] {"7", "3", "7", "9", "7", "3", "11", "7"}))
            .equals("repeats=4, distinct=4, first repeat=7"));
    ck(
        ProjectApp.display(W04CatalogueReference.minCoins(new int[] {50, 100, 200}, 450))
            .equals("3"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rangeSums(
                    new long[] {120L, 340L, 560L, 410L, 220L, 90L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 5}}))
            .equals("[1310, 1740]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    pairCheck(
        new int[] {499, 650, 250, 750, 300, 820},
        1000,
        W06CatalogueReference.pair(new int[] {499, 650, 250, 750, 300, 820}, 1000));
    pairCheck(new int[] {2, 7, 4, 5}, 9, W06CatalogueReference.pair(new int[] {2, 7, 4, 5}, 9));
    pairCheck(new int[] {3}, 6, W06CatalogueReference.pair(new int[] {3}, 6));
    ck(
        ProjectApp.display(Week9ProjectReference.min_rate(new int[] {120, 45, 300, 80}, 6))
            .equals("120"));
    ck(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6) == 23);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 1) == -1);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 2) == 7);
    ck(
        ProjectApp.display(
                Week11ProjectReference.simulate(new int[] {1, 2, 3, 1, 4, 2, 1, 5, 1}, 3))
            .equals("hits=3 of 9, final order (most recent first) [1, 5, 2]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
