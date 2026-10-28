import java.util.*;

public class W04ProjectTests {
  static long checks;

  static void ck(boolean b) {
    checks++;
    if (!b) throw new AssertionError("project check " + checks);
  }

  static void equal(Object actual, String expected) {
    ck(String.valueOf(actual).equals(expected));
  }

  static Set<Integer> reachable(int[] a) {
    Set<Integer> s = new HashSet<>();
    s.add(0);
    for (int v : a) {
      Set<Integer> next = new HashSet<>(s);
      for (int x : s) next.add(x + v);
      s = next;
    }
    return s;
  }

  static long subsetCount(int[] a, int target) {
    long count = 0;
    for (int mask = 0; mask < (1 << a.length); mask++) {
      int sum = 0;
      for (int i = 0; i < a.length; i++) if ((mask & (1 << i)) != 0) sum += a[i];
      if (sum == target) count++;
    }
    return count;
  }

  static int edit(String a, String b) {
    int[][] d = new int[a.length() + 1][b.length() + 1];
    for (int i = 0; i <= a.length(); i++) d[i][0] = i;
    for (int j = 0; j <= b.length(); j++) d[0][j] = j;
    for (int i = 1; i <= a.length(); i++)
      for (int j = 1; j <= b.length(); j++)
        d[i][j] =
            Math.min(
                d[i - 1][j] + 1,
                Math.min(
                    d[i][j - 1] + 1,
                    d[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)));
    return d[a.length()][b.length()];
  }

  static void rejected(int[] a, int capacity) {
    boolean fail = false;
    try {
      new CapacityPlanner(a, capacity);
    } catch (IllegalArgumentException ex) {
      fail = true;
    }
    ck(fail);
  }

