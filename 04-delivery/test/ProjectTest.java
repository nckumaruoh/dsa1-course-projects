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
                CoreBasics.repeats(new String[] {"41", "42", "43", "42", "44", "45", "41"}))
            .equals("repeats=2, distinct=5, first repeat=42"));
    ck(
        ProjectApp.display(W04CatalogueReference.bestTotal(new int[] {3, 4, 5, 7, 2}, 10))
            .equals("10"));
    pairCheck(
        new int[] {2, 7, 4, 5, 3, 8},
        9,
        W06CatalogueReference.pair(new int[] {2, 7, 4, 5, 3, 8}, 9));
    pairCheck(new int[] {2, 7, 4, 5}, 9, W06CatalogueReference.pair(new int[] {2, 7, 4, 5}, 9));
    pairCheck(new int[] {3}, 6, W06CatalogueReference.pair(new int[] {3}, 6));
    ck(
        ProjectApp.display(Week7ProjectReference.minLength(new int[] {8, 12, 5, 20, 9, 14, 3}, 50))
            .equals("5"));
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    ck(
        ProjectApp.display(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6))
            .equals("23"));
    ck(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6) == 23);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 1) == -1);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 2) == 7);
    ck(
        ProjectApp.display(
                Week10ProjectReference.mergeIntervals(
                    new int[][] {
                      new int[] {9, 13},
                      new int[] {12, 15},
                      new int[] {18, 22},
                      new int[] {21, 23},
                      new int[] {14, 16}
                    }))
            .equals("[[9, 16], [18, 23]]"));
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
