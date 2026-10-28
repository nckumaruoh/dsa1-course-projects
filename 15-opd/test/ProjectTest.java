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
                CoreBasics.repeats(new String[] {"17", "18", "19", "18", "20", "21", "17"}))
            .equals("repeats=2, distinct=5, first repeat=18"));
    ck(
        ProjectApp.display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        0,
                        new CoreBasics.Node(40, new CoreBasics.Node(25)),
                        new CoreBasics.Node(35))))
            .equals("100"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rangeSums(
                    new long[] {30L, 45L, 20L, 38L, 52L, 26L},
                    new int[][] {new int[] {0, 2}, new int[] {3, 5}}))
            .equals("[95, 116]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        ProjectApp.display(Week7ProjectReference.bestWindow(new int[] {30, 45, 20, 38, 52, 26}, 2))
            .equals("[90, 3]"));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        ProjectApp.display(
                Week9ProjectReference.min_capacity(
                    new int[] {30, 45, 20, 38, 52, 26, 18, 12, 9}, 5))
            .equals("65"));
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(
        ProjectApp.display(
                Week12ProjectReference.queueSim(new int[] {2, 1, 1, 0, 2, 1, 0, 0}, 4, 2))
            .equals("served=7, total wait=20 min, longest wait=7 min"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
