import java.util.*;

public class ShardHeatmap {
  // Each event is [timestamp, endpoint, hour]. Equal timestamps take the left shard first.
  static int[][] merge(int[][] a, int[][] b, int endpoints, int hours) {
    validate(a, endpoints, hours);
    validate(b, endpoints, hours);
    if ((long) a.length + b.length > 100000)
      throw new IllegalArgumentException("combined n<=100000");
    int[][] o = new int[a.length + b.length][];
    int i = 0, j = 0, k = 0;
    while (i < a.length || j < b.length) {
      int[] x = j == b.length || (i < a.length && a[i][0] <= b[j][0]) ? a[i++] : b[j++];
      o[k++] = x.clone();
    }
    return o;
  }

  static void validate(int[][] a, int E, int H) {
    if (E < 1 || H < 1 || E > 100 || H > 100 || a == null || a.length > 100000)
      throw new IllegalArgumentException("nonnull n<=100000,dimensions1..100 required");
    for (int i = 0; i < a.length; i++) {
      if (a[i] == null
          || a[i].length != 3
          || a[i][0] < 0
          || a[i][1] < 0
          || a[i][1] >= E
          || a[i][2] < 0
          || a[i][2] >= H) throw new IllegalArgumentException("invalid event");
      if (i > 0 && a[i - 1][0] > a[i][0]) throw new IllegalArgumentException("unsorted shard");
    }
  }

  static long[][] heat(int[][] events, int E, int H) {
    validate(events, E, H);
    long[][] out = new long[E][H];
    for (int[] x : events) out[x[1]][x[2]]++;
    return out;
  }
}
