import java.util.*;

public class Week7ProjectReference {
  static void valid(long[] a) {
    if (a == null || a.length > 100000) throw new IllegalArgumentException("nonnull n<=100000");
    for (long x : a)
      if (x < 0 || x > 1000000000L) throw new IllegalArgumentException("latency0..10^9");
  }

  static void array(int[] a) {
    if (a == null || a.length > 100000) throw new IllegalArgumentException("nonnull n<=100000");
  }

  static void text(String s, int max) {
    if (s == null || s.length() > max) throw new IllegalArgumentException("nonnull bounded text");
  }

  static double[] rolling(long[] a, int k) {
    valid(a);
    if (k < 1 || a.length > 0 && k > a.length) throw new IllegalArgumentException("window");
    if (a.length == 0) return new double[0];
    double[] out = new double[a.length - k + 1];
    long sum = 0;
    for (int i = 0; i < a.length; i++) {
      sum = Math.addExact(sum, a[i]);
      if (i >= k) sum = Math.subtractExact(sum, a[i - k]);
      if (i >= k - 1) out[i - k + 1] = (double) sum / k;
    }
    return out;
  }

  static int stable(long[] a, long threshold, int budget) {
    valid(a);
    if (threshold < 0 || budget < 0) throw new IllegalArgumentException("negative policy");
    int l = 0, bad = 0, best = 0;
    for (int r = 0; r < a.length; r++) {
      if (a[r] > threshold) bad++;
      while (bad > budget) if (a[l++] > threshold) bad--;
      best = Math.max(best, r - l + 1);
    }
    return best;
  }

  static long[] bestWindow(int[] a, int k) {
    array(a);
    if (k < 1 || k > a.length) throw new IllegalArgumentException("window");
    long sum = 0, best = Long.MIN_VALUE;
    int start = 0;
    for (int i = 0; i < a.length; i++) {
      sum = Math.addExact(sum, a[i]);
      if (i >= k) sum = Math.subtractExact(sum, a[i - k]);
      if (i >= k - 1 && sum > best) {
        best = sum;
        start = i - k + 1;
      }
    }
    return new long[] {best, start};
  }

  static int minLength(int[] a, int target) {
    array(a);
    if (target <= 0) throw new IllegalArgumentException("positive target");
    for (int x : a) if (x < 0) throw new IllegalArgumentException("nonnegative counts");
    long sum = 0;
    int l = 0, best = a.length + 1;
    for (int r = 0; r < a.length; r++) {
      sum = Math.addExact(sum, a[r]);
      while (sum >= target) {
        best = Math.min(best, r - l + 1);
        sum = Math.subtractExact(sum, a[l++]);
      }
    }
    return best > a.length ? 0 : best;
  }

  static int count(String s, String pattern) {
    text(s, 100000);
    text(pattern, 100000);
    if (pattern.isEmpty()) throw new IllegalArgumentException("nonempty pattern");
    int[] lps = new int[pattern.length()];
    for (int i = 1, j = 0; i < lps.length; i++) {
      while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) j = lps[j - 1];
      if (pattern.charAt(i) == pattern.charAt(j)) j++;
      lps[i] = j;
    }
    int total = 0;
    for (int i = 0, j = 0; i < s.length(); i++) {
      while (j > 0 && s.charAt(i) != pattern.charAt(j)) j = lps[j - 1];
      if (s.charAt(i) == pattern.charAt(j)) j++;
      if (j == pattern.length()) {
        total++;
        j = lps[j - 1];
      }
    }
    return total;
  }

  static int distinct(int[] a, int k) {
    array(a);
    if (k < 0) throw new IllegalArgumentException("nonnegative k");
    int l = 0, best = 0;
    Map<Integer, Integer> m = new HashMap<>();
    for (int r = 0; r < a.length; r++) {
      m.merge(a[r], 1, Integer::sum);
      while (m.size() > k) {
        int x = a[l++], v = m.get(x) - 1;
        if (v == 0) m.remove(x);
        else m.put(x, v);
      }
      best = Math.max(best, r - l + 1);
    }
    return best;
  }

  static int longest(int[] a, int k) {
    return distinct(a, k);
  }

  static int lcs(String a, String b) {
    text(a, 1000);
    text(b, 1000);
    int[][] dp = new int[a.length() + 1][b.length() + 1];
    for (int i = 1; i <= a.length(); i++)
      for (int j = 1; j <= b.length(); j++)
        dp[i][j] =
            a.charAt(i - 1) == b.charAt(j - 1)
                ? 1 + dp[i - 1][j - 1]
                : Math.max(dp[i - 1][j], dp[i][j - 1]);
    return dp[a.length()][b.length()];
  }

  static List<List<String>> groups(String[] words) {
    if (words == null || words.length > 100000) throw new IllegalArgumentException("word count");
    long total = 0;
    for (String word : words) {
      text(word, 100000);
      total += word.length();
      if (total > 100000) throw new IllegalArgumentException("total characters<=100000");
      for (int i = 0; i < word.length(); i++)
        if (word.charAt(i) < 'a' || word.charAt(i) > 'z')
          throw new IllegalArgumentException("lowercase alphabet");
    }
    Map<String, List<String>> map = new LinkedHashMap<>();
    for (String word : words) {
      int[] f = new int[26];
      for (int i = 0; i < word.length(); i++) f[word.charAt(i) - 'a']++;
      map.computeIfAbsent(Arrays.toString(f), key -> new ArrayList<>()).add(word);
    }
    return new ArrayList<>(map.values());
  }
}
