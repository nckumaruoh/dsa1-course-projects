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
        ProjectApp.display(CoreBasics.frequent(new int[] {2, 3, 3, 4, 3, 2, 4, 3}))
            .equals("most frequent=3 (count 4), distinct=3"));
    ck(
        ProjectApp.display(CoreBasics.region(new String[] {"..#.", ".##.", "...#", "#..."}, 0, 0))
            .equals("9"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rectangle(
                    new long[][] {
                      new long[] {120L, 145L, 180L, 90L},
                      new long[] {210L, 260L, 230L, 110L},
                      new long[] {190L, 150L, 140L, 95L}
                    },
                    0,
                    1,
                    1,
                    2))
            .equals("815"));
    ck(W05CatalogueReference.rectangle(new long[][] {{1, 2}, {3, 4}}, 0, 1, 1, 1) == 6);
    ck(W05CatalogueReference.rectangle(new long[][] {{5}}, 0, 0, 0, 0) == 5);
    ck(
        ProjectApp.display(
                Week7ProjectReference.bestWindow(
                    new int[] {120, 145, 180, 210, 260, 230, 190, 150}, 3))
            .equals("[700, 3]"));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        ProjectApp.display(
                W08CatalogueReference.count(
                    new int[] {120, 145, 150, 180, 190, 210, 230, 260, 310}, 200, 300))
            .equals("3"));
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(
        ProjectApp.display(
                Week12ProjectReference.waitDays(new int[] {120, 145, 130, 110, 160, 150, 170}))
            .equals("[1, 3, 2, 1, 2, 1, 0]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
