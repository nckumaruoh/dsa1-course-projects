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
        ProjectApp.display(CoreBasics.frequent(new int[] {4, 2, 4, 7, 9, 2, 4, 7}))
            .equals("most frequent=4 (count 3), distinct=4"));
    ck(ProjectApp.display(CoreBasics.winner(7, 3)).equals("4"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rangeSums(
                    new long[] {210L, 185L, 240L, 200L, 260L, 195L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 5}}))
            .equals("[625, 1290]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        ProjectApp.display(
                Week7ProjectReference.longest(new int[] {3, 3, 1, 2, 1, 1, 2, 3, 3, 4}, 2))
            .equals("5"));
    ck(Week7ProjectReference.longest(new int[] {1, 2, 1, 3}, 2) == 3);
    ck(Week7ProjectReference.longest(new int[] {1, 2}, 0) == 0);
    ck(
        ProjectApp.display(
                W08CatalogueReference.lower(new int[] {210, 395, 635, 835, 1095, 1290}, 700))
            .equals("3"));
    ck(W08CatalogueReference.lower(new int[] {210, 395}, 210) == 0);
    ck(W08CatalogueReference.lower(new int[] {210, 395}, 211) == 1);
    ck(W08CatalogueReference.lower(new int[] {210, 395}, 396) == 2);
    ck(
        ProjectApp.display(Week11ProjectReference.simulate(new int[] {4, 2, 4, 7, 9, 2, 4, 7}, 3))
            .equals("hits=1 of 8, final order (most recent first) [7, 4, 2]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
