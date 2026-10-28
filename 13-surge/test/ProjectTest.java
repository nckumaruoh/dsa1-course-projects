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
                CoreBasics.repeats(new String[] {"88", "91", "88", "93", "95", "91", "88"}))
            .equals("repeats=3, distinct=4, first repeat=88"));
    ck(ProjectApp.display(W04CatalogueReference.ways(new int[] {2, 3, 4}, 10)).equals("17"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.bestInterval(new long[] {-3L, 4L, 6L, -2L, 5L, -8L, 3L, 2L}))
            .equals("Interval[value=13, left=1, right=4]"));
    ck(
        W05CatalogueReference.bestInterval(new long[] {-2, -1})
            .equals(new W05CatalogueReference.Interval(-1, 1, 1)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {4})
            .equals(new W05CatalogueReference.Interval(4, 0, 0)));
    ck(
        ProjectApp.display(Week7ProjectReference.minLength(new int[] {8, 12, 5, 20, 9, 14, 3}, 50))
            .equals("5"));
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    ck(
        ProjectApp.display(Week9ProjectReference.min_rate(new int[] {120, 45, 300, 80}, 8))
            .equals("80"));
    ck(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6) == 23);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 1) == -1);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 2) == 7);
    ck(
        ProjectApp.display(
                Week12ProjectReference.slidingMax(new int[] {10, 12, 15, 13, 18, 11, 10}, 3))
            .equals("[15, 15, 18, 18, 18]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
