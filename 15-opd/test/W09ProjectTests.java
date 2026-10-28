import java.util.*;

public class W09ProjectTests {
  static long checks;

  static void ck(boolean b) {
    checks++;
    if (!b) throw new AssertionError("project check " + checks);
  }

  static void rejects(Runnable r) {
    checks++;
    try {
      r.run();
      throw new AssertionError("expected rejection");
    } catch (IllegalArgumentException expected) {
    }
  }

  static long capacity(int[] a, int rounds) {
    long best = Long.MAX_VALUE;
    for (int mask = 0; mask < (1 << (a.length - 1)); mask++) {
      if (1 + Integer.bitCount(mask) > rounds) continue;
      long sum = 0, max = 0;
      for (int i = 0; i < a.length; i++) {
        sum += a[i];
        if (i == a.length - 1 || (mask & (1 << i)) != 0) {
          max = Math.max(max, sum);
          sum = 0;
        }
      }
      best = Math.min(best, max);
    }
    return best;
  }

  static long gap(int[] a, int count) {
    int[] b = a.clone();
    Arrays.sort(b);
    long best = 0;
    for (int mask = 0; mask < (1 << b.length); mask++)
      if (Integer.bitCount(mask) == count) {
        long min = Long.MAX_VALUE;
        int last = -1;
        for (int i = 0; i < b.length; i++)
          if ((mask & (1 << i)) != 0) {
            if (last >= 0) min = Math.min(min, (long) b[i] - last);
            last = b[i];
          }
        best = Math.max(best, min);
      }
    return best;
  }

  static long hours(int[] a, long rate) {
    long sum = 0;
    for (int x : a) sum += x / rate + (x % rate == 0 ? 0 : 1);
    return sum;
  }

