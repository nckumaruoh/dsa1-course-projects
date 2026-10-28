import java.util.*;

public class W12ProjectTests {
  static long checks;

  static void ck(boolean b) {
    checks++;
    if (!b) throw new AssertionError("project check " + checks);
  }

  static void invalid(Runnable r) {
    try {
      r.run();
      throw new AssertionError("accepted invalid");
    } catch (IllegalArgumentException expected) {
      checks++;
    }
  }

  static void overflow(Runnable r) {
    try {
      r.run();
      throw new AssertionError("accepted overflow");
    } catch (ArithmeticException expected) {
      checks++;
    }
  }

  static String queueOracle(int[] arrivals, int service, int counters) {
    ArrayDeque<Integer> q = new ArrayDeque<>();
    int[] free = new int[counters];
    int n = Arrays.stream(arrivals).sum(), done = 0;
    long total = 0, max = 0;
    for (int t = 0; done < n; t++) {
      if (t < arrivals.length) for (int j = 0; j < arrivals[t]; j++) q.add(t);
      for (int c = 0; c < counters; c++)
        if (free[c] <= t && !q.isEmpty()) {
          int arrival = q.remove();
          long wait = t - arrival;
          total += wait;
          max = Math.max(max, wait);
          free[c] = t + service;
          done++;
        }
    }
    return "served=" + done + ", total wait=" + total + " min, longest wait=" + max + " min";
  }

  static int[] waits(int[] a) {
    int[] o = new int[a.length];
    for (int i = 0; i < a.length; i++)
      for (int j = i + 1; j < a.length; j++)
        if (a[j] > a[i]) {
          o[i] = j - i;
          break;
        }
    return o;
  }

  static int[] windows(int[] a, int k) {
    int[] o = new int[a.length - k + 1];
    for (int i = 0; i < o.length; i++) {
      o[i] = Integer.MIN_VALUE;
      for (int j = i; j < i + k; j++) o[i] = Math.max(o[i], a[j]);
    }
    return o;
  }

  static int[] spans(int[] a) {
    int[] o = new int[a.length];
    for (int i = 0; i < a.length; i++) {
      o[i] = 1;
      while (i - o[i] >= 0 && a[i - o[i]] <= a[i]) o[i]++;
    }
    return o;
  }

  static long rect(int[] a) {
    long best = 0;
    for (int i = 0; i < a.length; i++) {
      int min = Integer.MAX_VALUE;
      for (int j = i; j < a.length; j++) {
        min = Math.min(min, a[j]);
        best = Math.max(best, (long) min * (j - i + 1));
      }
    }
    return best;
  }

  static boolean brackets(String text) {
    String s = text.replaceAll("[^()\\[\\]{}]", "");
    String before;
    do {
      before = s;
      s = s.replace("()", "").replace("[]", "").replace("{}", "");
    } while (!before.equals(s));
    return s.isEmpty();
  }

  record E(String text, long value) {}

  static E expr(Random r, int depth, boolean vars) {
    if (depth == 0 || r.nextInt(3) == 0) {
      int x = r.nextInt(25);
      return new E(vars && r.nextBoolean() ? "x" : "" + x, vars ? 0 : x);
    }
    E a = expr(r, depth - 1, false), b = expr(r, depth - 1, false);
    char op = "+-*/".charAt(r.nextInt(4));
    if (op == '/' && b.value == 0) op = '+';
    long value =
        switch (op) {
          case '+' -> a.value + b.value;
          case '-' -> a.value - b.value;
          case '*' -> a.value * b.value;
          default -> a.value / b.value;
        };
    return new E("(" + a.text + op + b.text + ")", value);
  }

