import java.util.*;

public class W08ProjectTests {
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

  static int limit(int n) {
    return n == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(n);
  }

  public static void main(String[] args) {
    ck(
        W08CatalogueReference.count(new int[] {30, 95, 180, 241, 250, 266, 299, 300, 345}, 240, 300)
            == 4);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(W08CatalogueReference.lower(new int[] {6, 15, 19, 31, 38, 53, 56, 66}, 50) == 5);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(
        W08CatalogueReference.lower(new int[] {1002, 1450, 2210, 3999, 4417, 5120, 7004}, 4417)
            == 4);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(W08CatalogueReference.count(new int[] {1, 1, 2, 2, 3, 3, 3, 4, 5, 5}, 2, 4) == 5);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(W08CatalogueReference.lower(new int[] {60, 120, 180, 240, 360}, 135) == 2);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(
        W08CatalogueReference.count(
                new int[] {120, 145, 150, 180, 190, 210, 230, 260, 310}, 200, 300)
            == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(W08CatalogueReference.lower(new int[] {210, 395, 635, 835, 1095, 1290}, 700) == 3);
    ck(W08CatalogueReference.lower(new int[] {210, 395}, 210) == 0);
    ck(W08CatalogueReference.lower(new int[] {210, 395}, 211) == 1);
    ck(W08CatalogueReference.lower(new int[] {210, 395}, 396) == 2);
    ck(W08CatalogueReference.lower(new int[] {2, 3, 6, 8, 13, 15, 16}, 15) == 5);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(
        W08CatalogueReference.count(new int[] {20, 75, 100, 130, 155, 199, 200, 260}, 100, 200)
            == 4);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(
        W08CatalogueReference.count(
                new String[] {"bread", "chai", "chakli", "chana", "cheese", "curd"}, "cha", "chb")
            == 3);
    ck(
        java.util.Arrays.equals(
            new W08WordIndex(new String[] {"a", "z", "zz"}).prefix("z"), new int[] {1, 3}));
    ck(
        java.util.Arrays.equals(
            new W08WordIndex(new String[] {"a", "z", "zz"}).prefix(""), new int[] {0, 3}));
    ck(
        new W08WordIndex(new String[] {"a", "z", "zz"}).prefix("b")[1]
                - new W08WordIndex(new String[] {"a", "z", "zz"}).prefix("b")[0]
            == 0);
    ck(W08CatalogueReference.lower(new int[] {5, 10, 20, 30}, 17) == 2);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {100, 200, 300, 500}, 260) == 2);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {1, 2, 3, 4, 6, 7, 9, 10}, 7) == 5);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 10) == 0);
    ck(W08CatalogueReference.lower(new int[] {10, 10, 20}, 21) == 3);
    ck(W08CatalogueReference.lower(new int[] {}, 10) == 0);
    ck(W08CatalogueReference.count(new int[] {3, 8, 10, 11, 11, 14, 15, 21}, 10, 15) == 4);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(W08CatalogueReference.count(new int[] {2, 5, 10, 11, 12, 19, 20, 27}, 10, 20) == 4);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(W08CatalogueReference.count(new int[] {62, 68, 71, 75, 78, 81, 84, 90, 93}, 75, 90) == 4);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    ck(W08CatalogueReference.lower(new int[] {40, 95, 130, 180, 260}, 140) == 3);
    ck(W08CatalogueReference.lower(new int[] {40, 95}, 40) == 0);
    ck(W08CatalogueReference.lower(new int[] {40, 95}, 41) == 1);
    ck(W08CatalogueReference.lower(new int[] {40, 95}, 96) == 2);
    ck(W08CatalogueReference.count(new int[] {5, 18, 30, 31, 44, 59, 60, 75}, 30, 60) == 4);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 10, 30) == 3);
    ck(W08CatalogueReference.count(new int[] {10, 10, 20, 30, 30}, 20, 20) == 0);
    ck(W08CatalogueReference.count(new int[] {}, 10, 30) == 0);
    Random random = new Random(268801);
    for (int t = 0; t < 2200; t++) {
      int n = random.nextInt(100);
      long[] a = new long[n];
      for (int i = 0; i < n; i++) a[i] = random.nextInt(51) - 25;
      Arrays.sort(a);
      long[] before = a.clone();
      Week8ProjectReference index = new Week8ProjectReference(a);
      ck(index.buildComparisons == Math.max(0, n - 1));
      for (int q = 0; q < 20; q++) {
        long lo = random.nextInt(81) - 40, hi = lo + random.nextInt(30);
        int lower = 0, upper = 0, half = 0, closed = 0;
        for (long x : before) {
          if (x < lo) lower++;
          if (x <= lo) upper++;
          if (x >= lo && x < hi) half++;
          if (x >= lo && x <= hi) closed++;
        }
        ck(index.lower(lo) == lower);
        ck(index.comparisons <= limit(n));
        ck(index.upper(lo) == upper);
        ck(index.comparisons <= limit(n));
        ck(index.halfOpen(lo, hi) == half);
        ck(index.comparisons <= 2L * limit(n));
        ck(index.closed(lo, hi) == closed);
        ck(index.comparisons <= 2L * limit(n));
      }
      ck(Arrays.equals(a, before));
      if (n > 0) {
        a[0]++;
        ck(
            index.closed(before[0], before[0])
                == Arrays.stream(before).filter(x -> x == before[0]).count());
      }
      String[] words = new String[n];
      for (int i = 0; i < n; i++) {
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < random.nextInt(7); j++) s.append((char) ('a' + random.nextInt(26)));
        words[i] = s.toString();
      }
      Arrays.sort(words);
      String[] snapshot = words.clone();
      W08WordIndex wordIndex = new W08WordIndex(words);
      ck(wordIndex.buildComparisons == Math.max(0, n - 1));
      for (String prefix : new String[] {"", "a", "z", "zz", "az", "abc"}) {
        int l = 0, h = 0;
        for (String word : snapshot) {
          if (word.compareTo(prefix) < 0) l++;
          if (word.startsWith(prefix)) h++;
        }
        int[] bounds = wordIndex.prefix(prefix);
        ck(bounds[0] == l && bounds[1] == l + h);
        ck(wordIndex.comparisons <= 2L * limit(n));
      }
      int range = 0;
      for (String word : snapshot) if (word.compareTo("a") >= 0 && word.compareTo("m") < 0) range++;
      ck(wordIndex.count("a", "m") == range);
      if (n > 0) {
        words[0] = "zzz";
        ck(Arrays.equals(wordIndex.prefix(""), new int[] {0, n}));
      }
    }
    Week8ProjectReference x = new Week8ProjectReference(new long[] {10, 10, 20, 30, 30});
    ck(x.lower(10) == 0);
    ck(x.upper(10) == 2);
    ck(x.halfOpen(10, 30) == 3);
    ck(x.closed(10, 30) == 5);
    ck(x.halfOpen(20, 20) == 0);
    ck(x.halfOpen(11, 19) == 0);
    ck(x.lower(31) == 5);
    ck(new Week8ProjectReference(new long[] {}).closed(Long.MIN_VALUE, Long.MAX_VALUE) == 0);
    ck(
        new Week8ProjectReference(new long[] {Long.MIN_VALUE, Long.MAX_VALUE})
                .closed(Long.MIN_VALUE, Long.MAX_VALUE)
            == 2);
    ck(
        new Week8ProjectReference(new long[] {Long.MAX_VALUE})
                .closed(Long.MAX_VALUE, Long.MAX_VALUE)
            == 1);
    long[] max = new long[100000];
    Arrays.fill(max, Long.MAX_VALUE);
    Week8ProjectReference maxIndex = new Week8ProjectReference(max);
    ck(maxIndex.buildComparisons == 99999);
    ck(maxIndex.closed(Long.MAX_VALUE, Long.MAX_VALUE) == 100000);
    ck(maxIndex.comparisons <= 34);
    ck(maxIndex.halfOpen(Long.MIN_VALUE, Long.MAX_VALUE) == 0);
    rejects(() -> new Week8ProjectReference(null));
    rejects(() -> new Week8ProjectReference(new long[100001]));
    rejects(() -> new Week8ProjectReference(new long[] {20, 10}));
    rejects(() -> x.halfOpen(30, 10));
    rejects(() -> x.closed(30, 10));
    rejects(() -> new W08WordIndex(new String[] {"b", "a"}));
    rejects(() -> new W08WordIndex(new String[] {"A"}));
    rejects(() -> new W08WordIndex(null));
    rejects(() -> new W08WordIndex(new String[] {"a"}).prefix("A"));
    rejects(() -> new W08WordIndex(new String[] {"a"}).count("z", "a"));
    System.out.println(
        "PASS: "
            + checks
            + " project assertions;18 source samples +54 listed boundary cases;frozen numeric/word"
            + " indices,prefix successors,counters,limits and endpoint extremes.");
  }
}
