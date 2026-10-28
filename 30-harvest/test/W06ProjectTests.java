import java.util.*;

public class W06ProjectTests {
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

  static int[][] copy(int[][] a) {
    return Arrays.stream(a).map(int[]::clone).toArray(int[][]::new);
  }

  static void pairCheck(int[] a, int target, int[] pair) {
    boolean exists = false;
    for (int i = 0; i < a.length; i++)
      for (int j = i + 1; j < a.length; j++) exists |= (long) a[i] + a[j] == target;
    if (!exists) {
      ck(pair.length == 0);
      return;
    }
    ck(pair.length == 2 && (long) pair[0] + pair[1] == target);
    boolean found = false;
    for (int i = 0; i < a.length; i++)
      for (int j = i + 1; j < a.length; j++)
        found |= a[i] == pair[0] && a[j] == pair[1] || a[j] == pair[0] && a[i] == pair[1];
    ck(found);
  }

  static int slots(int[][] a) {
    int best = 0;
    for (int mask = 0; mask < (1 << a.length); mask++) {
      List<int[]> list = new ArrayList<>();
      for (int i = 0; i < a.length; i++) if ((mask & (1 << i)) != 0) list.add(a[i]);
      list.sort(Comparator.comparingInt(x -> x[0]));
      boolean ok = true;
      for (int i = 1; i < list.size(); i++) ok &= list.get(i)[0] >= list.get(i - 1)[1];
      if (ok) best = Math.max(best, list.size());
    }
    return best;
  }

  static long water(int[] a) {
    long total = 0;
    for (int i = 0; i < a.length; i++) {
      int l = 0, r = 0;
      for (int j = 0; j <= i; j++) l = Math.max(l, a[j]);
      for (int j = i; j < a.length; j++) r = Math.max(r, a[j]);
      total += Math.min(l, r) - a[i];
    }
    return total;
  }