  public static void main(String[] args) {
    ck(Week9ProjectReference.min_capacity(new int[] {300, 450, 200, 380, 520, 260}, 4) == 750L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6) == 23L);
    ck(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6) == 23);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 1) == -1);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 2) == 7);
    ck(
        Week9ProjectReference.max_goal(new int[] {8200, 10450, 6300, 12100, 9800, 15020, 4300}, 5)
            == 8200L);
    ck(Week9ProjectReference.max_goal(new int[] {8, 10, 6}, 2) == 8);
    ck(Week9ProjectReference.max_goal(new int[] {8, 8, 6}, 2) == 8);
    ck(Week9ProjectReference.max_goal(new int[] {8, 8, 6}, 4) == -1);
    ck(Week9ProjectReference.min_rate(new int[] {120, 45, 300, 80}, 6) == 120L);
    ck(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6) == 23);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 1) == -1);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 2) == 7);
    ck(Week9ProjectReference.min_capacity(new int[] {42, 38, 51, 29, 47, 33, 60, 25}, 3) == 127L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(Week9ProjectReference.min_rate(new int[] {120, 45, 300, 80}, 8) == 80L);
    ck(Week9ProjectReference.min_rate(new int[] {30, 11, 23, 4, 20}, 6) == 23);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 1) == -1);
    ck(Week9ProjectReference.min_rate(new int[] {5, 7}, 2) == 7);
    ck(Week9ProjectReference.max_min_gap(new int[] {0, 4, 9, 15, 22, 30}, 3) == 15L);
    ck(Week9ProjectReference.max_min_gap(new int[] {0, 4, 9, 15, 22, 30}, 3) == 15);
    ck(Week9ProjectReference.max_min_gap(new int[] {0, 30}, 2) == 30);
    rejects(() -> Week9ProjectReference.max_min_gap(new int[] {0, 30}, 1));
    ck(Week9ProjectReference.min_capacity(new int[] {30, 45, 20, 38, 52, 26, 18, 12, 9}, 5) == 65L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(Week9ProjectReference.min_capacity(new int[] {12, 30, 18, 25, 40, 15}, 3) == 55L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(Week9ProjectReference.min_capacity(new int[] {450, 200, 380, 120, 260, 300}, 4) == 560L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8, 3, 6}, 3) == 17L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    ck(Week9ProjectReference.min_capacity(new int[] {12, 35, 60, 8, 22, 41, 18}, 4) == 60L);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 1) == 32);
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 5) == 10);
    ck(
        Week9ProjectReference.min_capacity(new int[] {1000000000, 1000000000, 1000000000}, 1)
            == 3000000000L);
    Random random = new Random(269901);
    for (int t = 0; t < 1600; t++) {
      int n = 2 + random.nextInt(8);
      int[] a = new int[n];
      for (int i = 0; i < n; i++) a[i] = 1 + random.nextInt(20);
      int[] before = a.clone();
      int rounds = 1 + random.nextInt(n + 2);
      long result = Week9ProjectReference.min_capacity(a, rounds);
      ck(result == capacity(a, rounds));
      long calls = Week9ProjectReference.predicateCalls,
          visits = Week9ProjectReference.itemsVisited;
      ck(calls <= 64);
      ck(visits <= calls * n);
      ck(Week9ProjectReference.fits(a, rounds, result));
      ck(!Week9ProjectReference.fits(a, rounds, result - 1));
      boolean seenTrue = false;
      for (long c = 0; c <= Arrays.stream(a).sum(); c++) {
        boolean feasible = Week9ProjectReference.fits(a, rounds, c);
        if (seenTrue) ck(feasible);
        seenTrue |= feasible;
      }
      int budget = 1 + random.nextInt(3 * n);
      long rate = Week9ProjectReference.min_rate(a, budget);
      if (budget < n) ck(rate == -1);
      else {
        long oracle = 1;
        while (hours(a, oracle) > budget) oracle++;
        ck(rate == oracle);
        ck(hours(a, rate) <= budget);
        if (rate > 1) ck(hours(a, rate - 1) > budget);
      }
      int days = 1 + random.nextInt(n + 2);
      long goal = Week9ProjectReference.max_goal(a, days);
      if (days > n) ck(goal == -1);
      else {
        int[] sorted = a.clone();
        Arrays.sort(sorted);
        ck(goal == sorted[n - days]);
        int met = 0, next = 0;
        for (int x : a) {
          if (x >= goal) met++;
          if (x >= goal + 1) next++;
        }
        ck(met >= days && next < days);
      }
      int[] positions = new int[n];
      for (int i = 0; i < n; i++) positions[i] = 4 * i + random.nextInt(3);
      int count = 2 + random.nextInt(n - 1);
      int[] saved = positions.clone();
      ck(Week9ProjectReference.max_min_gap(positions, count) == gap(positions, count));
      ck(Arrays.equals(positions, saved));
      ck(Arrays.equals(a, before));
    }
    ck(Week9ProjectReference.min_capacity(new int[] {7, 2, 5, 10, 8}, 2) == 18);
    ck(!Week9ProjectReference.fits(new int[] {7, 2, 5, 10, 8}, 2, 17));
    ck(!Week9ProjectReference.fits(new int[] {10}, 2, 9));
    ck(Week9ProjectReference.min_capacity(new int[] {5}, 10) == 5);
    int[] max = new int[100000];
    Arrays.fill(max, 1000000000);
    ck(Week9ProjectReference.min_capacity(max, 1) == 100000000000000L);
    ck(Week9ProjectReference.min_rate(max, 100000) == 1000000000);
    ck(Week9ProjectReference.max_goal(max, 100000) == 1000000000);
    rejects(() -> Week9ProjectReference.min_capacity(null, 1));
    rejects(() -> Week9ProjectReference.min_capacity(new int[] {}, 1));
    rejects(() -> Week9ProjectReference.min_capacity(new int[100001], 1));
    rejects(() -> Week9ProjectReference.min_capacity(new int[] {1000000001}, 1));
    rejects(() -> Week9ProjectReference.min_capacity(new int[] {0}, 1));
    rejects(() -> Week9ProjectReference.min_capacity(new int[] {5}, 0));
    rejects(() -> Week9ProjectReference.max_min_gap(new int[] {0, 1000000001}, 2));
    rejects(() -> Week9ProjectReference.max_min_gap(new int[] {1, 1}, 2));
    rejects(() -> Week9ProjectReference.max_min_gap(null, 2));
    rejects(() -> Week9ProjectReference.max_min_gap(new int[] {0, 1}, 3));
    System.out.println(
        "PASS: "
            + checks
            + " project assertions;12 source samples +43 listed additional cases;4"
            + " families,partition/placement/rate/order-statistic oracles,predicate"
            + " neighbours,counters and input bounds.");
  }
}
