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
        ProjectApp.display(CoreBasics.frequent(new int[] {12, 4, 12, 7, 4, 12, 9}))
            .equals("most frequent=12 (count 3), distinct=4"));
    ck(ProjectApp.display(CoreBasics.palindrome("Mess-Code: edoc ssem")).equals("true"));
    ck(
        ProjectApp.display(
                W06CatalogueReference.merge(new int[] {1, 3, 4, 8}, new int[] {2, 3, 6, 9}))
            .equals("[1, 2, 3, 3, 4, 6, 8, 9]"));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        ProjectApp.display(
                Week7ProjectReference.count("rice was cold food cold food again", "cold food"))
            .equals("2"));
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(
        ProjectApp.display(
                W08CatalogueReference.count(new int[] {1, 1, 2, 2, 3, 3, 3, 4, 5, 5}, 2, 4))
            .equals("5"));
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(
        ProjectApp.display(Week12ProjectReference.validBrackets("[veg](spicy){menu}[x(y])"))
            .equals("false"));
    CoreBasicsTest.main(new String[0]);
    System.out.println(
        "PASS: " + checks + " project-specific sample/boundary checks; all six increments.");
  }
}
