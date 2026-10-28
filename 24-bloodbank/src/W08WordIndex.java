import java.util.*;

public class W08WordIndex {
  private final String[] words;
  public long comparisons, buildComparisons;

  static void letters(String s) {
    if (s == null || s.length() > 100000)
      throw new IllegalArgumentException("nonnull text length<=100000");
    for (int i = 0; i < s.length(); i++)
      if (s.charAt(i) < 'a' || s.charAt(i) > 'z')
        throw new IllegalArgumentException("lowercase a..z");
  }

  public W08WordIndex(String[] input) {
    if (input == null || input.length > 100000)
      throw new IllegalArgumentException("nonnull n<=100000");
    long total = 0;
    for (String s : input) {
      letters(s);
      total += s.length();
      if (total > 1000000) throw new IllegalArgumentException("total characters<=1000000");
    }
    words = input.clone();
    for (int i = 1; i < words.length; i++) {
      buildComparisons++;
      if (words[i - 1].compareTo(words[i]) > 0) throw new IllegalArgumentException("sorted words");
    }
  }

  private int bound(String key) {
    int l = 0, r = words.length;
    while (l < r) {
      int m = l + (r - l) / 2;
      comparisons++;
      if (words[m].compareTo(key) < 0) l = m + 1;
      else r = m;
    }
    return l;
  }

  public int lower(String key) {
    letters(key);
    comparisons = 0;
    return bound(key);
  }

  public int count(String lo, String hi) {
    letters(lo);
    letters(hi);
    if (lo.compareTo(hi) > 0) throw new IllegalArgumentException("reversed range");
    comparisons = 0;
    return bound(hi) - bound(lo);
  }

  public int[] prefix(String prefix) {
    letters(prefix);
    comparisons = 0;
    int lo = bound(prefix);
    char[] c = prefix.toCharArray();
    int i = c.length - 1;
    while (i >= 0 && c[i] == 'z') i--;
    int hi;
    if (i < 0) hi = words.length;
    else {
      c[i]++;
      hi = bound(new String(c, 0, i + 1));
    }
    return new int[] {lo, hi};
  }
}
