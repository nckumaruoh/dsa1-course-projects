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
                CoreBasics.repeats(new String[] {"7001", "7002", "7003", "7002", "7004", "7002"}))
            .equals("repeats=2, distinct=4, first repeat=7002"));
    ck(
        ProjectApp.display(
                W04CatalogueReference.countSubsets(new int[] {20, 30, 50, 40, 10, 60}, 100))
            .equals("5"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.count(
                    new long[] {2000L, 3000L, 5000L, 4000L, 1000L, 5000L, 5000L}, 10000L))
            .equals("4"));
    ck(W05CatalogueReference.count(new long[] {1, 1, 1}, 2L) == 2);
    ck(W05CatalogueReference.count(new long[] {0, 0}, 0L) == 3);
    ck(
        ProjectApp.display(Week7ProjectReference.minLength(new int[] {5, 12, 8, 20, 15, 3, 25}, 50))
            .equals("4"));
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    ck(
        ProjectApp.display(
                W08CatalogueReference.count(new int[] {5, 18, 30, 31, 44, 59, 60, 75}, 30, 60))
            .equals("4"));
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(
        ProjectApp.display(
                Week12ProjectReference.slidingMax(
                    new int[] {500, 1200, 300, 9000, 400, 350, 8000}, 3))
            .equals("[1200, 9000, 9000, 9000, 8000]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
