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
        ProjectApp.display(CoreBasics.frequent(new int[] {1, 4, 1, 0, 6, 1, 4, 1, 2, 0}))
            .equals("most frequent=1 (count 4), distinct=5"));
    ck(ProjectApp.display(W04CatalogueReference.ways(new int[] {1, 2, 4, 6}, 8)).equals("60"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.bestInterval(new long[] {-2L, 1L, -4L, 4L, -1L, 7L, -5L, 2L}))
            .equals("Interval[value=10, left=3, right=5]"));
    ck(
        W05CatalogueReference.bestInterval(new long[] {-2, -1})
            .equals(new W05CatalogueReference.Interval(-1, 1, 1)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {4})
            .equals(new W05CatalogueReference.Interval(4, 0, 0)));
    ck(
        ProjectApp.display(
                Week7ProjectReference.bestWindow(new int[] {6, 9, 4, 12, 7, 15, 3, 10}, 3))
            .equals("[34, 3]"));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        ProjectApp.display(
                W08CatalogueReference.lower(new int[] {6, 15, 19, 31, 38, 53, 56, 66}, 50))
            .equals("5"));
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(
        ProjectApp.display(
                Week10ProjectReference.rank(
                    new String[] {"Rohit", "Gill", "Kohli", "Pant"},
                    new int[] {45, 52, 45, 38},
                    new int[] {30, 41, 28, 22},
                    false))
            .equals("[Gill, Kohli, Rohit, Pant]"));
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"A", "B", "C"}, new int[] {10, 10, 10}, new int[] {2, 2, 1}, false),
            new String[] {"C", "A", "B"}));
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"A", "B", "C"}, new int[] {10, 10, 10}, new int[] {2, 2, 1}, true),
            new String[] {"A", "B", "C"}));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