  public static void main(String[] args) {
    pairCheck(
        new int[] {2, 7, 4, 5, 3, 8},
        9,
        W06CatalogueReference.pair(new int[] {2, 7, 4, 5, 3, 8}, 9));
    pairCheck(new int[] {2, 7, 4, 5}, 9, W06CatalogueReference.pair(new int[] {2, 7, 4, 5}, 9));
    pairCheck(new int[] {3}, 6, W06CatalogueReference.pair(new int[] {3}, 6));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3, 4, 8}, new int[] {2, 3, 6, 9}),
            new int[] {1, 2, 3, 3, 4, 6, 8, 9}));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        W06CatalogueReference.maxSlots(
                new int[][] {{1, 3}, {2, 5}, {4, 6}, {6, 8}, {5, 9}, {8, 10}})
            == 4);
    ck(W06CatalogueReference.maxSlots(new int[][] {{1, 3}, {3, 5}}) == 2);
    ck(W06CatalogueReference.maxSlots(new int[][] {}) == 0);
    pairCheck(
        new int[] {499, 650, 250, 750, 300, 820},
        1000,
        W06CatalogueReference.pair(new int[] {499, 650, 250, 750, 300, 820}, 1000));
    pairCheck(new int[] {2, 7, 4, 5}, 9, W06CatalogueReference.pair(new int[] {2, 7, 4, 5}, 9));
    pairCheck(new int[] {3}, 6, W06CatalogueReference.pair(new int[] {3}, 6));
    ck(
        W06CatalogueReference.maxSlots(
                new int[][] {{0, 40}, {30, 70}, {45, 90}, {95, 130}, {70, 100}})
            == 3);
    ck(W06CatalogueReference.maxSlots(new int[][] {{1, 3}, {3, 5}}) == 2);
    ck(W06CatalogueReference.maxSlots(new int[][] {}) == 0);
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 4, 6, 9}, new int[] {2, 3, 7, 10}),
            new int[] {1, 2, 3, 4, 6, 7, 9, 10}));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {3, 8, 15, 21}, new int[] {5, 8, 30}),
            new int[] {3, 5, 8, 8, 15, 21, 30}));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        W06CatalogueReference.maxSlots(
                new int[][] {{10, 12}, {11, 13}, {12, 14}, {14, 16}, {13, 17}})
            == 3);
    ck(W06CatalogueReference.maxSlots(new int[][] {{1, 3}, {3, 5}}) == 2);
    ck(W06CatalogueReference.maxSlots(new int[][] {}) == 0);
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {2, 5, 9, 14}, new int[] {3, 5, 11}),
            new int[] {2, 3, 5, 5, 9, 11, 14}));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 4, 6, 9}, new int[] {2, 3, 7, 10}),
            new int[] {1, 2, 3, 4, 6, 7, 9, 10}));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {3, 8, 11, 21}, new int[] {10, 11, 14, 15}),
            new int[] {3, 8, 10, 11, 11, 14, 15, 21}));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {5, 12, 30}, new int[] {7, 12, 18, 40}),
            new int[] {5, 7, 12, 12, 18, 30, 40}));
    ck(
        Arrays.equals(
            W06CatalogueReference.merge(new int[] {1, 3}, new int[] {2, 3}),
            new int[] {1, 2, 3, 3}));
    ck(W06CatalogueReference.merge(new int[] {}, new int[] {}).length == 0);
    ck(
        W06CatalogueReference.maxSlots(
                new int[][] {{9, 10}, {9, 12}, {10, 11}, {11, 13}, {12, 14}, {13, 15}})
            == 4);
    ck(W06CatalogueReference.maxSlots(new int[][] {{1, 3}, {3, 5}}) == 2);
    ck(W06CatalogueReference.maxSlots(new int[][] {}) == 0);
    ck(W06CatalogueReference.water(new int[] {12, 4, 7, 2, 9, 3, 3, 15, 6, 8, 1, 10}) == 59L);
    ck(W06CatalogueReference.water(new int[] {2, 0, 2}) == 2);
    ck(W06CatalogueReference.water(new int[] {}) == 0);
    Random random = new Random(266601);
    for (int t = 0; t < 2000; t++) {
      int[] a = new int[random.nextInt(15)], b = new int[random.nextInt(15)];
      for (int i = 0; i < a.length; i++) a[i] = random.nextInt(31) - 15;
      for (int i = 0; i < b.length; i++) b[i] = random.nextInt(31) - 15;
      int[] before = a.clone();
      int target = random.nextInt(41) - 20;
      pairCheck(a, target, W06CatalogueReference.pair(a, target));
      ck(Arrays.equals(a, before));
      Arrays.sort(a);
      Arrays.sort(b);
      int[] both = new int[a.length + b.length];
      System.arraycopy(a, 0, both, 0, a.length);
      System.arraycopy(b, 0, both, a.length, b.length);
      Arrays.sort(both);
      ck(Arrays.equals(W06CatalogueReference.merge(a, b), both));
      int[][] intervals = new int[random.nextInt(10)][2];
      for (int[] x : intervals) {
        x[0] = random.nextInt(21) - 10;
        x[1] = x[0] + 1 + random.nextInt(8);
      }
      int[][] snap = copy(intervals);
      ck(W06CatalogueReference.maxSlots(intervals) == slots(intervals));
      ck(Arrays.deepEquals(intervals, snap));
      int[] height = new int[random.nextInt(30)];
      for (int i = 0; i < height.length; i++) height[i] = random.nextInt(100);
      ck(W06CatalogueReference.water(height) == water(height));
      int E = 1 + random.nextInt(8), H = 1 + random.nextInt(8);
      int[][] left = new int[random.nextInt(20)][3], right = new int[random.nextInt(20)][3];
      for (int[] x : left) {
        x[0] = random.nextInt(8);
        x[1] = random.nextInt(E);
        x[2] = random.nextInt(H);
      }
      for (int[] x : right) {
        x[0] = random.nextInt(8);
        x[1] = random.nextInt(E);
        x[2] = random.nextInt(H);
      }
      Arrays.sort(left, Comparator.comparingInt(x -> x[0]));
      Arrays.sort(right, Comparator.comparingInt(x -> x[0]));
      int[][] savedLeft = copy(left), savedRight = copy(right);
      List<int[]> expected = new ArrayList<>();
      Collections.addAll(expected, left);
      Collections.addAll(expected, right);
      expected.sort(Comparator.comparingInt(x -> x[0]));
      int[][] merged = ShardHeatmap.merge(left, right, E, H);
      ck(merged.length == expected.size());
      for (int i = 0; i < merged.length; i++) {
        ck(Arrays.equals(merged[i], expected.get(i)));
        for (int[] source : left) ck(merged[i] != source);
        for (int[] source : right) ck(merged[i] != source);
      }
      long[][] heat = ShardHeatmap.heat(merged, E, H), direct = new long[E][H];
      for (int[] x : expected) direct[x[1]][x[2]]++;
      ck(Arrays.deepEquals(heat, direct));
      long total = 0;
      for (long[] row : heat) for (long x : row) total += x;
      ck(total == merged.length);
      ck(Arrays.deepEquals(left, savedLeft) && Arrays.deepEquals(right, savedRight));
      if (merged.length > 0) {
        merged[0][0]++;
        ck(Arrays.deepEquals(left, savedLeft) && Arrays.deepEquals(right, savedRight));
      }
    }
    ck(
        Arrays.deepEquals(
            ShardHeatmap.merge(
                new int[][] {{1, 0, 0}, {1, 0, 1}}, new int[][] {{1, 1, 0}, {1, 1, 1}}, 2, 2),
            new int[][] {{1, 0, 0}, {1, 0, 1}, {1, 1, 0}, {1, 1, 1}}));
    ck(ShardHeatmap.merge(new int[][] {}, new int[][] {}, 1, 1).length == 0);
    ck(ShardHeatmap.heat(new int[][] {}, 1, 1)[0][0] == 0);
    rejects(() -> ShardHeatmap.merge(null, new int[][] {}, 1, 1));
    rejects(() -> ShardHeatmap.merge(new int[][] {}, new int[][] {}, 101, 1));
    rejects(() -> ShardHeatmap.heat(new int[][] {}, 1, 101));
    rejects(() -> ShardHeatmap.heat(new int[][] {}, 0, 1));
    rejects(() -> ShardHeatmap.heat(new int[][] {null}, 1, 1));
    rejects(() -> ShardHeatmap.heat(new int[][] {{0, 0}}, 1, 1));
    rejects(() -> ShardHeatmap.heat(new int[][] {{-1, 0, 0}}, 1, 1));
    rejects(() -> ShardHeatmap.heat(new int[][] {{0, 1, 0}}, 1, 1));
    rejects(() -> ShardHeatmap.heat(new int[][] {{0, 0, 1}}, 1, 1));
    rejects(() -> ShardHeatmap.heat(new int[][] {{2, 0, 0}, {1, 0, 0}}, 1, 1));
    int[][] max = new int[100000][];
    Arrays.fill(max, new int[] {Integer.MAX_VALUE, 99, 99});
    ck(ShardHeatmap.merge(max, new int[][] {}, 100, 100).length == 100000);
    ck(ShardHeatmap.heat(max, 100, 100)[99][99] == 100000);
    rejects(() -> ShardHeatmap.merge(max, new int[][] {{Integer.MAX_VALUE, 0, 0}}, 100, 100));
    int[][] oversize = new int[100001][];
    Arrays.fill(oversize, new int[] {0, 0, 0});
    rejects(() -> ShardHeatmap.heat(oversize, 1, 1));
    pairCheck(
        new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE},
        -2,
        W06CatalogueReference.pair(new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE}, -2));
    pairCheck(new int[] {3, 3}, 6, W06CatalogueReference.pair(new int[] {3, 3}, 6));
    ck(
        W06CatalogueReference.maxSlots(
                new int[][] {{Integer.MIN_VALUE, -1}, {0, Integer.MAX_VALUE}})
            == 2);
    int[] large = new int[1000];
    large[0] = large[999] = 1000000000;
    ck(W06CatalogueReference.water(large) == 998000000000L);
    rejects(() -> W06CatalogueReference.merge(new int[] {2, 1}, new int[] {}));
    rejects(() -> W06CatalogueReference.merge(new int[1000], new int[1]));
    rejects(() -> W06CatalogueReference.pair(null, 0));
    rejects(() -> W06CatalogueReference.maxSlots(new int[][] {{1, 1}}));
    rejects(() -> W06CatalogueReference.water(new int[] {-1}));
    System.out.println(
        "PASS: "
            + checks
            + " project assertions;14 catalogue samples +28 listed additional cases;4 domain"
            + " families,stable copied shard merge,heat-map,bounds and long arithmetic.");
  }
}