  public static void main(String[] args) {
    equal(W04CatalogueReference.bestTotal(new int[] {60, 45, 30, 80, 25}, 150), "150");
    equal(W04CatalogueReference.bestTotal(new int[] {2, 3, 5}, 4), "3");
    equal(W04CatalogueReference.bestTotal(new int[] {}, 0), "0");
    equal(W04CatalogueReference.minCoins(new int[] {10, 25, 60}, 135), "4");
    equal(W04CatalogueReference.minCoins(new int[] {2}, 3), "-1");
    equal(W04CatalogueReference.minCoins(new int[] {1}, 0), "0");
    equal(W04CatalogueReference.ways(new int[] {1, 2, 4, 6}, 8), "60");
    equal(W04CatalogueReference.ways(new int[] {1, 2}, 3), "3");
    equal(W04CatalogueReference.ways(new int[] {1, 2}, 0), "1");
    equal(W04CatalogueReference.bestTotal(new int[] {3, 4, 5, 7, 2}, 10), "10");
    equal(W04CatalogueReference.bestTotal(new int[] {2, 3, 5}, 4), "3");
    equal(W04CatalogueReference.bestTotal(new int[] {}, 0), "0");
    equal(W04CatalogueReference.countSubsets(new int[] {20, 30, 10, 40, 30}, 60), "4");
    equal(W04CatalogueReference.countSubsets(new int[] {2, 2}, 2), "2");
    equal(W04CatalogueReference.countSubsets(new int[] {}, 0), "1");
    equal(W04CatalogueReference.paths(new String[] {"...", ".#.", "..."}), "2");
    equal(W04CatalogueReference.paths(new String[] {"...", ".#.", "..."}), "2");
    equal(W04CatalogueReference.paths(new String[] {"#"}), "0");
    equal(W04CatalogueReference.minCoins(new int[] {50, 100, 200}, 450), "3");
    equal(W04CatalogueReference.minCoins(new int[] {2}, 3), "-1");
    equal(W04CatalogueReference.minCoins(new int[] {1}, 0), "0");
    equal(W04CatalogueReference.minCoins(new int[] {1, 2, 5}, 13), "4");
    equal(W04CatalogueReference.minCoins(new int[] {2}, 3), "-1");
    equal(W04CatalogueReference.minCoins(new int[] {1}, 0), "0");
    equal(W04CatalogueReference.ways(new int[] {2, 3, 4}, 10), "17");
    equal(W04CatalogueReference.ways(new int[] {1, 2}, 3), "3");
    equal(W04CatalogueReference.ways(new int[] {1, 2}, 0), "1");
    equal(W04CatalogueReference.paths(new String[] {"....", ".#..", "...#", "#..."}), "3");
    equal(W04CatalogueReference.paths(new String[] {"...", ".#.", "..."}), "2");
    equal(W04CatalogueReference.paths(new String[] {"#"}), "0");
    equal(W04CatalogueReference.bestTotal(new int[] {1850, 2420, 980, 1460, 3100}, 5000), "4950");
    equal(W04CatalogueReference.bestTotal(new int[] {2, 3, 5}, 4), "3");
    equal(W04CatalogueReference.bestTotal(new int[] {}, 0), "0");
    equal(W04CatalogueReference.distance("recieve", "receive"), "2");
    equal(W04CatalogueReference.distance("ab", "ac"), "1");
    equal(W04CatalogueReference.distance("", "abc"), "3");
    equal(
        W04CatalogueReference.combinations(new int[] {2, 3, 5, 5, 7, 8}, 10),
        "[[2, 3, 5], [2, 8], [3, 7], [5, 5]]");
    equal(W04CatalogueReference.combinations(new int[] {2, 2}, 4), "[[2, 2]]");
    equal(W04CatalogueReference.combinations(new int[] {2}, 1), "[]");
    equal(W04CatalogueReference.minCoins(new int[] {50, 120, 200}, 370), "3");
    equal(W04CatalogueReference.minCoins(new int[] {2}, 3), "-1");
    equal(W04CatalogueReference.minCoins(new int[] {1}, 0), "0");
    equal(W04CatalogueReference.countSubsets(new int[] {20, 30, 50, 40, 10, 60}, 100), "5");
    equal(W04CatalogueReference.countSubsets(new int[] {2, 2}, 2), "2");
    equal(W04CatalogueReference.countSubsets(new int[] {}, 0), "1");
    equal(W04CatalogueReference.bestTotal(new int[] {350, 420, 280, 160, 510}, 1000), "950");
    equal(W04CatalogueReference.bestTotal(new int[] {2, 3, 5}, 4), "3");
    equal(W04CatalogueReference.bestTotal(new int[] {}, 0), "0");
    equal(W04CatalogueReference.countSubsets(new int[] {350, 450, 350, 250, 200}, 800), "4");
    equal(W04CatalogueReference.countSubsets(new int[] {2, 2}, 2), "2");
    equal(W04CatalogueReference.countSubsets(new int[] {}, 0), "1");
    equal(W04CatalogueReference.distance("sp4mmer", "spammer"), "1");
    equal(W04CatalogueReference.distance("ab", "ac"), "1");
    equal(W04CatalogueReference.distance("", "abc"), "3");
    equal(W04CatalogueReference.bestTotal(new int[] {6, 4, 9, 3, 7}, 15), "15");
    equal(W04CatalogueReference.bestTotal(new int[] {2, 3, 5}, 4), "3");
    equal(W04CatalogueReference.bestTotal(new int[] {}, 0), "0");
    equal(
        W04CatalogueReference.combinations(new int[] {30, 45, 60, 15, 30, 90}, 120),
        "[[15, 30, 30, 45], [15, 45, 60], [30, 30, 60], [30, 90]]");
    equal(W04CatalogueReference.combinations(new int[] {2, 2}, 4), "[[2, 2]]");
    equal(W04CatalogueReference.combinations(new int[] {2}, 1), "[]");
    equal(W04CatalogueReference.distance("algoritm", "algorithm"), "1");
    equal(W04CatalogueReference.distance("ab", "ac"), "1");
    equal(W04CatalogueReference.distance("", "abc"), "3");
    equal(W04CatalogueReference.countSubsets(new int[] {20, 30, 50, 40, 10, 60}, 100), "5");
    equal(W04CatalogueReference.countSubsets(new int[] {2, 2}, 2), "2");
    equal(W04CatalogueReference.countSubsets(new int[] {}, 0), "1");
    Random random = new Random(261204);
    for (int t = 0; t < 1000; t++) {
      int[] a = new int[random.nextInt(11)];
      for (int i = 0; i < a.length; i++) a[i] = random.nextInt(11);
      int capacity = random.nextInt(31);
      int[] before = a.clone();
      Set<Integer> reachable = reachable(a);
      CapacityPlanner planner = new CapacityPlanner(a, capacity);
      int best = 0;
      for (int target = 0; target <= capacity; target++) {
        boolean expected = reachable.contains(target);
        ck(planner.exact(target) == expected);
        ck(W04CatalogueReference.countSubsets(a, target) == subsetCount(a, target));
        if (expected) {
          best = target;
          List<Integer> witness = planner.selectedIndices(target);
          Set<Integer> unique = new HashSet<>(witness);
          int sum = 0;
          for (int index : witness) {
            ck(index >= 0 && index < a.length);
            sum += a[index];
          }
          ck(unique.size() == witness.size() && sum == target);
        }
      }
      ck(planner.bestTotal() == best);
      long misses = planner.misses;
      planner.bestTotal();
      ck(planner.misses == misses);
      ck(Arrays.equals(a, before));
    }
    CapacityPlanner falseCache = new CapacityPlanner(new int[] {2, 2}, 3);
    ck(!falseCache.exact(1));
    long misses = falseCache.misses;
    ck(!falseCache.exact(1) && falseCache.misses == misses && falseCache.hits > 0);
    int[] source = {2, 2, 3};
    CapacityPlanner frozen = new CapacityPlanner(source, 4);
    source[0] = 99;
    ck(frozen.exact(4));
    ck(frozen.selectedIndices(4).equals(List.of(0, 1)));
    ck(new CapacityPlanner(new int[] {}, 5000).bestTotal() == 0);
    rejected(new int[31], 0);
    rejected(new int[] {5001}, 1);
    rejected(new int[] {}, 5001);
    rejected(new int[] {-1}, 1);
    rejected(null, 1);
    rejected(new int[] {}, -1);
    CapacityPlanner bounded = new CapacityPlanner(new int[] {1}, 1);
    boolean bad = false;
    try {
      bounded.exact(2);
    } catch (IllegalArgumentException ex) {
      bad = true;
    }
    ck(bad);
    for (int t = 0; t < 300; t++) {
      StringBuilder a = new StringBuilder(), b = new StringBuilder();
      for (int i = 0; i < random.nextInt(10); i++) a.append((char) ('a' + random.nextInt(3)));
      for (int i = 0; i < random.nextInt(10); i++) b.append((char) ('a' + random.nextInt(3)));
      ck(
          W04CatalogueReference.distance(a.toString(), b.toString())
              == edit(a.toString(), b.toString()));
    }
    ck(W04CatalogueReference.countSubsets(new int[] {0, 0, 1}, 0) == 4);
    ck(W04CatalogueReference.countSubsets(new int[60], 0) == (1L << 60));
    ck(W04CatalogueReference.ways(new int[] {1, 2}, 60) == 2504730781961L); // Fibonacci61
    String[] grid = new String[30];
    Arrays.fill(grid, ".".repeat(30));
    ck(W04CatalogueReference.paths(grid) == 30067266499541040L);
    System.out.println(
        "PASS project/catalogue: "
            + checks
            + " assertions;22 source milestones+44 additional cases, memo reuse, witness indices,"
            + " bounds and family oracles.");
  }
}