  public static void main(String[] args) {
    Random random = new Random(2612021);
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 1, 0, 2, 1, 0, 0}, 3, 1)
            .equals("served=7, total wait=46 min, longest wait=13 min"));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 1, 0, 2, 1, 0, 0}, 3, 1)
            .equals(queueOracle(new int[] {1, 2, 1, 0, 2, 1, 0, 0}, 3, 1)));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 1, 0, 2, 1, 0, 0}, 3, 2)
            .equals(queueOracle(new int[] {1, 2, 1, 0, 2, 1, 0, 0}, 3, 2)));
    ck(
        Week12ProjectReference.queueSim(new int[] {}, 1, 1)
            .equals("served=0, total wait=0 min, longest wait=0 min"));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 0, 1));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 1, 0));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {820, 1100, 950, 700, 1300, 1250, 900}),
            new int[] {1, 3, 2, 1, 0, 0, 0}));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {5, 5, 5}), waits(new int[] {5, 5, 5})));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {1, 2, 3}), waits(new int[] {1, 2, 3})));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {3, 2, 1}), waits(new int[] {3, 2, 1})));
    ck(Arrays.equals(Week12ProjectReference.waitDays(new int[] {}), waits(new int[] {})));
    ck(
        Week12ProjectReference.queueSim(new int[] {2, 1, 0, 2, 1, 1, 0, 0}, 2, 1)
            .equals("served=7, total wait=26 min, longest wait=7 min"));
    ck(
        Week12ProjectReference.queueSim(new int[] {2, 1, 0, 2, 1, 1, 0, 0}, 2, 1)
            .equals(queueOracle(new int[] {2, 1, 0, 2, 1, 1, 0, 0}, 2, 1)));
    ck(
        Week12ProjectReference.queueSim(new int[] {2, 1, 0, 2, 1, 1, 0, 0}, 2, 2)
            .equals(queueOracle(new int[] {2, 1, 0, 2, 1, 1, 0, 0}, 2, 2)));
    ck(
        Week12ProjectReference.queueSim(new int[] {}, 1, 1)
            .equals("served=0, total wait=0 min, longest wait=0 min"));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 0, 1));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 1, 0));
    ck(Week12ProjectReference.validBrackets("[veg](spicy){menu}[x(y])") == false);
    ck(Week12ProjectReference.validBrackets("[veg](spicy)") == brackets("[veg](spicy)"));
    ck(Week12ProjectReference.validBrackets("([)]") == brackets("([)]"));
    ck(Week12ProjectReference.validBrackets("abc") == brackets("abc"));
    ck(Week12ProjectReference.validBrackets("((") == brackets("(("));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {72, 80, 95, 110, 104, 98, 120, 90}, 3),
            new int[] {95, 110, 110, 110, 120, 120}));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {5, 5, 5}, 2),
            windows(new int[] {5, 5, 5}, 2)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 1),
            windows(new int[] {1, 2, 3}, 1)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 3),
            windows(new int[] {1, 2, 3}, 3)));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 0));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 4));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 2, 1, 0, 1, 0}, 2, 1)
            .equals("served=7, total wait=28 min, longest wait=7 min"));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 2, 1, 0, 1, 0}, 2, 1)
            .equals(queueOracle(new int[] {1, 2, 2, 1, 0, 1, 0}, 2, 1)));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 2, 1, 0, 1, 0}, 2, 2)
            .equals(queueOracle(new int[] {1, 2, 2, 1, 0, 1, 0}, 2, 2)));
    ck(
        Week12ProjectReference.queueSim(new int[] {}, 1, 1)
            .equals("served=0, total wait=0 min, longest wait=0 min"));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 0, 1));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 1, 0));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {120, 145, 130, 110, 160, 150, 170}),
            new int[] {1, 3, 2, 1, 2, 1, 0}));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {5, 5, 5}), waits(new int[] {5, 5, 5})));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {1, 2, 3}), waits(new int[] {1, 2, 3})));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {3, 2, 1}), waits(new int[] {3, 2, 1})));
    ck(Arrays.equals(Week12ProjectReference.waitDays(new int[] {}), waits(new int[] {})));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {10, 12, 15, 13, 18, 11, 10}, 3),
            new int[] {15, 15, 18, 18, 18}));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {5, 5, 5}, 2),
            windows(new int[] {5, 5, 5}, 2)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 1),
            windows(new int[] {1, 2, 3}, 1)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 3),
            windows(new int[] {1, 2, 3}, 3)));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 0));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 4));
    ck(
        Week12ProjectReference.queueSim(new int[] {2, 1, 1, 0, 2, 1, 0, 0}, 4, 2)
            .equals("served=7, total wait=20 min, longest wait=7 min"));
    ck(
        Week12ProjectReference.queueSim(new int[] {2, 1, 1, 0, 2, 1, 0, 0}, 4, 1)
            .equals(queueOracle(new int[] {2, 1, 1, 0, 2, 1, 0, 0}, 4, 1)));
    ck(
        Week12ProjectReference.queueSim(new int[] {2, 1, 1, 0, 2, 1, 0, 0}, 4, 2)
            .equals(queueOracle(new int[] {2, 1, 1, 0, 2, 1, 0, 0}, 4, 2)));
    ck(
        Week12ProjectReference.queueSim(new int[] {}, 1, 1)
            .equals("served=0, total wait=0 min, longest wait=0 min"));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 0, 1));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 1, 0));
    ck(
        Arrays.equals(
            Week12ProjectReference.span(new int[] {100, 80, 60, 70, 60, 75, 85}),
            new int[] {1, 1, 1, 2, 1, 4, 6}));
    ck(Arrays.equals(Week12ProjectReference.span(new int[] {5, 5, 5}), spans(new int[] {5, 5, 5})));
    ck(Arrays.equals(Week12ProjectReference.span(new int[] {3, 2, 1}), spans(new int[] {3, 2, 1})));
    ck(Arrays.equals(Week12ProjectReference.span(new int[] {}), spans(new int[] {})));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {12, 15, 9, 22, 18, 30, 14}, 3),
            new int[] {15, 22, 22, 30, 30}));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {5, 5, 5}, 2),
            windows(new int[] {5, 5, 5}, 2)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 1),
            windows(new int[] {1, 2, 3}, 1)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 3),
            windows(new int[] {1, 2, 3}, 3)));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 0));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 4));
    ck(Week12ProjectReference.evaluate("3+4*(2-1)*5") == 23);
    ck(Week12ProjectReference.evaluate("8/3*3") == 6);
    ck(Week12ProjectReference.evaluate("2*(3+(4*5))") == 46);
    invalid(() -> Week12ProjectReference.evaluate("1/0"));
    invalid(() -> Week12ProjectReference.evaluate("1++2"));
    invalid(() -> Week12ProjectReference.evaluate("(1+2"));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {1800, 1650, 1700, 2100, 1900, 2400, 2250}),
            new int[] {3, 1, 1, 2, 1, 0, 0}));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {5, 5, 5}), waits(new int[] {5, 5, 5})));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {1, 2, 3}), waits(new int[] {1, 2, 3})));
    ck(
        Arrays.equals(
            Week12ProjectReference.waitDays(new int[] {3, 2, 1}), waits(new int[] {3, 2, 1})));
    ck(Arrays.equals(Week12ProjectReference.waitDays(new int[] {}), waits(new int[] {})));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 0, 1, 2, 0}, 3, 2)
            .equals("served=6, total wait=8 min, longest wait=3 min"));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 0, 1, 2, 0}, 3, 1)
            .equals(queueOracle(new int[] {1, 2, 0, 1, 2, 0}, 3, 1)));
    ck(
        Week12ProjectReference.queueSim(new int[] {1, 2, 0, 1, 2, 0}, 3, 2)
            .equals(queueOracle(new int[] {1, 2, 0, 1, 2, 0}, 3, 2)));
    ck(
        Week12ProjectReference.queueSim(new int[] {}, 1, 1)
            .equals("served=0, total wait=0 min, longest wait=0 min"));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 0, 1));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 1, 0));
    ck(Week12ProjectReference.validBrackets("*(bold [link])* {quote}") == true);
    ck(Week12ProjectReference.validBrackets("[veg](spicy)") == brackets("[veg](spicy)"));
    ck(Week12ProjectReference.validBrackets("([)]") == brackets("([)]"));
    ck(Week12ProjectReference.validBrackets("abc") == brackets("abc"));
    ck(Week12ProjectReference.validBrackets("((") == brackets("(("));
    ck(Week12ProjectReference.validBrackets("if (a[i] > b) { x = (y]; }") == false);
    ck(Week12ProjectReference.validBrackets("[veg](spicy)") == brackets("[veg](spicy)"));
    ck(Week12ProjectReference.validBrackets("([)]") == brackets("([)]"));
    ck(Week12ProjectReference.validBrackets("abc") == brackets("abc"));
    ck(Week12ProjectReference.validBrackets("((") == brackets("(("));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {500, 1200, 300, 9000, 400, 350, 8000}, 3),
            new int[] {1200, 9000, 9000, 9000, 8000}));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {5, 5, 5}, 2),
            windows(new int[] {5, 5, 5}, 2)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 1),
            windows(new int[] {1, 2, 3}, 1)));
    ck(
        Arrays.equals(
            Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 3),
            windows(new int[] {1, 2, 3}, 3)));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 0));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {1, 2, 3}, 4));
    ck(Week12ProjectReference.maxRectangle(new int[] {2, 1, 5, 6, 2, 3}) == 10);
    ck(Week12ProjectReference.maxRectangle(new int[] {2, 2}) == rect(new int[] {2, 2}));
    ck(Week12ProjectReference.maxRectangle(new int[] {0, 0}) == rect(new int[] {0, 0}));
    ck(Week12ProjectReference.maxRectangle(new int[] {1, 2, 3}) == rect(new int[] {1, 2, 3}));
    ck(Week12ProjectReference.maxRectangle(new int[] {}) == rect(new int[] {}));
    for (int t = 0; t < 2500; t++) {
      int n = random.nextInt(26);
      int[] a = new int[n];
      for (int i = 0; i < n; i++) a[i] = random.nextInt(31) - 15;
      int[] before = a.clone();
      ck(Arrays.equals(Week12ProjectReference.waitDays(a), waits(a)));
      ck(Arrays.equals(Week12ProjectReference.span(a), spans(a)));
      if (n > 0) {
        int k = 1 + random.nextInt(n);
        ck(Arrays.equals(Week12ProjectReference.slidingMax(a, k), windows(a, k)));
      }
      ck(Arrays.equals(a, before));
      int[] hist = Arrays.stream(a).map(Math::abs).toArray();
      ck(Week12ProjectReference.maxRectangle(hist) == rect(hist));
      int[] arrival = new int[random.nextInt(14)];
      for (int i = 0; i < arrival.length; i++) arrival[i] = random.nextInt(4);
      int[] snapshot = arrival.clone();
      int service = 1 + random.nextInt(5), counter = 1 + random.nextInt(2);
      ck(
          Week12ProjectReference.queueSim(arrival, service, counter)
              .equals(queueOracle(arrival, service, counter)));
      ck(Arrays.equals(arrival, snapshot));
      E expression = expr(random, 4, false);
      ck(Week12ProjectReference.evaluate(expression.text) == expression.value);
      Map<String, Integer> vars =
          Map.of("x", random.nextInt(200) - 100, "p95", random.nextInt(1000));
      ck(
          Week12ProjectReference.evaluate("(x+p95)*2-3", vars)
              == ((long) vars.get("x") + vars.get("p95")) * 2 - 3);
      StringBuilder text = new StringBuilder();
      for (int i = 0; i < n; i++) text.append("()[]{}abc ".charAt(random.nextInt(10)));
      ck(Week12ProjectReference.validBrackets(text.toString()) == brackets(text.toString()));
      int len = 1 + random.nextInt(30), cap = 1 + random.nextInt(5), k = 1 + random.nextInt(len);
      int[] keys = new int[len], latency = new int[len];
      List<Integer> order = new ArrayList<>();
      int hits = 0;
      for (int i = 0; i < len; i++) {
        keys[i] = random.nextInt(10);
        latency[i] = random.nextInt(100);
        if (order.remove((Integer) keys[i])) hits++;
        else if (order.size() == cap) order.remove(order.size() - 1);
        order.add(0, keys[i]);
      }
      String expected =
          "hits="
              + hits
              + "/"
              + len
              + "; MRU="
              + order
              + "; windows="
              + Arrays.toString(windows(latency, k))
              + "; waits="
              + Arrays.toString(waits(latency))
              + "; alert=true";
      String old = Week12ProjectReference.pipeline(keys, latency, cap, k, "3+4*(2-1)*5", 20),
          fast =
              Week12ProjectReference.pipeline(
                  keys, latency, cap, k, "p95-base*2", Map.of("p95", 480, "base", 200), 80, false);
      ck(old.equals(expected));
      ck(fast.equals(expected));
    }
    int[] p95 = Week12ProjectReference.slmLatencies();
    ck(Arrays.equals(Week12ProjectReference.slidingMax(p95, 5), windows(p95, 5)));
    ck(Arrays.equals(Week12ProjectReference.waitDays(p95), waits(p95)));
    long[] scores = {366, 80, 24, 210};
    String[] rules = Week12ProjectReference.slmRules();
    for (int i = 0; i < rules.length; i++)
      ck(
          Week12ProjectReference.evaluate(rules[i], Week12ProjectReference.slmVariables())
              == scores[i]);
    for (String bad :
        new String[] {
          "", " ", "1/0", "1++2", "(1+2", "1+2)", "()", "2(3)", "1 2", "-1", "1+", "*2", "2**3",
          "x", "1%2", "p95", "1_2"
        }) invalid(() -> Week12ProjectReference.evaluate(bad));
    invalid(() -> Week12ProjectReference.evaluate(null));
    invalid(() -> Week12ProjectReference.evaluate("1".repeat(100001)));
    invalid(() -> Week12ProjectReference.evaluate("p95", Map.of()));
    invalid(() -> Week12ProjectReference.evaluate("p95", null));
    for (String big :
        new String[] {
          "9223372036854775808",
          "9223372036854775807+1",
          "3037000500*3037000500",
          "(0-9223372036854775807-1)/(0-1)"
        }) overflow(() -> Week12ProjectReference.evaluate(big));
    ck(Week12ProjectReference.evaluate("(0-7)/2") == -3);
    ck(Week12ProjectReference.evaluate("(".repeat(40000) + "1" + ")".repeat(40000)) == 1);
    invalid(() -> Week12ProjectReference.waitDays(null));
    invalid(() -> Week12ProjectReference.waitDays(new int[100001]));
    invalid(() -> Week12ProjectReference.span(new int[100001]));
    invalid(() -> Week12ProjectReference.validBrackets(null));
    invalid(() -> Week12ProjectReference.maxRectangle(new int[] {-1}));
    invalid(() -> Week12ProjectReference.maxRectangle(new int[] {1000000001}));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {100001}, 1, 1));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {1}, 100001, 1));
    invalid(() -> Week12ProjectReference.queueSim(new int[] {-1}, 1, 1));
    invalid(() -> Week12ProjectReference.queueSim(null, 1, 1));
    invalid(() -> Week12ProjectReference.slidingMax(new int[] {}, 1));
    invalid(() -> Week12ProjectReference.pipeline(null, new int[] {1}, 1, 1, "1", 0));
    invalid(() -> Week12ProjectReference.pipeline(new int[] {1}, new int[] {}, 1, 1, "1", 0));
    invalid(() -> Week12ProjectReference.pipeline(new int[] {-1}, new int[] {1}, 1, 1, "1", 0));
    invalid(() -> Week12ProjectReference.pipeline(new int[] {10001}, new int[] {1}, 1, 1, "1", 0));
    invalid(() -> Week12ProjectReference.pipeline(new int[] {1}, new int[] {-1}, 1, 1, "1", 0));
    invalid(
        () -> Week12ProjectReference.pipeline(new int[] {1}, new int[] {1000000001}, 1, 1, "1", 0));
    invalid(() -> Week12ProjectReference.pipeline(new int[] {1}, new int[] {1}, 3001, 1, "1", 0));
    int[] large = new int[100000];
    Arrays.fill(large, 1000000000);
    ck(Week12ProjectReference.maxRectangle(large) == 100000000000000L);
    ck(Week12ProjectReference.slidingMax(large, 100000)[0] == 1000000000);
    ck(
        Week12ProjectReference.queueSim(new int[] {100000}, 100000, 1)
            .equals("served=100000, total wait=499995000000000 min, longest wait=9999900000 min"));
    // Checked Week11 cache regression retained by the integrated pipeline.
    for (int cap = 1; cap <= 4; cap++) {
      var c = new Week12ProjectReference.LRUCache(cap);
      List<Integer> o = new ArrayList<>();
      Map<Integer, Integer> v = new HashMap<>();
      for (int t = 0; t < 5000; t++) {
        int key = random.nextInt(12);
        if (random.nextBoolean()) {
          ck(c.get(key) == v.getOrDefault(key, -1));
          if (v.containsKey(key)) {
            o.remove((Integer) key);
            o.add(0, key);
          }
        } else {
          int value = random.nextInt(100001);
          c.put(key, value);
          o.remove((Integer) key);
          o.add(0, key);
          v.put(key, value);
          if (o.size() > cap) v.remove(o.remove(o.size() - 1));
        }
        c.integrity();
        ck(Arrays.equals(c.order(), o.stream().mapToInt(x -> x).toArray()));
        ck(c.getCalls == c.getHits + c.getMisses);
      }
    }
    System.out.println(
        "PASS: "
            + checks
            + " project assertions;18 catalogue samples,81 repeated listed cases,7"
            + " families,variable rules,queue/time and integration oracles.");
  }
}
