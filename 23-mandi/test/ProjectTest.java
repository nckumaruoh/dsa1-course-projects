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
        ProjectApp.display(CoreBasics.frequent(new int[] {2, 5, 2, 7, 5, 2, 1}))
            .equals("most frequent=2 (count 3), distinct=4"));
    ck(
        ProjectApp.display(
                W04CatalogueReference.bestTotal(new int[] {350, 420, 280, 160, 510}, 1000))
            .equals("950"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.bestTrade(
                    new long[] {1800L, 1650L, 1700L, 2100L, 1900L, 2400L, 2250L}))
            .equals("Interval[value=750, left=1, right=5]"));
    ck(
        W05CatalogueReference.bestTrade(new long[] {3, 1, 4})
            .equals(new W05CatalogueReference.Interval(3, 1, 2)));
    ck(
        W05CatalogueReference.bestTrade(new long[] {3, 2, 1})
            .equals(new W05CatalogueReference.Interval(0, -1, -1)));
    ck(
        ProjectApp.display(
                W06CatalogueReference.merge(new int[] {1, 4, 6, 9}, new int[] {2, 3, 7, 10}))
            .equals("[1, 2, 3, 4, 6, 7, 9, 10]"));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        ProjectApp.display(W08CatalogueReference.lower(new int[] {1, 2, 3, 4, 6, 7, 9, 10}, 7))
            .equals("5"));
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(
        ProjectApp.display(
                Week12ProjectReference.waitDays(
                    new int[] {1800, 1650, 1700, 2100, 1900, 2400, 2250}))
            .equals("[3, 1, 1, 2, 1, 0, 0]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
