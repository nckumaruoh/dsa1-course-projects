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
        ProjectApp.display(CoreBasics.repeats(new String[] {"a9", "c4", "a9", "f1", "c4", "a9"}))
            .equals("repeats=3, distinct=3, first repeat=a9"));
    ck(
        ProjectApp.display(
                CoreBasics.total(
                    new CoreBasics.Node(
                        5,
                        new CoreBasics.Node(
                            1200, new CoreBasics.Node(800), new CoreBasics.Node(950)),
                        new CoreBasics.Node(300))))
            .equals("3255"));
    ck(
        ProjectApp.display(
                W06CatalogueReference.merge(new int[] {2, 5, 9, 14}, new int[] {3, 5, 11}))
            .equals("[2, 3, 5, 5, 9, 11, 14]"));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        ProjectApp.display(
                Week7ProjectReference.count("/img/2025/img_01.jpg /img/2026/img_02.jpg", "img"))
            .equals("4"));
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(
        ProjectApp.display(
                Week9ProjectReference.min_capacity(new int[] {450, 200, 380, 120, 260, 300}, 4))
            .equals("560"));
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(
        ProjectApp.display(Week11ProjectReference.simulate(new int[] {1, 2, 1, 3, 2, 1, 3}, 2))
            .equals("hits=1 of 7, final order (most recent first) [3, 1]"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
