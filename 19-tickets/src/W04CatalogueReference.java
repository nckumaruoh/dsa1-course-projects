import java.util.*;

public class W04CatalogueReference {
  static void bounds(int[] a, int n, int target) {
    if (a == null || a.length > n || target < 0 || target > 5000)
      throw new IllegalArgumentException("lab bounds");
  }

  static int bestTotal(int[] a, int capacity) {
    return new CapacityPlanner(a, capacity).bestTotal();
  }

  static int minCoins(int[] coins, int amount) {
    bounds(coins, 1000, amount);
    for (int c : coins) if (c <= 0) throw new IllegalArgumentException("positive denominations");
    int[] d = new int[amount + 1];
    Arrays.fill(d, amount + 1);
    d[0] = 0;
    for (int t = 1; t <= amount; t++)
      for (int c : coins) if (c <= t) d[t] = Math.min(d[t], 1 + d[t - c]);
    return d[amount] > amount ? -1 : d[amount];
  }

  static long ways(int[] choices, int target) {
    bounds(choices, 60, target);
    if (target > 60) throw new IllegalArgumentException("ordered-count lab target<=60");
    Set<Integer> seen = new HashSet<>();
    for (int c : choices)
      if (c <= 0 || !seen.add(c)) throw new IllegalArgumentException("distinct positive choices");
    long[] d = new long[target + 1];
    d[0] = 1;
    for (int t = 1; t <= target; t++)
      for (int c : choices) if (c <= t) d[t] = Math.addExact(d[t], d[t - c]);
    return d[target];
  }

  static long countSubsets(int[] a, int target) {
    bounds(a, 60, target);
    for (int x : a) if (x < 0 || x > 5000) throw new IllegalArgumentException("value bounds");
    long[] d = new long[target + 1];
    d[0] = 1;
    for (int x : a) for (int t = target; t >= x; t--) d[t] = Math.addExact(d[t], d[t - x]);
    return d[target];
  }

  static long paths(String[] grid) {
    if (grid == null
        || grid.length < 1
        || grid.length > 30
        || grid[0] == null
        || grid[0].isEmpty()
        || grid[0].length() > 30) throw new IllegalArgumentException("grid bounds");
    int cols = grid[0].length();
    long[] d = new long[cols];
    d[0] = 1;
    for (String row : grid) {
      if (row == null || row.length() != cols) throw new IllegalArgumentException("rectangle");
      for (int c = 0; c < cols; c++) {
        char ch = row.charAt(c);
        if (ch == '#') d[c] = 0;
        else if (ch == '.') {
          if (c > 0) d[c] = Math.addExact(d[c], d[c - 1]);
        } else throw new IllegalArgumentException("cell type");
      }
    }
    return d[cols - 1];
  }

  static int distance(String a, String b) {
    if (a == null || b == null || a.length() > 1000 || b.length() > 1000)
      throw new IllegalArgumentException("text bounds");
    if (a.length() < b.length()) {
      String x = a;
      a = b;
      b = x;
    }
    int[] previous = new int[b.length() + 1];
    for (int j = 0; j < previous.length; j++) previous[j] = j;
    for (int i = 1; i <= a.length(); i++) {
      int[] current = new int[b.length() + 1];
      current[0] = i;
      for (int j = 1; j <= b.length(); j++)
        current[j] =
            a.charAt(i - 1) == b.charAt(j - 1)
                ? previous[j - 1]
                : 1 + Math.min(previous[j - 1], Math.min(previous[j], current[j - 1]));
      previous = current;
    }
    return previous[b.length()];
  }

  static void gen(int[] a, int i, int remain, List<Integer> path, List<List<Integer>> out) {
    if (remain == 0) {
      out.add(new ArrayList<>(path));
      return;
    }
    for (int j = i; j < a.length && a[j] <= remain; j++) {
      if (j > i && a[j] == a[j - 1]) continue;
      path.add(a[j]);
      gen(a, j + 1, remain - a[j], path, out);
      path.remove(path.size() - 1);
    }
  }

  static List<List<Integer>> combinations(int[] a, int target) {
    bounds(a, 20, target);
    for (int v : a) if (v <= 0 || v > 5000) throw new IllegalArgumentException("positive values");
    a = a.clone();
    Arrays.sort(a);
    List<List<Integer>> out = new ArrayList<>();
    gen(a, 0, target, new ArrayList<>(), out);
    return out;
  }
}
