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
        ProjectApp.display(CoreBasics.frequent(new int[] {2, 1, 2, 3, 2, 1, 1, 2}))
            .equals("most frequent=2 (count 4), distinct=3"));
    ck(
        ProjectApp.display(W04CatalogueReference.countSubsets(new int[] {20, 30, 10, 40, 30}, 60))
            .equals("4"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.bestInterval(
                    new long[] {-800L, 1450L, -2700L, 3100L, 800L, 6020L, -4700L}))
            .equals("Interval[value=9920, left=3, right=5]"));
    ck(
        W05CatalogueReference.bestInterval(new long[] {-2, -1})
            .equals(new W05CatalogueReference.Interval(-1, 1, 1)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {4})
            .equals(new W05CatalogueReference.Interval(4, 0, 0)));
    ck(
        ProjectApp.display(
                Week7ProjectReference.bestWindow(
                    new int[] {8200, 10450, 6300, 12100, 9800, 15020, 4300, 7600, 11200, 9900}, 7))
            .equals("[69920, 3]"));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        ProjectApp.display(
                Week9ProjectReference.max_goal(
                    new int[] {8200, 10450, 6300, 12100, 9800, 15020, 4300}, 5))
            .equals("8200"));
    ck(Week9ProjectReference.max_goal(new int[] {8, 10, 6}, 2) == 8);
    ck(Week9ProjectReference.max_goal(new int[] {8, 8, 6}, 2) == 8);
    ck(Week9ProjectReference.max_goal(new int[] {8, 8, 6}, 4) == -1);
    ck(
        ProjectApp.display(
                Week12ProjectReference.slidingMax(new int[] {72, 80, 95, 110, 104, 98, 120, 90}, 3))
            .equals("[95, 110, 110, 110, 120, 120]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
