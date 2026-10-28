import java.util.*;

public class W05CatalogueReference {
  public record Interval(long value, int left, int right) {}

  static void values(long[] a, boolean nonempty) {
    if (a == null || a.length > 1000 || (nonempty && a.length == 0))
      throw new IllegalArgumentException("array bounds");
    for (long x : a)
      if (x < -1000000000L || x > 1000000000L) throw new IllegalArgumentException("value bounds");
  }

  static long[] rangeSums(long[] a, int[][] q) {
    values(a, false);
    if (q == null || q.length > 1000) throw new IllegalArgumentException("query count");
    PulseLogWeek5 index = new PulseLogWeek5(a);
    long[] out = new long[q.length];
    for (int i = 0; i < q.length; i++) {
      if (q[i] == null
          || q[i].length != 2
          || q[i][0] < 0
          || q[i][1] < q[i][0]
          || q[i][1] >= a.length) throw new IllegalArgumentException("inclusive range");
      out[i] = index.query(q[i][0], q[i][1] + 1);
    }
    return out;
  }

  static long[] apply(int n, int[][] u) {
    if (n < 0 || n > 1000 || u == null || u.length > 1000)
      throw new IllegalArgumentException("bounds");
    long[] d = new long[n + 1], out = new long[n];
    for (int[] q : u) {
      if (q == null || q.length != 3 || q[0] < 0 || q[1] < q[0] || q[1] >= n)
        throw new IllegalArgumentException("inclusive update");
      d[q[0]] = Math.addExact(d[q[0]], q[2]);
      d[q[1] + 1] = Math.subtractExact(d[q[1] + 1], q[2]);
    }
    long v = 0;
    for (int i = 0; i < n; i++) out[i] = v = Math.addExact(v, d[i]);
    return out;
  }

  static Interval bestInterval(long[] a) {
    values(a, true);
    long current = a[0], best = a[0];
    int start = 0, left = 0, right = 0;
    for (int i = 1; i < a.length; i++) {
      long extend = Math.addExact(current, a[i]);
      if (a[i] > extend) {
        current = a[i];
        start = i;
      } else current = extend;
      if (current > best) {
        best = current;
        left = start;
        right = i;
      }
    }
    return new Interval(best, left, right);
  }

  static long bestSum(long[] a) {
    return bestInterval(a).value();
  }

  static Interval bestTrade(long[] a) {
    values(a, false);
    if (a.length < 2) return new Interval(0, -1, -1);
    long low = a[0], best = 0;
    int lowIndex = 0, buy = -1, sell = -1;
    for (int i = 1; i < a.length; i++) {
      long gain = Math.subtractExact(a[i], low);
      if (gain > best) {
        best = gain;
        buy = lowIndex;
        sell = i;
      }
      if (a[i] < low) {
        low = a[i];
        lowIndex = i;
      }
    }
    return new Interval(best, buy, sell);
  }

  static long profit(long[] a) {
    return bestTrade(a).value();
  }

  static class GridIndex {
    final long[][] p;
    final int rows, cols;

    GridIndex(long[][] a) {
      if (a == null
          || a.length < 1
          || a.length > 30
          || a[0] == null
          || a[0].length < 1
          || a[0].length > 30) throw new IllegalArgumentException("grid bounds");
      rows = a.length;
      cols = a[0].length;
      p = new long[rows + 1][cols + 1];
      for (int r = 0; r < rows; r++) {
        if (a[r] == null || a[r].length != cols) throw new IllegalArgumentException("ragged grid");
        long row = 0;
        for (int c = 0; c < cols; c++) {
          if (a[r][c] < -1000000000L || a[r][c] > 1000000000L)
            throw new IllegalArgumentException("cell bounds");
          row = Math.addExact(row, a[r][c]);
          p[r + 1][c + 1] = Math.addExact(p[r][c + 1], row);
        }
      }
    }

    long query(int r1, int c1, int r2, int c2) {
      if (r1 < 0 || c1 < 0 || r2 < r1 || c2 < c1 || r2 >= rows || c2 >= cols)
        throw new IllegalArgumentException("inclusive rectangle");
      return Math.subtractExact(
          Math.subtractExact(p[r2 + 1][c2 + 1], p[r1][c2 + 1]),
          Math.subtractExact(p[r2 + 1][c1], p[r1][c1]));
    }
  }

  static long rectangle(long[][] a, int r1, int c1, int r2, int c2) {
    return new GridIndex(a).query(r1, c1, r2, c2);
  }

  static long count(long[] a, int k) {
    values(a, false);
    if (k < 1 || k > 10000) throw new IllegalArgumentException("positive k<=10000");
    long[] f = new long[k];
    f[0] = 1;
    int rem = 0;
    long out = 0;
    for (long x : a) {
      rem = (int) Math.floorMod((long) rem + Math.floorMod(x, k), k);
      out = Math.addExact(out, f[rem]++);
    }
    return out;
  }

  static long count(long[] a, long target) {
    values(a, false);
    if (target < -1000000000000L || target > 1000000000000L)
      throw new IllegalArgumentException("target bounds");
    Map<Long, Long> f = new HashMap<>();
    f.put(0L, 1L);
    long prefix = 0, out = 0;
    for (long x : a) {
      prefix = Math.addExact(prefix, x);
      out = Math.addExact(out, f.getOrDefault(Math.subtractExact(prefix, target), 0L));
      f.merge(prefix, 1L, Math::addExact);
    }
    return out;
  }
}
