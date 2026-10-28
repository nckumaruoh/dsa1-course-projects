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
                CoreBasics.repeats(new String[] {"1450", "2210", "1450", "3999", "2210", "1450"}))
            .equals("repeats=3, distinct=3, first repeat=1450"));
    ck(
        ProjectApp.display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        120,
                        new CoreBasics.Node(45, new CoreBasics.Node(30)),
                        new CoreBasics.Node(60))))
            .equals("255"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.apply(
                    8,
                    new int[][] {
                      new int[] {0, 3, 1},
                      new int[] {2, 6, 1},
                      new int[] {5, 7, 1},
                      new int[] {1, 1, 1}
                    }))
            .equals("[1, 2, 2, 2, 1, 2, 2, 1]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(
        ProjectApp.display(
                Week7ProjectReference.count(
                    "data structures; data science; big data analytics", "data"))
            .equals("3"));
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(
        ProjectApp.display(
                W08CatalogueReference.lower(
                    new int[] {1002, 1450, 2210, 3999, 4417, 5120, 7004}, 4417))
            .equals("4"));
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(
        ProjectApp.display(
                Week12ProjectReference.queueSim(new int[] {2, 1, 0, 2, 1, 1, 0, 0}, 2, 1))
            .equals("served=7, total wait=26 min, longest wait=7 min"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
