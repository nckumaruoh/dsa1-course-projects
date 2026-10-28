import java.util.*;

/**
 * Six course increments integrated into a canteen analytics application. All examples use synthetic
 * data. Monetary amounts are integer rupees.
 */
public final class CampusBite {
  private CampusBite() {}

  public record RepeatReport(int repeats, int distinct, String firstRepeat) {
    @Override
    public String toString() {
      return "repeats="
          + repeats
          + ", distinct="
          + distinct
          + ", first repeat="
          + (firstRepeat == null ? "none" : firstRepeat);
    }
  }

  private static void ids(String[] ids) {
    if (ids == null || ids.length > 100_000) throw new IllegalArgumentException("ID count");
    for (String s : ids)
      if (s == null || s.length() > 100)
        throw new IllegalArgumentException("nonnull IDs, at most 100 characters");
  }

  /** Every occurrence after the first counts as a repeat. Input is preserved. */
  public static RepeatReport repeatReport(String[] ids) {
    ids(ids);
    Set<String> seen = new HashSet<>();
    int repeats = 0;
    String first = null;
    for (String id : ids)
      if (!seen.add(id)) {
        repeats++;
        if (first == null) first = id;
      }
    return new RepeatReport(repeats, seen.size(), first);
  }

  public static String repeats(String[] ids) {
    return repeatReport(ids).toString();
  }

  /** Small-input quadratic oracle. Deliberately scans every previous position. */
  public static RepeatReport repeatsPairwise(String[] ids) {
    ids(ids);
    int repeats = 0;
    String first = null;
    for (int i = 0; i < ids.length; i++) {
      boolean duplicate = false;
      for (int j = 0; j < i; j++) if (ids[i].equals(ids[j])) duplicate = true;
      if (duplicate) {
        repeats++;
        if (first == null) first = ids[i];
      }
    }
    return new RepeatReport(repeats, ids.length - repeats, first);
  }

  /** Count-array variant: numeric IDs must lie in the declared universe [0,U). */
  public static RepeatReport repeatsCountArray(int[] ids, int universe) {
    if (ids == null || ids.length > 100_000 || universe < 1 || universe > 100_001)
      throw new IllegalArgumentException("ID count/universe");
    int[] counts = new int[universe];
    int repeats = 0, distinct = 0;
    String first = null;
    for (int id : ids) {
      if (id < 0 || id >= universe) throw new IllegalArgumentException("ID outside universe");
      if (counts[id]++ == 0) distinct++;
      else {
        repeats++;
        if (first == null) first = Integer.toString(id);
      }
    }
    return new RepeatReport(repeats, distinct, first);
  }

  /** One-use subset sum, with a memo for the fixed copied input and capacity. */
  public static int bestTotal(int[] values, int capacity) {
    if (values == null || values.length > 30 || capacity < 0 || capacity > 5000)
      throw new IllegalArgumentException("n<=30, capacity 0..5000");
    int[] a = values.clone();
    for (int x : a) if (x < 0 || x > 5000) throw new IllegalArgumentException("price 0..5000");
    int[][] memo = new int[a.length][capacity + 1];
    for (int[] row : memo) Arrays.fill(row, -1);
    return best(a, 0, capacity, memo);
  }

  private static int best(int[] a, int i, int remaining, int[][] memo) {
    if (i == a.length) return 0;
    if (memo[i][remaining] != -1) return memo[i][remaining];
    int answer = best(a, i + 1, remaining, memo);
    if (a[i] <= remaining) answer = Math.max(answer, a[i] + best(a, i + 1, remaining - a[i], memo));
    return memo[i][remaining] = answer;
  }

  /** Frozen prefix index; construct once, query inclusive [left,right] in O(1). */
  public static final class RevenueIndex {
    private final long[] prefix;

    public RevenueIndex(long[] values) {
      if (values == null || values.length > 1000) throw new IllegalArgumentException("value count");
      prefix = new long[values.length + 1];
      for (int i = 0; i < values.length; i++) {
        if (values[i] < -1_000_000_000L || values[i] > 1_000_000_000L)
          throw new IllegalArgumentException("value bound");
        prefix[i + 1] = prefix[i] + values[i];
      }
    }

    public long query(int left, int right) {
      if (left < 0 || right < left || right >= prefix.length - 1)
        throw new IllegalArgumentException("inclusive query bounds");
      return prefix[right + 1] - prefix[left];
    }
  }

  public static long[] rangeSums(long[] values, int[][] queries) {
    RevenueIndex index = new RevenueIndex(values);
    if (queries == null || queries.length > 1000) throw new IllegalArgumentException("query count");
    long[] result = new long[queries.length];
    for (int i = 0; i < queries.length; i++) {
      if (queries[i] == null || queries[i].length != 2)
        throw new IllegalArgumentException("query shape");
      result[i] = index.query(queries[i][0], queries[i][1]);
    }
    return result;
  }

