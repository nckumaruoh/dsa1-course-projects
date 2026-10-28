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
        ProjectApp.display(CoreBasics.frequent(new int[] {3, 1, 3, 5, 1, 3, 7}))
            .equals("most frequent=3 (count 3), distinct=4"));
    ck(
        ProjectApp.display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        120,
                        new CoreBasics.Node(30, new CoreBasics.Node(12)),
                        new CoreBasics.Node(45))))
            .equals("207"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rangeSums(
                    new long[] {540L, 820L, 1310L, 2200L, 1900L, 760L},
                    new int[][] {new int[] {2, 4}, new int[] {0, 5}}))
            .equals("[5410, 7530]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        canonical(
                Week7ProjectReference.groups(
                    new String[] {"iplfinal", "finalipl", "goat", "toga", "rcb", "bcr", "csk"}))
            .equals("[[bcr, rcb], [csk], [finalipl, iplfinal], [goat, toga]]"));
    ck(
        canonical(Week7ProjectReference.groups(new String[] {"ab", "ba", "a"}))
            .equals("[[a], [ab, ba]]"));
    ck(canonical(Week7ProjectReference.groups(new String[] {""})).equals("[[]]"));
    ck(
        ProjectApp.display(
                W08CatalogueReference.count(
                    new int[] {20, 75, 100, 130, 155, 199, 200, 260}, 100, 200))
            .equals("4"));
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    topCheck(
        new int[] {3, 1, 3, 5, 1, 3, 7, 5, 5, 3},
        3,
        Week10ProjectReference.topKLinear(new int[] {3, 1, 3, 5, 1, 3, 7, 5, 5, 3}, 3));
    topCheck(
        new int[] {1, 1, 2, 2, 3},
        2,
        Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 2));
    ck(Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 0).length == 0);
    rejects(() -> Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 4));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
