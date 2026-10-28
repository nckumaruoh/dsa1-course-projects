import java.util.*;

public class W10ProjectTests {
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

  static void topCheck(int[] a, int k, int[] out) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int x : a) freq.merge(x, 1, Integer::sum);
    Set<Integer> seen = new HashSet<>();
    ck(out.length == k);
    int previous = Integer.MAX_VALUE;
    for (int x : out) {
      ck(freq.containsKey(x) && seen.add(x));
      ck(freq.get(x) <= previous);
      previous = freq.get(x);
    }
    if (k > 0) for (int x : freq.keySet()) if (!seen.contains(x)) ck(freq.get(x) <= previous);
  }

  static int[][] union(int[][] a) {
    boolean[] cover = new boolean[101];
    for (int[] x : a) for (int i = x[0] * 2; i <= x[1] * 2; i++) cover[i] = true;
    List<int[]> out = new ArrayList<>();
    for (int l = 0; l < cover.length; ) {
      if (!cover[l]) {
        l++;
        continue;
      }
      int r = l;
      while (r + 1 < cover.length && cover[r + 1]) r++;
      out.add(new int[] {l / 2, r / 2});
      l = r + 1;
    }
    return out.toArray(new int[0][]);
  }

  public static void main(String[] args) {
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"Rohit", "Gill", "Kohli", "Pant"},
                new int[] {45, 52, 45, 38},
                new int[] {30, 41, 28, 22},
                false),
            new String[] {"Gill", "Kohli", "Rohit", "Pant"}));
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"A", "B", "C"}, new int[] {10, 10, 10}, new int[] {2, 2, 1}, false),
            new String[] {"C", "A", "B"}));
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"A", "B", "C"}, new int[] {10, 10, 10}, new int[] {2, 2, 1}, true),
            new String[] {"A", "B", "C"}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(
                new int[][] {{9, 13}, {12, 15}, {18, 22}, {21, 23}, {14, 16}}),
            new int[][] {{9, 16}, {18, 23}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 2}, {2, 3}}),
            new int[][] {{1, 3}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 4}, {2, 3}}),
            new int[][] {{1, 4}}));
    ck(Week10ProjectReference.mergeIntervals(new int[][] {}).length == 0);
    rejects(() -> Week10ProjectReference.mergeIntervals(new int[][] {{3, 1}}));
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"Asha", "Bala", "Chitra", "Dev"},
                new int[] {410, 425, 410, 398},
                new int[] {88, 90, 95, 99},
                true),
            new String[] {"Bala", "Chitra", "Asha", "Dev"}));
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"A", "B", "C"}, new int[] {10, 10, 10}, new int[] {2, 2, 1}, false),
            new String[] {"C", "A", "B"}));
    ck(
        Arrays.equals(
            Week10ProjectReference.rank(
                new String[] {"A", "B", "C"}, new int[] {10, 10, 10}, new int[] {2, 2, 1}, true),
            new String[] {"A", "B", "C"}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(
                new int[][] {{6, 10}, {9, 12}, {14, 18}, {17, 21}, {12, 13}}),
            new int[][] {{6, 13}, {14, 21}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 2}, {2, 3}}),
            new int[][] {{1, 3}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 4}, {2, 3}}),
            new int[][] {{1, 4}}));
    ck(Week10ProjectReference.mergeIntervals(new int[][] {}).length == 0);
    rejects(() -> Week10ProjectReference.mergeIntervals(new int[][] {{3, 1}}));
    ck(
        Arrays.equals(
            Week10ProjectReference.topKLinear(new int[] {3, 1, 3, 5, 1, 3, 7, 5, 5, 3}, 3),
            new int[] {3, 5, 1}));
    topCheck(
        new int[] {1, 1, 2, 2, 3},
        2,
        Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 2));
    ck(Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 0).length == 0);
    rejects(() -> Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 4));
    ck(
        Arrays.equals(
            Week10ProjectReference.topKLinear(new int[] {5, 2, 5, 8, 2, 5, 9, 2, 2}, 2),
            new int[] {2, 5}));
    topCheck(
        new int[] {1, 1, 2, 2, 3},
        2,
        Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 2));
    ck(Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 0).length == 0);
    rejects(() -> Week10ProjectReference.topKLinear(new int[] {1, 1, 2, 2, 3}, 4));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(
                new int[][] {{1, 4}, {2, 6}, {8, 10}, {9, 12}, {15, 18}}),
            new int[][] {{1, 6}, {8, 12}, {15, 18}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 2}, {2, 3}}),
            new int[][] {{1, 3}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 4}, {2, 3}}),
            new int[][] {{1, 4}}));
    ck(Week10ProjectReference.mergeIntervals(new int[][] {}).length == 0);
    rejects(() -> Week10ProjectReference.mergeIntervals(new int[][] {{3, 1}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(
                new int[][] {{8, 10}, {9, 11}, {13, 14}, {11, 12}, {15, 17}}),
            new int[][] {{8, 12}, {13, 14}, {15, 17}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 2}, {2, 3}}),
            new int[][] {{1, 3}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 4}, {2, 3}}),
            new int[][] {{1, 4}}));
    ck(Week10ProjectReference.mergeIntervals(new int[][] {}).length == 0);
    rejects(() -> Week10ProjectReference.mergeIntervals(new int[][] {{3, 1}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(
                new int[][] {{9, 11}, {10, 12}, {13, 15}, {14, 17}, {18, 19}}),
            new int[][] {{9, 12}, {13, 17}, {18, 19}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 2}, {2, 3}}),
            new int[][] {{1, 3}}));
    ck(
        Arrays.deepEquals(
            Week10ProjectReference.mergeIntervals(new int[][] {{1, 4}, {2, 3}}),
            new int[][] {{1, 4}}));
    ck(Week10ProjectReference.mergeIntervals(new int[][] {}).length == 0);
    rejects(() -> Week10ProjectReference.mergeIntervals(new int[][] {{3, 1}}));
    Random r = new Random(261010);
    for (int round = 0; round < 1500; round++) {
      int n = r.nextInt(30), cut = r.nextInt(n + 1);
      Week10ProjectReference.Event[] events = new Week10ProjectReference.Event[n];
      long[] times = new long[n];
      for (int i = 0; i < n; i++) {
        times[i] = r.nextInt(15) - 7;
        events[i] =
            new Week10ProjectReference.Event(
                times[i], i, "/" + (char) ('a' + r.nextInt(6)), r.nextInt(100));
      }
      var a = Arrays.copyOfRange(events, 0, cut);
      var b = Arrays.copyOfRange(events, cut, n);
      Arrays.sort(a, Week10ProjectReference::cmp);
      Arrays.sort(b, Week10ProjectReference::cmp);
      var savedA = a.clone();
      var savedB = b.clone();
      var expected = events.clone();
      Arrays.sort(expected, Week10ProjectReference::cmp);
      var merged = Week10ProjectReference.mergeShards(a, b);
      ck(Arrays.equals(merged, expected));
      ck(Arrays.equals(a, savedA) && Arrays.equals(b, savedB));
      ck(Week10ProjectReference.mergeComparisons <= Math.max(0, n - 1));
      ck(Week10ProjectReference.mergeArraySlots == n);
      ck(
          Week10ProjectReference.validationComparisons
              == Math.max(0, a.length - 1) + Math.max(0, b.length - 1));
      long inversions = 0;
      for (int i = 0; i < n; i++)
        for (int j = i + 1; j < n; j++) if (times[i] > times[j]) inversions++;
      long[] snapshot = times.clone();
      ck(Week10ProjectReference.inversions(times) == inversions);
      ck(Arrays.equals(times, snapshot));
      ck(Week10ProjectReference.inversionArraySlots == 2L * n);
      Map<String, Integer> max = new HashMap<>();
      for (var e : events) max.merge(e.endpoint, e.latency, Math::max);
      String[] keys = max.keySet().toArray(new String[0]);
      for (int i = 0; i < keys.length; i++)
        for (int j = i + 1; j < keys.length; j++)
          if (max.get(keys[j]) > max.get(keys[i])
              || max.get(keys[j]).equals(max.get(keys[i])) && keys[j].compareTo(keys[i]) < 0) {
            String swap = keys[i];
            keys[i] = keys[j];
            keys[j] = swap;
          }
      int k = r.nextInt(keys.length + 1);
      ck(Arrays.equals(Week10ProjectReference.topEndpoints(events, k), Arrays.copyOf(keys, k)));
      int[] ids = new int[n];
      for (int i = 0; i < n; i++) ids[i] = r.nextInt(10) - 5;
      int distinct = (int) Arrays.stream(ids).distinct().count();
      k = r.nextInt(distinct + 1);
      topCheck(ids, k, Week10ProjectReference.topKLinear(ids, k));
      topCheck(ids, k, Week10ProjectReference.topK(ids, k));
      int[][] intervals = new int[r.nextInt(12)][2];
      for (int[] x : intervals) {
        x[0] = r.nextInt(20);
        x[1] = x[0] + r.nextInt(10);
      }
      int[][] before = copy(intervals);
      ck(Arrays.deepEquals(Week10ProjectReference.mergeIntervals(intervals), union(intervals)));
      ck(Arrays.deepEquals(intervals, before));
      String[] names = new String[n];
      int[] primary = new int[n], secondary = new int[n];
      List<Integer> order = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        names[i] = "n" + i;
        primary[i] = r.nextInt(4);
        secondary[i] = r.nextInt(4);
        order.add(i);
      }
      for (boolean descending : new boolean[] {false, true}) {
        List<Integer> wanted = new ArrayList<>(order);
        for (int i = 0; i < wanted.size(); i++)
          for (int j = i + 1; j < wanted.size(); j++) {
            int x = wanted.get(i), y = wanted.get(j);
            if (primary[y] > primary[x]
                || primary[y] == primary[x]
                    && (descending ? secondary[y] > secondary[x] : secondary[y] < secondary[x])
                || primary[y] == primary[x] && secondary[y] == secondary[x] && y < x) {
              wanted.set(i, y);
              wanted.set(j, x);
            }
          }
        String[] actual = Week10ProjectReference.rank(names, primary, secondary, descending);
        for (int i = 0; i < n; i++) ck(actual[i].equals(names[wanted.get(i)]));
      }
    }
    var merged =
        Week10ProjectReference.mergeShards(
            Week10ProjectReference.shardA(), Week10ProjectReference.shardB());
    ck(Arrays.equals(Arrays.stream(merged).mapToInt(e -> e.seq).toArray(), new int[] {0, 1, 2, 3}));
    ck(Arrays.equals(Week10ProjectReference.topEndpoints(merged, 2), new String[] {"/b", "/c"}));
    ck(Week10ProjectReference.inversions(new long[] {20, 10, 10, 30}) == 2);
    ck(Week10ProjectReference.inversions(new long[] {10, 10, 20}) == 0);
    ck(Week10ProjectReference.inversions(new long[] {}) == 0);
    long[] reverse = new long[100000];
    for (int i = 0; i < reverse.length; i++) reverse[i] = reverse.length - i;
    ck(Week10ProjectReference.inversions(reverse) == 4999950000L);
    Map<String, int[][]> rooms = new LinkedHashMap<>();
    rooms.put("r1", new int[][] {{1, 3}, {2, 4}});
    rooms.put("r2", new int[][] {{2, 5}});
    var roomResult = Week10ProjectReference.mergeByRoom(rooms);
    ck(Arrays.deepEquals(roomResult.get("r1"), new int[][] {{1, 4}}));
    ck(Arrays.deepEquals(roomResult.get("r2"), new int[][] {{2, 5}}));
    rejects(() -> new Week10ProjectReference.Event(0, -1, "/a", 0));
    rejects(() -> new Week10ProjectReference.Event(0, 0, null, 0));
    rejects(() -> new Week10ProjectReference.Event(0, 0, "a".repeat(101), 0));
    rejects(() -> new Week10ProjectReference.Event(0, 0, "/a", 1000000001));
    rejects(
        () ->
            Week10ProjectReference.mergeShards(
                new Week10ProjectReference.Event[] {
                  new Week10ProjectReference.Event(0, 1, "/a", 0)
                },
                new Week10ProjectReference.Event[] {
                  new Week10ProjectReference.Event(1, 1, "/b", 0)
                }));
    rejects(
        () ->
            Week10ProjectReference.mergeShards(
                new Week10ProjectReference.Event[100001], new Week10ProjectReference.Event[] {}));
    rejects(
        () ->
            Week10ProjectReference.mergeShards(
                new Week10ProjectReference.Event[] {null}, new Week10ProjectReference.Event[] {}));
    rejects(
        () ->
            Week10ProjectReference.mergeShards(
                new Week10ProjectReference.Event[] {
                  new Week10ProjectReference.Event(2, 0, "a", 0),
                  new Week10ProjectReference.Event(1, 1, "a", 0)
                },
                new Week10ProjectReference.Event[] {}));
    rejects(
        () -> Week10ProjectReference.rank(new String[] {"a"}, new int[] {}, new int[] {1}, false));
    rejects(() -> Week10ProjectReference.mergeIntervals(new int[][] {{1}}));
    rejects(() -> Week10ProjectReference.inversions(new long[100001]));
    rejects(() -> Week10ProjectReference.topEndpoints(merged, 4));
    System.out.println(
        "PASS: "
            + checks
            + " project assertions;9 source outputs +30 listed additional"
            + " cases;merge/rank/interval/top-k families,unique IDs,room scope,inversions,counters"
            + " and bounds.");
  }
}
