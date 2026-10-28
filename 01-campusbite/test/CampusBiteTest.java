import java.util.*;

/** Dependency-free regression suite with independent small-input oracles. */
public class CampusBiteTest {
  private static int checks;

  private static void check(boolean condition) {
    checks++;
    if (!condition) throw new AssertionError("check " + checks);
  }

  private static void rejects(Runnable operation) {
    checks++;
    try {
      operation.run();
    } catch (IllegalArgumentException expected) {
      return;
    }
    throw new AssertionError("expected rejection " + checks);
  }

  private static int subsetOracle(int[] a, int budget) {
    int answer = 0;
    for (int mask = 0; mask < (1 << a.length); mask++) {
      int sum = 0;
      for (int i = 0; i < a.length; i++) if ((mask & (1 << i)) != 0) sum += a[i];
      if (sum <= budget) answer = Math.max(answer, sum);
    }
    return answer;
  }

  private static long[] windowOracle(int[] a, int k) {
    long best = Long.MIN_VALUE;
    int start = 0;
    for (int i = 0; i + k <= a.length; i++) {
      long sum = 0;
      for (int j = i; j < i + k; j++) sum += a[j];
      if (sum > best) {
        best = sum;
        start = i;
      }
    }
    return new long[] {best, start};
  }

  /** Minute-by-minute oracle deliberately differs from event-driven implementation. */
  private static long[] queueOracle(int[] buckets, int service, int counters) {
    ArrayDeque<Integer> waiting = new ArrayDeque<>();
    long[] free = new long[counters];
    int total = Arrays.stream(buckets).sum(), served = 0;
    long sum = 0, max = 0;
    for (int time = 0; served < total; time++) {
      if (time < buckets.length) for (int j = 0; j < buckets[time]; j++) waiting.add(time);
      for (int c = 0; c < counters && !waiting.isEmpty(); c++)
        if (free[c] <= time) {
          long wait = time - waiting.remove();
          sum += wait;
          max = Math.max(max, wait);
          free[c] = time + service;
          served++;
        }
    }
    return new long[] {served, sum, max};
  }

