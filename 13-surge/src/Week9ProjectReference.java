import java.util.*;

public class Week9ProjectReference {
  static long predicateCalls, itemsVisited, lastCapacity;
  static int lastGroups;

  // Synthetic SLA: positive indivisible batches, original order, at most rounds contiguous groups.
  static void valid(int[] a, int budget) {
    if (a == null || a.length == 0 || a.length > 100000 || budget < 1)
      throw new IllegalArgumentException("empty/budget");
    for (int x : a)
      if (x < 1 || x > 1000000000) throw new IllegalArgumentException("positive batches required");
  }

  static boolean fits(int[] a, int rounds, long cap) {
    predicateCalls++;
    lastCapacity = cap;
    lastGroups = 1;
    long used = 0;
    int groups = 1;
    for (int x : a) {
      itemsVisited++;
      if (x > cap) return false;
      if (used + x > cap) {
        groups++;
        used = x;
      } else used += x;
      lastGroups = groups;
      if (groups > rounds) return false;
    }
    return true;
  }

  static long min_capacity(int[] a, int rounds) {
    valid(a, rounds);
    predicateCalls = itemsVisited = lastCapacity = 0;
    lastGroups = 0;
    long lo = 0, hi = 0;
    for (int x : a) {
      lo = Math.max(lo, x);
      hi += x;
    }
    while (lo < hi) {
      long mid = lo + (hi - lo) / 2;
      if (fits(a, rounds, mid)) hi = mid;
      else lo = mid + 1;
    }
    return lo;
  }

  // Sequential zones, each charged ceil(work/rate) whole hours; no fractional sharing.
  static long min_rate(int[] a, int hours) {
    valid(a, hours);
    if (hours < a.length) return -1;
    long lo = 1, hi = 0;
    for (int x : a) hi = Math.max(hi, x);
    while (lo < hi) {
      long mid = lo + (hi - lo) / 2, time = 0;
      for (int x : a) time += (x - 1L) / mid + 1;
      if (time <= hours) hi = mid;
      else lo = mid + 1;
    }
    return lo;
  }

  static long max_goal(int[] a, int days) {
    valid(a, days);
    if (days > a.length) return -1;
    long lo = 0, hi = 0;
    for (int x : a) hi = Math.max(hi, x);
    while (lo < hi) {
      long mid = lo + (hi - lo + 1) / 2;
      int met = 0;
      for (int x : a) if (x >= mid) met++;
      if (met >= days) lo = mid;
      else hi = mid - 1;
    }
    return lo;
  }

  static long max_min_gap(int[] a, int count) {
    if (a == null || a.length < 2 || a.length > 100000)
      throw new IllegalArgumentException("require2..100000 positions");
    for (int x : a)
      if (x < 0 || x > 1000000000) throw new IllegalArgumentException("position0..10^9");
    if (count < 2 || count > a.length) throw new IllegalArgumentException("count");
    int[] b = a.clone();
    Arrays.sort(b);
    for (int i = 1; i < b.length; i++)
      if (b[i] == b[i - 1]) throw new IllegalArgumentException("distinct positions");
    long lo = 0, hi = (long) b[b.length - 1] - b[0];
    while (lo < hi) {
      long mid = lo + (hi - lo + 1) / 2, last = b[0];
      int placed = 1;
      for (int x : b)
        if (x - last >= mid) {
          placed++;
          last = x;
        }
      if (placed >= count) lo = mid;
      else hi = mid - 1;
    }
    return lo;
  }
}
