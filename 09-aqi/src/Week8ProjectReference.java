import java.util.*;

public class Week8ProjectReference {
  private final long[] times;
  public long comparisons, buildComparisons;

  public Week8ProjectReference(long[] input) {
    if (input == null || input.length > 100000)
      throw new IllegalArgumentException("nonnull n<=100000");
    times = input.clone();
    for (int i = 1; i < times.length; i++) {
      buildComparisons++;
      if (times[i] < times[i - 1]) throw new IllegalArgumentException("unsorted timestamps");
    }
  }

  private int bound(long key, boolean strict) {
    int l = 0, r = times.length;
    while (l < r) {
      int m = l + (r - l) / 2;
      comparisons++;
      if (strict ? times[m] <= key : times[m] < key) l = m + 1;
      else r = m;
    }
    return l;
  }

  public int lower(long key) {
    comparisons = 0;
    return bound(key, false);
  }

  public int upper(long key) {
    comparisons = 0;
    return bound(key, true);
  }

  public int halfOpen(long start, long end) {
    if (start > end) throw new IllegalArgumentException("reversed range");
    comparisons = 0;
    int hi = bound(end, false), lo = bound(start, false);
    return hi - lo;
  }

  public int closed(long start, long end) {
    if (start > end) throw new IllegalArgumentException("reversed range");
    comparisons = 0;
    int hi = bound(end, true), lo = bound(start, false);
    return hi - lo;
  }

  // Helpers below assume the catalogue's sorted input contract. Validate once when building the
  // index.
  static int lowerArray(int[] a, int key) {
    int l = 0, r = a.length;
    while (l < r) {
      int m = l + (r - l) / 2;
      if (a[m] < key) l = m + 1;
      else r = m;
    }
    return l;
  }

  static int countArray(int[] a, int lo, int hi) {
    if (lo > hi) throw new IllegalArgumentException("range");
    return lowerArray(a, hi) - lowerArray(a, lo);
  }

  static int lowerWords(String[] a, String key) {
    int l = 0, r = a.length;
    while (l < r) {
      int m = l + (r - l) / 2;
      if (a[m].compareTo(key) < 0) l = m + 1;
      else r = m;
    }
    return l;
  }

  static int countWords(String[] a, String lo, String hi) {
    if (lo.compareTo(hi) > 0) throw new IllegalArgumentException("range");
    return lowerWords(a, hi) - lowerWords(a, lo);
  }

  static int[] prefixBounds(String[] a, String prefix) {
    int lo = lowerWords(a, prefix);
    char[] c = prefix.toCharArray();
    int i = c.length - 1;
    while (i >= 0 && c[i] == 'z') i--;
    int hi;
    if (i < 0) hi = a.length;
    else {
      c[i]++;
      String successor = new String(c, 0, i + 1);
      hi = lowerWords(a, successor);
    }
    return new int[] {lo, hi};
  }
}
