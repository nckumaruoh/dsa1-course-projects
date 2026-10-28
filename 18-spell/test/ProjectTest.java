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
        ProjectApp.display(CoreBasics.frequent(new int[] {4, 0, 4, 19, 4, 0, 18}))
            .equals("most frequent=4 (count 3), distinct=4"));
    ck(ProjectApp.display(W04CatalogueReference.distance("recieve", "receive")).equals("2"));
    ck(
        ProjectApp.display(
                W06CatalogueReference.merge(new int[] {3, 8, 15, 21}, new int[] {5, 8, 30}))
            .equals("[3, 5, 8, 8, 15, 21, 30]"));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(ProjectApp.display(Week7ProjectReference.lcs("klconnect", "kluniversity")).equals("5"));
    ck(Week7ProjectReference.lcs("abcde", "ace") == 3);
    ck(Week7ProjectReference.lcs("abc", "def") == 0);
    ck(
        ProjectApp.display(
                W08CatalogueReference.count(
                    new String[] {"bread", "chai", "chakli", "chana", "cheese", "curd"},
                    "cha",
                    "chb"))
            .equals("3"));
    ck(
        java.util.Arrays.equals(
            new W08WordIndex(new String[] {"a", "z", "zz"}).prefix("z"), new int[] {1, 3}));
    ck(
        java.util.Arrays.equals(
            new W08WordIndex(new String[] {"a", "z", "zz"}).prefix(""), new int[] {0, 3}));
    ck(
        new W08WordIndex(new String[] {"a", "z", "zz"}).prefix("b")[1]
                - new W08WordIndex(new String[] {"a", "z", "zz"}).prefix("b")[0]
            == 0);
    topCheck(
        new int[] {5, 2, 5, 8, 2, 5, 9, 2, 2},
        2,
        Week10ProjectReference.topKLinear(new int[] {5, 2, 5, 8, 2, 5, 9, 2, 2}, 2));
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
