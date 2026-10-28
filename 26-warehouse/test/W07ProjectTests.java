import java.util.*;

public class W07ProjectTests {
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

  static void close(double a, double b) {
    ck(Math.abs(a - b) <= 1e-10 * Math.max(1, Math.abs(b)));
  }

  static String canonical(List<List<String>> groups) {
    List<String> out = new ArrayList<>();
    for (var group : groups) {
      List<String> copy = new ArrayList<>(group);
      Collections.sort(copy);
      out.add(copy.toString());
    }
    Collections.sort(out);
    return out.toString();
  }

  static String sorted(String s) {
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
  }

  static boolean subsequence(String a, String b) {
    int i = 0;
    for (int j = 0; j < b.length() && i < a.length(); j++) if (a.charAt(i) == b.charAt(j)) i++;
    return i == a.length();
  }

  static int lcs(String a, String b) {
    int best = 0;
    for (int mask = 0; mask < (1 << a.length()); mask++) {
      StringBuilder s = new StringBuilder();
      for (int i = 0; i < a.length(); i++) if ((mask & (1 << i)) != 0) s.append(a.charAt(i));
      if (subsequence(s.toString(), b)) best = Math.max(best, s.length());
    }
    return best;
  }

  public static void main(String[] args) {
    ck(
        Arrays.equals(
            Week7ProjectReference.bestWindow(new int[] {450, 1200, 2300, 3100, 2800, 900, 600}, 3),
            new long[] {8200, 2}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        Arrays.equals(
            Week7ProjectReference.bestWindow(new int[] {40, 65, 120, 180, 150, 90, 60}, 3),
            new long[] {450, 2}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        Arrays.equals(
            Week7ProjectReference.bestWindow(new int[] {6, 9, 4, 12, 7, 15, 3, 10}, 3),
            new long[] {34, 3}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(Week7ProjectReference.minLength(new int[] {8, 12, 5, 20, 9, 14, 3}, 50) == 5);
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    ck(
        Week7ProjectReference.count("data structures; data science; big data analytics", "data")
            == 3);
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(Week7ProjectReference.count("rice was cold food cold food again", "cold food") == 2);
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(
        Arrays.equals(
            Week7ProjectReference.bestWindow(
                new int[] {8200, 10450, 6300, 12100, 9800, 15020, 4300, 7600, 11200, 9900}, 7),
            new long[] {69920, 3}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        Arrays.equals(
            Week7ProjectReference.bestWindow(new int[] {120, 145, 180, 210, 260, 230, 190, 150}, 3),
            new long[] {700, 3}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(Week7ProjectReference.lcs("abcbdacb", "abdcabcb") == 6);
    ck(Week7ProjectReference.lcs("abcde", "ace") == 3);
    ck(Week7ProjectReference.lcs("abc", "def") == 0);
    ck(Week7ProjectReference.longest(new int[] {3, 3, 1, 2, 1, 1, 2, 3, 3, 4}, 2) == 5);
    ck(Week7ProjectReference.longest(new int[] {1, 2, 1, 3}, 2) == 3);
    ck(Week7ProjectReference.longest(new int[] {1, 2}, 0) == 0);
    ck(Week7ProjectReference.minLength(new int[] {8, 12, 5, 20, 9, 14, 3}, 50) == 5);
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    ck(
        Arrays.equals(
            Week7ProjectReference.bestWindow(new int[] {30, 45, 20, 38, 52, 26}, 2),
            new long[] {90, 3}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(
        canonical(
                Week7ProjectReference.groups(
                    new String[] {"iplfinal", "finalipl", "goat", "toga", "rcb", "bcr", "csk"}))
            .equals("[[bcr, rcb], [csk], [finalipl, iplfinal], [goat, toga]]"));
    ck(
        canonical(Week7ProjectReference.groups(new String[] {"ab", "ba", "a"}))
            .equals("[[a], [ab, ba]]"));
    ck(canonical(Week7ProjectReference.groups(new String[] {""})).equals("[[]]"));
    ck(Week7ProjectReference.lcs("klconnect", "kluniversity") == 5);
    ck(Week7ProjectReference.lcs("abcde", "ace") == 3);
    ck(Week7ProjectReference.lcs("abc", "def") == 0);
    ck(
        Arrays.equals(
            Week7ProjectReference.bestWindow(new int[] {12, 15, 9, 22, 18, 30, 14}, 3),
            new long[] {70, 3}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {1, 2, 3}, 2), new long[] {5, 1}));
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {5}, 1), new long[] {5, 0}));
    ck(Week7ProjectReference.lcs("forijsum", "forjisum") == 7);
    ck(Week7ProjectReference.lcs("abcde", "ace") == 3);
    ck(Week7ProjectReference.lcs("abc", "def") == 0);
    ck(Week7ProjectReference.count("/img/2025/img_01.jpg /img/2026/img_02.jpg", "img") == 4);
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(Week7ProjectReference.count("buy now buy now limited buy now", "buy now") == 3);
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(Week7ProjectReference.minLength(new int[] {7, 2, 5, 10, 8, 3, 6}, 30) == 5);
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    ck(Week7ProjectReference.count("abracadabra abracadabra", "abra") == 4);
    ck(Week7ProjectReference.count("aaaa", "aa") == 3);
    ck(Week7ProjectReference.count("abc", "z") == 0);
    ck(Week7ProjectReference.minLength(new int[] {5, 12, 8, 20, 15, 3, 25}, 50) == 4);
    ck(Week7ProjectReference.minLength(new int[] {2, 3, 1}, 4) == 2);
    ck(Week7ProjectReference.minLength(new int[] {1, 1}, 5) == 0);
    Random random = new Random(267701);
    for (int t = 0; t < 1600; t++) {
      int n = 1 + random.nextInt(15);
      long[] latency = new long[n];
      int[] values = new int[n], nonnegative = new int[n];
      for (int i = 0; i < n; i++) {
        latency[i] = random.nextInt(100);
        values[i] = random.nextInt(41) - 20;
        nonnegative[i] = random.nextInt(10);
      }
      long[] before = latency.clone();
      int k = 1 + random.nextInt(n), budget = random.nextInt(n + 1);
      long threshold = random.nextInt(101);
      double[] means = Week7ProjectReference.rolling(latency, k);
      for (int i = 0; i + k <= n; i++) {
        long sum = 0;
        for (int j = i; j < i + k; j++) sum += latency[j];
        close(means[i], (double) sum / k);
      }
      int stable = 0;
      for (int l = 0; l < n; l++) {
        int bad = 0;
        for (int r = l; r < n; r++) {
          if (latency[r] > threshold) bad++;
          if (bad <= budget) stable = Math.max(stable, r - l + 1);
        }
      }
      ck(Week7ProjectReference.stable(latency, threshold, budget) == stable);
      ck(Arrays.equals(latency, before));
      long best = Long.MIN_VALUE;
      int index = 0;
      for (int i = 0; i + k <= n; i++) {
        long sum = 0;
        for (int j = i; j < i + k; j++) sum += values[j];
        if (sum > best) {
          best = sum;
          index = i;
        }
      }
      ck(Arrays.equals(Week7ProjectReference.bestWindow(values, k), new long[] {best, index}));
      int target = 1 + random.nextInt(40), minimum = n + 1, distinct = 0;
      for (int l = 0; l < n; l++) {
        int sum = 0;
        Set<Integer> seen = new HashSet<>();
        for (int r = l; r < n; r++) {
          sum += nonnegative[r];
          seen.add(values[r]);
          if (sum >= target) minimum = Math.min(minimum, r - l + 1);
          if (seen.size() <= budget) distinct = Math.max(distinct, r - l + 1);
        }
      }
      ck(Week7ProjectReference.minLength(nonnegative, target) == (minimum == n + 1 ? 0 : minimum));
      ck(Week7ProjectReference.longest(values, budget) == distinct);
      StringBuilder a = new StringBuilder(), b = new StringBuilder();
      for (int i = 0; i < random.nextInt(10); i++) a.append((char) ('a' + random.nextInt(3)));
      for (int i = 0; i < 1 + random.nextInt(8); i++) b.append((char) ('a' + random.nextInt(3)));
      String s = a.toString(), pattern = b.toString();
      int count = 0;
      for (int i = 0; i + pattern.length() <= s.length(); i++)
        if (s.startsWith(pattern, i)) count++;
      ck(Week7ProjectReference.count(s, pattern) == count);
      ck(Week7ProjectReference.lcs(s, pattern) == lcs(s, pattern));
      String[] words = {s, pattern, s, new StringBuilder(s).reverse().toString(), ""};
      Map<String, List<String>> expected = new TreeMap<>();
      for (String word : words)
        expected.computeIfAbsent(sorted(word), x -> new ArrayList<>()).add(word);
      ck(
          canonical(Week7ProjectReference.groups(words))
              .equals(canonical(new ArrayList<>(expected.values()))));
    }
    ck(Week7ProjectReference.rolling(new long[] {}, 1).length == 0);
    ck(Week7ProjectReference.stable(new long[] {}, 50, 0) == 0);
    ck(Week7ProjectReference.stable(new long[] {50, 50}, 50, 0) == 2);
    ck(Week7ProjectReference.stable(new long[] {10, 60, 20, 30, 70, 20}, 50, 0) == 2);
    ck(Week7ProjectReference.stable(new long[] {10, 60, 20, 30, 70, 20}, 50, 1) == 4);
    close(Week7ProjectReference.rolling(new long[] {10, 20, 30, 40}, 2)[0], 15);
    close(Week7ProjectReference.rolling(new long[] {10, 20, 30, 40}, 2)[1], 25);
    close(Week7ProjectReference.rolling(new long[] {10, 20, 30, 40}, 2)[2], 35);
    close(Week7ProjectReference.rolling(new long[] {10, 20, 30, 40}, 4)[0], 25);
    close(Week7ProjectReference.rolling(new long[] {7}, 1)[0], 7);
    long[] max = new long[100000];
    Arrays.fill(max, 1000000000L);
    close(Week7ProjectReference.rolling(max, 100000)[0], 1000000000);
    ck(Week7ProjectReference.stable(max, 1000000000, 0) == 100000);
    int[] integers = new int[100000];
    Arrays.fill(integers, Integer.MAX_VALUE);
    ck(Week7ProjectReference.bestWindow(integers, 100000)[0] == 214748364700000L);
    ck(Week7ProjectReference.count("a".repeat(100000), "aa") == 99999);
    ck(Week7ProjectReference.lcs("a".repeat(1000), "a".repeat(1000)) == 1000);
    ck(Arrays.equals(Week7ProjectReference.bestWindow(new int[] {-5, -5}, 1), new long[] {-5, 0}));
    ck(Week7ProjectReference.minLength(new int[] {0, 0, 3, 0}, 3) == 1);
    rejects(() -> Week7ProjectReference.rolling(null, 1));
    rejects(() -> Week7ProjectReference.rolling(new long[100001], 1));
    rejects(() -> Week7ProjectReference.rolling(new long[] {1}, 0));
    rejects(() -> Week7ProjectReference.rolling(new long[] {1}, 2));
    rejects(() -> Week7ProjectReference.stable(new long[] {-1}, 0, 0));
    rejects(() -> Week7ProjectReference.stable(new long[] {1000000001L}, 0, 0));
    rejects(() -> Week7ProjectReference.stable(new long[] {}, -1, 0));
    rejects(() -> Week7ProjectReference.stable(new long[] {}, 0, -1));
    rejects(() -> Week7ProjectReference.minLength(new int[] {-1}, 1));
    rejects(() -> Week7ProjectReference.minLength(new int[] {1}, 0));
    rejects(() -> Week7ProjectReference.bestWindow(new int[] {}, 1));
    rejects(() -> Week7ProjectReference.count("a", ""));
    rejects(() -> Week7ProjectReference.lcs("a".repeat(1001), "a"));
    rejects(() -> Week7ProjectReference.groups(new String[] {"A"}));
    rejects(() -> Week7ProjectReference.groups(new String[] {"a".repeat(100001)}));
    System.out.println(
        "PASS: "
            + checks
            + " project assertions;21 source samples plus42 listed additional cases,rolling/stable"
            + " model,independent oracles,limits and multiplicities.");
  }
}
