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
        ProjectApp.display(CoreBasics.frequent(new int[] {7, 3, 7, 1, 3, 7, 2}))
            .equals("most frequent=7 (count 3), distinct=4"));
    ck(ProjectApp.display(W04CatalogueReference.distance("algoritm", "algorithm")).equals("1"));
    ck(
        ProjectApp.display(
                W05CatalogueReference.rangeSums(
                    new long[] {120L, 85L, 240L, 60L, 175L},
                    new int[][] {new int[] {1, 3}, new int[] {0, 4}}))
            .equals("[385, 680]"));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        ProjectApp.display(Week7ProjectReference.count("abracadabra abracadabra", "abra"))
            .equals("4"));
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(
        ProjectApp.display(W08CatalogueReference.lower(new int[] {40, 95, 130, 180, 260}, 140))
            .equals("3"));
    ck(W08CatalogueReference.lower(new int[] {40, 95}, 40) == 0);
    ck(W08CatalogueReference.lower(new int[] {40, 95}, 41) == 1);
    ck(W08CatalogueReference.lower(new int[] {40, 95}, 96) == 2);
    ck(
        ProjectApp.display(Week12ProjectReference.validBrackets("if (a[i] > b) { x = (y]; }"))
            .equals("false"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
