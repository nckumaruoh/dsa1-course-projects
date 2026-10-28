import java.util.*;

public class Week10ProjectReference {
  static long validationComparisons,
      mergeComparisons,
      mergeArraySlots,
      rankingComparisons,
      inversionComparisons,
      inversionArraySlots;

  static class Event {
    final long time;
    final int seq, latency;
    final String endpoint;

    Event(long t, int s, String e, int l) {
      if (s < 0 || e == null || e.length() > 100 || l < 0 || l > 1000000000)
        throw new IllegalArgumentException("event fields");
      time = t;
      seq = s;
      endpoint = e;
      latency = l;
    }

    public String toString() {
      return time + ":" + seq + ":" + endpoint + ":" + latency;
    }
  }

  static Event[] shardA() {
    return new Event[] {new Event(10, 0, "/a", 80), new Event(20, 2, "/b", 120)};
  }

  static Event[] shardB() {
    return new Event[] {new Event(10, 1, "/a", 100), new Event(30, 3, "/c", 120)};
  }

  static int cmp(Event a, Event b) {
    int c = Long.compare(a.time, b.time);
    return c != 0 ? c : Integer.compare(a.seq, b.seq);
  }

  static void shape(Object[] a) {
    if (a == null || a.length > 100000) throw new IllegalArgumentException("nonnull n<=100000");
  }

  static void events(Event[] a, boolean ordered, Set<Integer> ids) {
    shape(a);
    for (int i = 0; i < a.length; i++) {
      Event e = a[i];
      if (e == null || !ids.add(e.seq))
        throw new IllegalArgumentException("null event/duplicate arrival ID");
      if (ordered && i > 0) {
        validationComparisons++;
        if (cmp(a[i - 1], e) > 0) throw new IllegalArgumentException("unsorted shard");
      }
    }
  }

  static void validate(Event[] a) {
    validationComparisons = 0;
    events(a, true, new HashSet<>());
  }

  static Event[] mergeShards(Event[] a, Event[] b) {
    shape(a);
    shape(b);
    if ((long) a.length + b.length > 100000)
      throw new IllegalArgumentException("combined n<=100000");
    validationComparisons = mergeComparisons = 0;
    mergeArraySlots = a.length + b.length;
    Set<Integer> ids = new HashSet<>();
    events(a, true, ids);
    events(b, true, ids);
    Event[] out = new Event[a.length + b.length];
    int i = 0, j = 0, z = 0;
    while (i < a.length || j < b.length) {
      boolean left;
      if (j == b.length) left = true;
      else if (i == a.length) left = false;
      else {
        mergeComparisons++;
        left = cmp(a[i], b[j]) <= 0;
      }
      out[z++] = left ? a[i++] : b[j++];
    }
    return out;
  }

  static String[] topEndpoints(Event[] a, int k) {
    events(a, false, new HashSet<>());
    Map<String, Integer> max = new HashMap<>();
    for (Event e : a) max.merge(e.endpoint, e.latency, Math::max);
    if (k < 0 || k > max.size()) throw new IllegalArgumentException("k");
    rankingComparisons = 0;
    List<String> keys = new ArrayList<>(max.keySet());
    keys.sort(
        (x, y) -> {
          rankingComparisons++;
          return !max.get(x).equals(max.get(y))
              ? Integer.compare(max.get(y), max.get(x))
              : x.compareTo(y);
        });
    return keys.subList(0, k).toArray(new String[0]);
  }

  static long inversions(long[] input) {
    if (input == null || input.length > 100000)
      throw new IllegalArgumentException("nonnull n<=100000");
    inversionComparisons = 0;
    inversionArraySlots = 2L * input.length;
    long[] a = input.clone(), tmp = new long[a.length];
    return count(a, tmp, 0, a.length);
  }

  static boolean less(long a, long b) {
    inversionComparisons++;
    return a < b;
  }