  /** Return [sum,earliest start] for exactly k slots. Signed slot values are valid. */
  public static long[] bestWindow(int[] values, int k) {
    if (values == null || values.length > 100_000 || k < 1 || k > values.length)
      throw new IllegalArgumentException("window bounds");
    long sum = 0;
    for (int i = 0; i < k; i++) sum += values[i];
    long best = sum;
    int start = 0;
    for (int i = k; i < values.length; i++) {
      sum += (long) values[i] - values[i - k];
      if (sum > best) {
        best = sum;
        start = i - k + 1;
      }
    }
    return new long[] {best, start};
  }

  /** Copy/validate once, then each half-open count query takes O(log n). */
  public static final class TimeIndex {
    private final int[] sorted;

    public TimeIndex(int[] times) {
      if (times == null || times.length > 100_000) throw new IllegalArgumentException("time count");
      sorted = times.clone();
      for (int i = 1; i < sorted.length; i++)
        if (sorted[i] < sorted[i - 1]) throw new IllegalArgumentException("times must be sorted");
    }

    private int lower(int target) {
      int left = 0, right = sorted.length;
      while (left < right) {
        int mid = left + (right - left) / 2;
        if (sorted[mid] < target) left = mid + 1;
        else right = mid;
      }
      return left;
    }

    public int count(int lo, int hi) {
      if (lo > hi) throw new IllegalArgumentException("lo exceeds hi");
      return lower(hi) - lower(lo);
    }
  }

  public static int count(int[] sorted, int lo, int hi) {
    return new TimeIndex(sorted).count(lo, hi);
  }

  public record Service(
      int token, int arrival, int counter, long start, long finish, long waitMinutes) {}

  public record QueueReport(int served, long totalWait, long longestWait, List<Service> trace) {
    public QueueReport {
      trace = List.copyOf(trace);
    }

    @Override
    public String toString() {
      return "served="
          + served
          + ", total wait="
          + totalWait
          + " min, longest wait="
          + longestWait
          + " min";
    }
  }

  /** Circular-array FIFO; counters are zero-based and ties choose the smaller index. */
  public static QueueReport simulate(int[] arrivals, int serviceMinutes, int counters) {
    if (arrivals == null
        || arrivals.length > 100_000
        || serviceMinutes < 1
        || serviceMinutes > 100_000
        || counters < 1
        || counters > 2) throw new IllegalArgumentException("queue bounds");
    int total = 0;
    for (int count : arrivals) {
      if (count < 0 || count > 100_000 - total) throw new IllegalArgumentException("request count");
      total += count;
    }
    int[] queue = new int[Math.max(1, total)];
    int head = 0, size = 0;
    for (int minute = 0; minute < arrivals.length; minute++)
      for (int j = 0; j < arrivals[minute]; j++) queue[(head + size++) % queue.length] = minute;
    long[] available = new long[counters];
    long waitSum = 0, longest = 0;
    List<Service> trace = new ArrayList<>();
    int token = 0;
    while (size > 0) {
      int arrival = queue[head];
      head = (head + 1) % queue.length;
      size--;
      int selected = 0;
      for (int c = 1; c < counters; c++) if (available[c] < available[selected]) selected = c;
      long start = Math.max(arrival, available[selected]);
      long wait = start - arrival;
      available[selected] = start + serviceMinutes;
      waitSum += wait;
      longest = Math.max(longest, wait);
      trace.add(new Service(++token, arrival, selected, start, available[selected], wait));
    }
    return new QueueReport(total, waitSum, longest, trace);
  }

  public static String queueSim(int[] arrivals, int serviceMinutes, int counters) {
    return simulate(arrivals, serviceMinutes, counters).toString();
  }

  public static void main(String[] args) {
    System.out.println("CampusBite — synthetic canteen shift");
    System.out.println(
        repeats(new String[] {"501", "502", "503", "502", "504", "501", "505", "502"}));
    System.out.println(
        "Best combo within Rs 150: " + bestTotal(new int[] {60, 45, 30, 80, 25}, 150));
    System.out.println(
        "Revenue queries: "
            + Arrays.toString(
                rangeSums(
                    new long[] {450, 1200, 2300, 3100, 2800, 900, 600},
                    new int[][] {{2, 4}, {0, 6}})));
    System.out.println(
        "Rush [sum,start]: "
            + Arrays.toString(bestWindow(new int[] {450, 1200, 2300, 3100, 2800, 900, 600}, 3)));
    System.out.println(
        "Orders in [240,300): "
            + count(new int[] {30, 95, 180, 241, 250, 266, 299, 300, 345}, 240, 300));
    int[] arrivals = {1, 2, 1, 0, 2, 1, 0, 0};
    for (int counters = 1; counters <= 2; counters++) {
      QueueReport report = simulate(arrivals, 3, counters);
      System.out.println(counters + " counter(s): " + report);
      if (Arrays.asList(args).contains("--trace")) report.trace().forEach(System.out::println);
    }
  }
}
