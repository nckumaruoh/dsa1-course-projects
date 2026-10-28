import java.util.*;

public class W05ProjectTests {
  static long checks;

  static void ck(boolean b) {
    checks++;
    if (!b) throw new AssertionError("project check " + checks);
  }

  static void rejects(Runnable r, Class<?> type) {
    checks++;
    try {
      r.run();
      throw new AssertionError("expected " + type);
    } catch (RuntimeException e) {
      if (!type.isInstance(e)) throw e;
    }
  }

  static long sum(long[] a, int l, int r) {
    long s = 0;
    for (int i = l; i <= r; i++) s += a[i];
    return s;
  }

  public static void main(String[] args) {
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {450L, 1200L, 2300L, 3100L, 2800L, 900L, 600L},
                new int[][] {new int[] {2, 4}, new int[] {0, 6}}),
            new long[] {8200L, 11350L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(
                6, new int[][] {new int[] {0, 3, 120}, new int[] {2, 5, 80}, new int[] {1, 2, 60}}),
            new long[] {120L, 180L, 260L, 200L, 80L, 80L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(W05CatalogueReference.bestSum(new long[] {-2L, 1L, -4L, 4L, -1L, 7L, -5L, 2L}) == 10L);
    ck(
        W05CatalogueReference.bestInterval(new long[] {-2L, 1L, -4L, 4L, -1L, 7L, -5L, 2L})
            .equals(new W05CatalogueReference.Interval(10L, 3, 5)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {-2, -1})
            .equals(new W05CatalogueReference.Interval(-1, 1, 1)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {4})
            .equals(new W05CatalogueReference.Interval(4, 0, 0)));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(
                8,
                new int[][] {
                  new int[] {0, 3, 1}, new int[] {2, 6, 1}, new int[] {5, 7, 1}, new int[] {1, 1, 1}
                }),
            new long[] {1L, 2L, 2L, 2L, 1L, 2L, 2L, 1L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(
        W05CatalogueReference.bestSum(new long[] {-800L, 1450L, -2700L, 3100L, 800L, 6020L, -4700L})
            == 9920L);
    ck(
        W05CatalogueReference.bestInterval(
                new long[] {-800L, 1450L, -2700L, 3100L, 800L, 6020L, -4700L})
            .equals(new W05CatalogueReference.Interval(9920L, 3, 5)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {-2, -1})
            .equals(new W05CatalogueReference.Interval(-1, 1, 1)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {4})
            .equals(new W05CatalogueReference.Interval(4, 0, 0)));
    ck(
        W05CatalogueReference.rectangle(
                new long[][] {
                  new long[] {1L, 0L, 1L, 1L},
                  new long[] {0L, 1L, 1L, 0L},
                  new long[] {1L, 1L, 0L, 1L}
                },
                0,
                1,
                1,
                2)
            == 3L);
    ck(W05CatalogueReference.rectangle(new long[][] {{1, 2}, {3, 4}}, 0, 1, 1, 1) == 6);
    ck(W05CatalogueReference.rectangle(new long[][] {{5}}, 0, 0, 0, 0) == 5);
    ck(
        W05CatalogueReference.rectangle(
                new long[][] {
                  new long[] {120L, 145L, 180L, 90L},
                  new long[] {210L, 260L, 230L, 110L},
                  new long[] {190L, 150L, 140L, 95L}
                },
                0,
                1,
                1,
                2)
            == 815L);
    ck(W05CatalogueReference.rectangle(new long[][] {{1, 2}, {3, 4}}, 0, 1, 1, 1) == 6);
    ck(W05CatalogueReference.rectangle(new long[][] {{5}}, 0, 0, 0, 0) == 5);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {120L, 340L, 560L, 410L, 220L, 90L},
                new int[][] {new int[] {1, 3}, new int[] {0, 5}}),
            new long[] {1310L, 1740L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {72L, 85L, 64L, 91L, 58L, 77L},
                new int[][] {new int[] {0, 2}, new int[] {3, 5}}),
            new long[] {221L, 226L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {210L, 185L, 240L, 200L, 260L, 195L},
                new int[][] {new int[] {1, 3}, new int[] {0, 5}}),
            new long[] {625L, 1290L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(W05CatalogueReference.bestSum(new long[] {-3L, 4L, 6L, -2L, 5L, -8L, 3L, 2L}) == 13L);
    ck(
        W05CatalogueReference.bestInterval(new long[] {-3L, 4L, 6L, -2L, 5L, -8L, 3L, 2L})
            .equals(new W05CatalogueReference.Interval(13L, 1, 4)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {-2, -1})
            .equals(new W05CatalogueReference.Interval(-1, 1, 1)));
    ck(
        W05CatalogueReference.bestInterval(new long[] {4})
            .equals(new W05CatalogueReference.Interval(4, 0, 0)));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(
                6, new int[][] {new int[] {0, 2, 12}, new int[] {1, 4, 8}, new int[] {3, 5, 15}}),
            new long[] {12L, 20L, 20L, 23L, 23L, 15L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {30L, 45L, 20L, 38L, 52L, 26L},
                new int[][] {new int[] {0, 2}, new int[] {3, 5}}),
            new long[] {95L, 116L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        W05CatalogueReference.profit(
                new long[] {2841L, 2856L, 2830L, 2812L, 2847L, 2869L, 2858L, 2875L})
            == 63L);
    ck(
        W05CatalogueReference.bestTrade(
                new long[] {2841L, 2856L, 2830L, 2812L, 2847L, 2869L, 2858L, 2875L})
            .equals(new W05CatalogueReference.Interval(63L, 3, 7)));
    ck(
        W05CatalogueReference.bestTrade(new long[] {3, 1, 4})
            .equals(new W05CatalogueReference.Interval(3, 1, 2)));
    ck(
        W05CatalogueReference.bestTrade(new long[] {3, 2, 1})
            .equals(new W05CatalogueReference.Interval(0, -1, -1)));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {540L, 820L, 1310L, 2200L, 1900L, 760L},
                new int[][] {new int[] {2, 4}, new int[] {0, 5}}),
            new long[] {5410L, 7530L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(
                7,
                new int[][] {
                  new int[] {0, 6, 3}, new int[] {1, 3, 2}, new int[] {2, 5, 1}, new int[] {5, 6, 1}
                }),
            new long[] {3L, 5L, 6L, 6L, 4L, 5L, 4L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(W05CatalogueReference.count(new long[] {4L, 5L, 0L, 3L, 2L, 1L}, 5) == 7L);
    ck(W05CatalogueReference.count(new long[] {1, 2, 3}, 3) == 3);
    ck(W05CatalogueReference.count(new long[] {1}, 2) == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {14L, 22L, 35L, 41L, 28L, 19L},
                new int[][] {new int[] {0, 2}, new int[] {2, 5}}),
            new long[] {71L, 123L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        W05CatalogueReference.profit(new long[] {1800L, 1650L, 1700L, 2100L, 1900L, 2400L, 2250L})
            == 750L);
    ck(
        W05CatalogueReference.bestTrade(
                new long[] {1800L, 1650L, 1700L, 2100L, 1900L, 2400L, 2250L})
            .equals(new W05CatalogueReference.Interval(750L, 1, 5)));
    ck(
        W05CatalogueReference.bestTrade(new long[] {3, 1, 4})
            .equals(new W05CatalogueReference.Interval(3, 1, 2)));
    ck(
        W05CatalogueReference.bestTrade(new long[] {3, 2, 1})
            .equals(new W05CatalogueReference.Interval(0, -1, -1)));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(
                7,
                new int[][] {
                  new int[] {0, 4, 1}, new int[] {1, 2, 1}, new int[] {2, 6, 1}, new int[] {3, 5, 1}
                }),
            new long[] {1L, 2L, 3L, 3L, 3L, 2L, 1L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {140L, 220L, 310L, 280L, 190L, 120L},
                new int[][] {new int[] {1, 3}, new int[] {0, 5}}),
            new long[] {810L, 1260L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(
                8,
                new int[][] {
                  new int[] {0, 2, 1}, new int[] {1, 3, 1}, new int[] {2, 5, 1}, new int[] {4, 7, 1}
                }),
            new long[] {1L, 2L, 3L, 2L, 2L, 2L, 1L, 1L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.apply(3, new int[][] {{0, 1, 2}, {1, 2, -1}}),
            new long[] {2, 1, -1}));
    ck(W05CatalogueReference.apply(0, new int[][] {}).length == 0);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {120L, 85L, 240L, 60L, 175L},
                new int[][] {new int[] {1, 3}, new int[] {0, 4}}),
            new long[] {385L, 680L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    ck(
        W05CatalogueReference.count(
                new long[] {2000L, 3000L, 5000L, 4000L, 1000L, 5000L, 5000L}, 10000L)
            == 4L);
    ck(W05CatalogueReference.count(new long[] {1, 1, 1}, 2L) == 2);
    ck(W05CatalogueReference.count(new long[] {0, 0}, 0L) == 3);
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(
                new long[] {0L, 12L, 35L, 60L, 8L, 0L, 22L, 41L},
                new int[][] {new int[] {1, 4}, new int[] {0, 7}}),
            new long[] {115L, 178L}));
    ck(
        Arrays.equals(
            W05CatalogueReference.rangeSums(new long[] {2, -1, 3}, new int[][] {{0, 0}, {1, 2}}),
            new long[] {2, 2}));
    ck(W05CatalogueReference.rangeSums(new long[] {}, new int[][] {}).length == 0);
    Random random = new Random(265501);
    for (int t = 0; t < 1000; t++) {
      int n = 1 + random.nextInt(16);
      long[] a = new long[n];
      for (int i = 0; i < n; i++) a[i] = random.nextInt(41) - 20;
      long[] before = a.clone();
      PulseLogWeek5 frozen = new PulseLogWeek5(a);
      long best = Long.MIN_VALUE, profit = 0, exact = 0, div = 0;
      int k = 1 + random.nextInt(8);
      long target = random.nextInt(21) - 10;
      for (int l = 0; l <= n; l++)
        for (int r = l; r <= n; r++) ck(frozen.query(l, r) == sum(a, l, r - 1));
      for (int l = 0; l < n; l++)
        for (int r = l; r < n; r++) {
          long s = sum(a, l, r);
          best = Math.max(best, s);
          if (s == target) exact++;
          if (s % k == 0) div++;
          if (l < r) profit = Math.max(profit, a[r] - a[l]);
        }
      var interval = W05CatalogueReference.bestInterval(a);
      ck(interval.value() == best);
      ck(interval.left() >= 0 && interval.left() <= interval.right() && interval.right() < n);
      ck(sum(a, interval.left(), interval.right()) == best);
      var trade = W05CatalogueReference.bestTrade(a);
      ck(trade.value() == profit);
      ck(
          profit == 0
              ? trade.left() == -1 && trade.right() == -1
              : trade.left() >= 0
                  && trade.left() < trade.right()
                  && trade.right() < n
                  && a[trade.right()] - a[trade.left()] == profit);
      ck(W05CatalogueReference.count(a, target) == exact);
      ck(W05CatalogueReference.count(a, k) == div);
      int[][] q = new int[n][2];
      for (int i = 0; i < n; i++) q[i] = new int[] {i, n - 1};
      long[] ranges = W05CatalogueReference.rangeSums(a, q);
      for (int i = 0; i < n; i++) ck(ranges[i] == sum(a, i, n - 1));
      ck(Arrays.equals(a, before));
      a[0]++;
      ck(frozen.query(0, 1) == before[0]);
      int[][] u = new int[12][3];
      long[] direct = new long[n];
      for (int i = 0; i < u.length; i++) {
        int l = random.nextInt(n), r = l + random.nextInt(n - l), v = random.nextInt(21) - 10;
        u[i] = new int[] {l, r, v};
        for (int j = l; j <= r; j++) direct[j] += v;
      }
      ck(Arrays.equals(W05CatalogueReference.apply(n, u), direct));
      int rows = 1 + random.nextInt(5), cols = 1 + random.nextInt(5);
      long[][] grid = new long[rows][cols];
      for (long[] row : grid) for (int c = 0; c < cols; c++) row[c] = random.nextInt(41) - 20;
      var index = new W05CatalogueReference.GridIndex(grid);
      for (int r1 = 0; r1 < rows; r1++)
        for (int r2 = r1; r2 < rows; r2++)
          for (int c1 = 0; c1 < cols; c1++)
            for (int c2 = c1; c2 < cols; c2++) {
              long s = 0;
              for (int r = r1; r <= r2; r++) for (int c = c1; c <= c2; c++) s += grid[r][c];
              ck(index.query(r1, c1, r2, c2) == s);
            }
      long first = grid[0][0];
      grid[0][0]++;
      ck(index.query(0, 0, 0, 0) == first);
    }
    ck(new PulseLogWeek5(new long[] {}).query(0, 0) == 0);
    ck(new PulseLogWeek5(new long[100000]).query(0, 100000) == 0);
    rejects(() -> new PulseLogWeek5(null), IllegalArgumentException.class);
    rejects(() -> new PulseLogWeek5(new long[100001]), IllegalArgumentException.class);
    rejects(() -> new PulseLogWeek5(new long[] {Long.MAX_VALUE, 1}), ArithmeticException.class);
    rejects(
        () -> new PulseLogWeek5(new long[] {Long.MIN_VALUE, Long.MAX_VALUE, 1}).query(1, 3),
        ArithmeticException.class);
    for (int[] bounds : new int[][] {{-1, 0}, {0, 2}, {1, 0}})
      rejects(
          () -> new PulseLogWeek5(new long[] {1}).query(bounds[0], bounds[1]),
          IndexOutOfBoundsException.class);
    rejects(
        () -> W05CatalogueReference.rangeSums(new long[] {1}, new int[][] {{0, 1}}),
        IllegalArgumentException.class);
    rejects(
        () -> W05CatalogueReference.apply(0, new int[][] {{0, 0, 1}}),
        IllegalArgumentException.class);
    rejects(
        () -> W05CatalogueReference.bestInterval(new long[] {}), IllegalArgumentException.class);
    rejects(
        () -> new W05CatalogueReference.GridIndex(new long[][] {{1}, {2, 3}}),
        IllegalArgumentException.class);
    rejects(() -> W05CatalogueReference.count(new long[] {1}, 0), IllegalArgumentException.class);
    ck(W05CatalogueReference.count(new long[1000], 0L) == 500500);
    ck(W05CatalogueReference.count(new long[1000], 1) == 500500);
    ck(W05CatalogueReference.bestTrade(new long[] {}).value() == 0);
    System.out.println(
        "PASS: "
            + checks
            + " project assertions; 25 catalogue samples +50 listed additional cases; frozen"
            + " queries,7 domain families,witnesses,bounds and overflow.");
  }
}