  public static void main(String[] args) {
    String[] ids = {"501", "502", "503", "502", "504", "501", "505", "502"};
    check(CampusBite.repeats(ids).equals("repeats=3, distinct=5, first repeat=502"));
    check(
        CampusBite.repeats(new String[] {"a", "a", "a", "b", "b"})
            .equals("repeats=3, distinct=2, first repeat=a"));
    check(CampusBite.repeats(new String[0]).equals("repeats=0, distinct=0, first repeat=none"));
    check(CampusBite.bestTotal(new int[] {60, 45, 30, 80, 25}, 150) == 150);
    check(CampusBite.bestTotal(new int[] {2, 3, 5}, 4) == 3);
    check(CampusBite.bestTotal(new int[0], 0) == 0);
    check(
        Arrays.equals(
            CampusBite.rangeSums(
                new long[] {450, 1200, 2300, 3100, 2800, 900, 600}, new int[][] {{2, 4}, {0, 6}}),
            new long[] {8200, 11350}));
    check(
        Arrays.equals(
            CampusBite.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    check(CampusBite.rangeSums(new long[0], new int[0][]).length == 0);
    check(
        Arrays.equals(
            CampusBite.bestWindow(new int[] {450, 1200, 2300, 3100, 2800, 900, 600}, 3),
            new long[] {8200, 2}));
    check(Arrays.equals(CampusBite.bestWindow(new int[] {-5, -2, -3}, 2), new long[] {-5, 1}));
    check(Arrays.equals(CampusBite.bestWindow(new int[] {1, 1, 1}, 2), new long[] {2, 0}));
    check(CampusBite.count(new int[] {30, 95, 180, 241, 250, 266, 299, 300, 345}, 240, 300) == 4);
    check(CampusBite.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    check(CampusBite.count(new int[] {10, 20}, 20, 20) == 0);
    check(CampusBite.count(new int[0], 10, 30) == 0);
    int[] arrivals = {1, 2, 1, 0, 2, 1, 0, 0};
    check(
        CampusBite.queueSim(arrivals, 3, 1)
            .equals("served=7, total wait=46 min, longest wait=13 min"));
    check(
        CampusBite.queueSim(arrivals, 3, 2)
            .equals("served=7, total wait=13 min, longest wait=4 min"));
    check(CampusBite.simulate(new int[0], 1, 1).served() == 0);
    check(CampusBite.simulate(new int[] {2}, 1, 2).trace().get(1).counter() == 1);
    long[] original = {1, 2, 3};
    var frozen = new CampusBite.RevenueIndex(original);
    original[0] = 99;
    check(frozen.query(0, 2) == 6);
    int[] times = {1, 2, 2};
    var timeIndex = new CampusBite.TimeIndex(times);
    times[0] = 99;
    check(timeIndex.count(1, 3) == 3);
    rejects(() -> CampusBite.repeats(new String[] {null}));
    rejects(() -> CampusBite.repeatsCountArray(new int[] {10}, 10));
    rejects(() -> CampusBite.bestTotal(new int[] {-1}, 10));
    rejects(() -> CampusBite.bestTotal(new int[31], 10));
    rejects(() -> CampusBite.rangeSums(new long[0], new int[][] {{0, 0}}));
    rejects(() -> CampusBite.rangeSums(new long[] {1}, new int[][] {{0}}));
    rejects(() -> CampusBite.bestWindow(new int[0], 1));
    rejects(() -> CampusBite.count(new int[] {2, 1}, 0, 3));
    rejects(() -> CampusBite.count(new int[] {1}, 3, 2));
    rejects(() -> CampusBite.simulate(new int[] {-1}, 1, 1));
    rejects(() -> CampusBite.simulate(new int[] {100001}, 1, 1));
    rejects(() -> CampusBite.simulate(new int[0], 0, 1));
    rejects(() -> CampusBite.simulate(new int[0], 1, 3));
    Random random = new Random(20261028L);
    for (int trial = 0; trial < 400; trial++) {
      int n = random.nextInt(12);
      int[] a = new int[n];
      String[] s = new String[n];
      for (int i = 0; i < n; i++) {
        a[i] = random.nextInt(20);
        s[i] = Integer.toString(a[i]);
      }
      int[] before = a.clone();
      check(CampusBite.repeatReport(s).equals(CampusBite.repeatsPairwise(s)));
      check(CampusBite.repeatReport(s).equals(CampusBite.repeatsCountArray(a, 20)));
      int capacity = random.nextInt(80);
      check(CampusBite.bestTotal(a, capacity) == subsetOracle(a, capacity));
      check(Arrays.equals(a, before));
      long[] values = new long[n];
      for (int i = 0; i < n; i++) values[i] = random.nextInt(200) - 100;
      var prefix = new CampusBite.RevenueIndex(values);
      for (int left = 0; left < n; left++)
        for (int right = left; right < n; right++) {
          long expected = 0;
          for (int j = left; j <= right; j++) expected += values[j];
          check(prefix.query(left, right) == expected);
        }
      if (n > 0) {
        for (int i = 0; i < n; i++) a[i] -= 10;
        int k = 1 + random.nextInt(n);
        check(Arrays.equals(CampusBite.bestWindow(a, k), windowOracle(a, k)));
      }
      Arrays.sort(a);
      int lo = random.nextInt(30) - 15, hi = lo + random.nextInt(20), expected = 0;
      for (int x : a) if (x >= lo && x < hi) expected++;
      check(CampusBite.count(a, lo, hi) == expected);
      int[] buckets = new int[random.nextInt(10)];
      for (int i = 0; i < buckets.length; i++) buckets[i] = random.nextInt(4);
      int service = 1 + random.nextInt(5);
      for (int c = 1; c <= 2; c++) {
        var actual = CampusBite.simulate(buckets, service, c);
        long[] oracle = queueOracle(buckets, service, c);
        check(
            actual.served() == oracle[0]
                && actual.totalWait() == oracle[1]
                && actual.longestWait() == oracle[2]);
        for (var event : actual.trace())
          check(event.start() >= event.arrival() && event.finish() - event.start() == service);
      }
    }
    int[] maximum = new int[100000];
    Arrays.fill(maximum, 1_000_000_000);
    check(CampusBite.bestWindow(maximum, 100000)[0] == 100_000_000_000_000L);
    var drained = CampusBite.simulate(new int[] {100000}, 100000, 1);
    check(drained.totalWait() == 499_995_000_000_000L && drained.longestWait() == 9_999_900_000L);
    System.out.println(
        "PASS: " + checks + " checks; six increments, fixed seed 20261028, independent oracles.");
  }
}