  static long count(long[] a, long[] tmp, int l, int r) {
    if (r - l < 2) return 0;
    int m = l + (r - l) / 2;
    long answer = count(a, tmp, l, m) + count(a, tmp, m, r);
    int i = l, j = m, z = l;
    while (i < m || j < r) {
      if (j < r && (i == m || less(a[j], a[i]))) {
        tmp[z++] = a[j++];
        answer += m - i;
      } else tmp[z++] = a[i++];
    }
    for (int t = l; t < r; t++) a[t] = tmp[t];
    return answer;
  }

  static int[][] mergeIntervals(int[][] input) {
    shape(input);
    int[][] a = new int[input.length][];
    for (int i = 0; i < a.length; i++) {
      if (input[i] == null || input[i].length != 2 || input[i][0] > input[i][1])
        throw new IllegalArgumentException("closed pair");
      a[i] = input[i].clone();
    }
    Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
    List<int[]> out = new ArrayList<>();
    for (int[] v : a) {
      if (out.isEmpty() || v[0] > out.get(out.size() - 1)[1]) out.add(v);
      else out.get(out.size() - 1)[1] = Math.max(out.get(out.size() - 1)[1], v[1]);
    }
    return out.toArray(new int[0][]);
  }

  static Map<String, int[][]> mergeByRoom(Map<String, int[][]> input) {
    if (input == null) throw new IllegalArgumentException("rooms");
    long total = 0;
    for (var entry : input.entrySet()) {
      if (entry.getKey() == null) throw new IllegalArgumentException("room ID");
      shape(entry.getValue());
      total += entry.getValue().length;
      if (total > 100000) throw new IllegalArgumentException("total intervals");
    }
    Map<String, int[][]> out = new LinkedHashMap<>();
    for (var entry : input.entrySet()) out.put(entry.getKey(), mergeIntervals(entry.getValue()));
    return out;
  }

  static String[] rank(String[] names, int[] primary, int[] secondary, boolean secondaryDesc) {
    shape(names);
    if (primary == null
        || secondary == null
        || primary.length != names.length
        || secondary.length != names.length) throw new IllegalArgumentException("aligned arrays");
    for (String s : names) if (s == null) throw new IllegalArgumentException("nonnull names");
    Integer[] idx = new Integer[names.length];
    for (int i = 0; i < idx.length; i++) idx[i] = i;
    Arrays.sort(
        idx,
        (i, j) -> {
          int c = Integer.compare(primary[j], primary[i]);
          if (c != 0) return c;
          c =
              secondaryDesc
                  ? Integer.compare(secondary[j], secondary[i])
                  : Integer.compare(secondary[i], secondary[j]);
          return c != 0 ? c : Integer.compare(i, j);
        });
    String[] out = new String[idx.length];
    for (int i = 0; i < idx.length; i++) out[i] = names[idx[i]];
    return out;
  }

  static void numbers(int[] a) {
    if (a == null || a.length > 100000) throw new IllegalArgumentException("nonnull n<=100000");
  }

  static int[] topK(int[] a, int k) {
    numbers(a);
    Map<Integer, Integer> f = new HashMap<>();
    for (int x : a) f.merge(x, 1, Integer::sum);
    if (k < 0 || k > f.size()) throw new IllegalArgumentException("k");
    List<Integer> keys = new ArrayList<>(f.keySet());
    keys.sort(
        (x, y) ->
            !f.get(x).equals(f.get(y))
                ? Integer.compare(f.get(y), f.get(x))
                : Integer.compare(x, y));
    int[] out = new int[k];
    for (int i = 0; i < k; i++) out[i] = keys.get(i);
    return out;
  }

  static int[] topKLinear(int[] a, int k) {
    numbers(a);
    Map<Integer, Integer> f = new HashMap<>();
    for (int x : a) f.merge(x, 1, Integer::sum);
    if (k < 0 || k > f.size()) throw new IllegalArgumentException("k");
    List<List<Integer>> b = new ArrayList<>();
    for (int i = 0; i <= a.length; i++) b.add(new ArrayList<>());
    for (var e : f.entrySet()) b.get(e.getValue()).add(e.getKey());
    int[] out = new int[k];
    int z = 0;
    for (int c = a.length; c > 0 && z < k; c--)
      for (int x : b.get(c)) {
        out[z++] = x;
        if (z == k) break;
      }
    return out;
  }
}
