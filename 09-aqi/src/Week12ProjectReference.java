import java.util.*;

public class Week12ProjectReference {
  static class MyCircularQueue {
    final int[] a;
    int head, size;

    MyCircularQueue(int k) {
      if (k < 1 || k > 100000) throw new IllegalArgumentException("capacity1..100000");
      a = new int[k];
    }

    boolean enQueue(int v) {
      if (isFull()) return false;
      a[(head + size) % a.length] = v;
      size++;
      return true;
    }

    boolean deQueue() {
      if (isEmpty()) return false;
      head = (head + 1) % a.length;
      size--;
      return true;
    }

    int Front() {
      return isEmpty() ? -1 : a[head];
    }

    int Rear() {
      return isEmpty() ? -1 : a[(head + size - 1) % a.length];
    }

    boolean isEmpty() {
      return size == 0;
    }

    boolean isFull() {
      return size == a.length;
    }
  }

  static class LRUCache {
    static class Entry {
      int key, value;
      Entry prev, next;

      Entry(int k, int v) {
        key = k;
        value = v;
      }
    }

    final int capacity;
    final Map<Integer, Entry> map = new HashMap<>();
    final Entry head = new Entry(0, 0), tail = new Entry(0, 0);
    long getCalls, putCalls, getHits, getMisses, evictions, dataNodeAllocations, pointerWrites;

    LRUCache(int c) {
      if (c < 1 || c > 3000) throw new IllegalArgumentException("capacity1..3000");
      capacity = c;
      head.next = tail;
      tail.prev = head;
    }

    static void key(int k) {
      if (k < 0 || k > 10000) throw new IllegalArgumentException("key0..10000");
    }

    void unlink(Entry e) {
      e.prev.next = e.next;
      e.next.prev = e.prev;
      pointerWrites += 2;
    }

    void front(Entry e) {
      e.next = head.next;
      e.prev = head;
      head.next.prev = e;
      head.next = e;
      pointerWrites += 4;
    }

    int get(int key) {
      key(key);
      getCalls++;
      Entry e = map.get(key);
      if (e == null) {
        getMisses++;
        return -1;
      }
      getHits++;
      unlink(e);
      front(e);
      return e.value;
    }

    void put(int key, int value) {
      key(key);
      if (value < 0 || value > 100000) throw new IllegalArgumentException("value0..100000");
      putCalls++;
      Entry e = map.get(key);
      if (e != null) {
        e.value = value;
        unlink(e);
        front(e);
        return;
      }
      e = new Entry(key, value);
      dataNodeAllocations++;
      map.put(key, e);
      front(e);
      if (map.size() > capacity) {
        Entry old = tail.prev;
        unlink(old);
        map.remove(old.key);
        evictions++;
      }
    }

    int[] order() {
      List<Integer> a = new ArrayList<>();
      Set<Entry> seen = Collections.newSetFromMap(new IdentityHashMap<>());
      for (Entry e = head.next; e != tail; e = e.next) {
        if (e == null || !seen.add(e)) throw new AssertionError("broken/cyclic debug traversal");
        a.add(e.key);
      }
      return a.stream().mapToInt(x -> x).toArray();
    }

    void integrity() {
      if (head.prev != null || tail.next != null) throw new AssertionError("sentinel links");
      Set<Entry> seen = Collections.newSetFromMap(new IdentityHashMap<>());
      Entry previous = head;
      for (Entry e = head.next; e != tail; e = e.next) {
        if (e == null || !seen.add(e) || e.prev != previous || map.get(e.key) != e)
          throw new AssertionError("forward/map");
        previous = e;
      }
      if (tail.prev != previous || map.size() != seen.size() || seen.size() > capacity)
        throw new AssertionError("size/tail");
      int count = 0;
      Entry after = tail;
      for (Entry e = tail.prev; e != head; e = e.prev) {
        if (e == null || e.next != after || !seen.remove(e)) throw new AssertionError("backward");
        after = e;
        count++;
      }
      if (head.next != after || count != map.size() || !seen.isEmpty())
        throw new AssertionError("backward size/head");
    }
  }

  static String queueSim(int[] arrivals, int service, int counters) {
    validArray(arrivals);
    if (service < 1 || service > 100000 || counters < 1 || counters > 2)
      throw new IllegalArgumentException("service/counters");
    int n = 0;
    for (int x : arrivals) {
      if (x < 0) throw new IllegalArgumentException("arrivals");
      if (x > 100000 - n) throw new IllegalArgumentException("total requests<=100000");
      n += x;
    }
    MyCircularQueue q = new MyCircularQueue(Math.max(1, n));
    for (int t = 0; t < arrivals.length; t++) for (int j = 0; j < arrivals[t]; j++) q.enQueue(t);
    long[] free = new long[counters];
    long total = 0, max = 0;
    while (!q.isEmpty()) {
      int arrival = q.Front();
      q.deQueue();
      int best = 0;
      for (int c = 1; c < counters; c++) if (free[c] < free[best]) best = c;
      long start = Math.max(arrival, free[best]), wait = start - arrival;
      free[best] = start + service;
      total += wait;
      max = Math.max(max, wait);
    }
    return "served=" + n + ", total wait=" + total + " min, longest wait=" + max + " min";
  }

  static int[] waitDays(int[] a) {
    validArray(a);
    int[] out = new int[a.length];
    Deque<Integer> s = new ArrayDeque<>();
    for (int i = 0; i < a.length; i++) {
      while (!s.isEmpty() && a[s.peek()] < a[i]) {
        int j = s.pop();
        out[j] = i - j;
      }
      s.push(i);
    }
    return out;
  }

  static int[] slidingMax(int[] a, int k) {
    validArray(a);
    if (k < 1 || k > a.length) throw new IllegalArgumentException("window");
    int[] out = new int[a.length - k + 1];
    Deque<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < a.length; i++) {
      while (!q.isEmpty() && q.peekFirst() <= i - k) q.removeFirst();
      while (!q.isEmpty() && a[q.peekLast()] <= a[i]) q.removeLast();
      q.addLast(i);
      if (i >= k - 1) out[i - k + 1] = a[q.peekFirst()];
    }
    return out;
  }

  static int[] span(int[] a) {
    validArray(a);
    int[] out = new int[a.length];
    Deque<int[]> s = new ArrayDeque<>();
    for (int i = 0; i < a.length; i++) {
      int count = 1;
      while (!s.isEmpty() && s.peek()[0] <= a[i]) count += s.pop()[1];
      s.push(new int[] {a[i], count});
      out[i] = count;
    }
    return out;
  }

  static boolean validBrackets(String text) {
    validText(text);
    Deque<Character> s = new ArrayDeque<>();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '(' || c == '[' || c == '{') s.push(c);
      else if (c == ')' || c == ']' || c == '}') {
        if (s.isEmpty()) return false;
        char open = s.pop();
        if ((c == ')' && open != '(') || (c == ']' && open != '[') || (c == '}' && open != '{'))
          return false;
      }
    }
    return s.isEmpty();
  }

  static long maxRectangle(int[] a) {
    validArray(a);
    for (int x : a) if (x < 0 || x > 1000000000) throw new IllegalArgumentException("height0..1e9");
    long best = 0;
    Deque<Integer> s = new ArrayDeque<>();
    for (int i = 0; i <= a.length; i++) {
      while (!s.isEmpty() && (i == a.length || a[s.peek()] >= a[i])) {
        int j = s.pop(), left = s.isEmpty() ? -1 : s.peek();
        best = Math.max(best, (long) a[j] * (i - left - 1));
      }
      if (i < a.length) s.push(i);
    }
    return best;
  }

  static int precedence(char c) {
    return c == '*' || c == '/' ? 2 : 1;
  }

  static void apply(Deque<Long> values, Deque<Character> ops) {
    long b = values.pop(), a = values.pop();
    char op = ops.pop();
    long r;
    if (op == '+') r = Math.addExact(a, b);
    else if (op == '-') r = Math.subtractExact(a, b);
    else if (op == '*') r = Math.multiplyExact(a, b);
    else {
      if (b == 0) throw new IllegalArgumentException("division by zero");
      if (a == Long.MIN_VALUE && b == -1) throw new ArithmeticException("long division overflow");
      r = a / b;
    }
    values.push(r);
  }

  // Numeric catalogue grammar remains unsigned literals,binary + - * /,parentheses and spaces.
  // Variable overload matches PulseLog identifiers:letter followed by letters/digits;no unary
  // operators.
  static void validArray(int[] a) {
    if (a == null || a.length > 100000) throw new IllegalArgumentException("array0..100000");
  }

  static void validText(String s) {
    if (s == null || s.length() > 100000) throw new IllegalArgumentException("text<=100000");
  }

  static long evaluate(String s) {
    return evaluate(s, Collections.emptyMap());
  }

  static long evaluate(String s, Map<String, Integer> variables) {
    validText(s);
    if (variables == null) throw new IllegalArgumentException("variables");
    Deque<Long> v = new ArrayDeque<>();
    Deque<Character> ops = new ArrayDeque<>();
    boolean needValue = true;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == ' ') continue;
      if (c >= '0' && c <= '9') {
        if (!needValue) throw new IllegalArgumentException("missing operator");
        long x = 0;
        while (i < s.length() && s.charAt(i) >= '0' && s.charAt(i) <= '9')
          x = Math.addExact(Math.multiplyExact(x, 10), s.charAt(i++) - '0');
        i--;
        v.push(x);
        needValue = false;
      } else if (Character.isLetter(c)) {
        if (!needValue) throw new IllegalArgumentException("missing operator");
        int start = i;
        while (i < s.length() && Character.isLetterOrDigit(s.charAt(i))) i++;
        String name = s.substring(start, i--);
        Integer value = variables.get(name);
        if (value == null) throw new IllegalArgumentException("undefined variable: " + name);
        v.push((long) value);
        needValue = false;
      } else if (c == '(') {
        if (!needValue) throw new IllegalArgumentException("missing operator");
        ops.push(c);
      } else if (c == ')') {
        if (needValue) throw new IllegalArgumentException("missing operand");
        while (!ops.isEmpty() && ops.peek() != '(') apply(v, ops);
        if (ops.isEmpty()) throw new IllegalArgumentException("unmatched close");
        ops.pop();
        needValue = false;
      } else if (c == '+' || c == '-' || c == '*' || c == '/') {
        if (needValue) throw new IllegalArgumentException("binary operator");
        while (!ops.isEmpty() && ops.peek() != '(' && precedence(ops.peek()) >= precedence(c))
          apply(v, ops);
        ops.push(c);
        needValue = true;
      } else throw new IllegalArgumentException("character");
    }
    if (needValue) throw new IllegalArgumentException("missing operand");
    while (!ops.isEmpty()) {
      if (ops.peek() == '(') throw new IllegalArgumentException("unmatched open");
      apply(v, ops);
    }
    if (v.size() != 1) throw new IllegalArgumentException("expression");
    return v.pop();
  }

  static String pipeline(
      int[] keys, int[] latencies, int capacity, int window, String rule, long threshold) {
    return pipeline(
        keys, latencies, capacity, window, rule, Collections.emptyMap(), threshold, true);
  }

  static String pipeline(
      int[] keys,
      int[] latencies,
      int capacity,
      int window,
      String rule,
      Map<String, Integer> variables,
      long threshold,
      boolean debug) {
    validArray(keys);
    validArray(latencies);
    if (keys.length == 0 || keys.length != latencies.length)
      throw new IllegalArgumentException("aligned nonempty events");
    if (capacity < 1 || capacity > 3000 || window < 1 || window > keys.length)
      throw new IllegalArgumentException("capacity/window");
    for (int key : keys) LRUCache.key(key);
    for (int x : latencies)
      if (x < 0 || x > 1000000000) throw new IllegalArgumentException("latency0..1e9");
    long score = evaluate(rule, variables);
    LRUCache c = new LRUCache(capacity);
    int hits = 0;
    for (int key : keys) {
      if (c.get(key) != -1) hits++;
      else c.put(key, key);
      if (debug) c.integrity();
    }
    return "hits="
        + hits
        + "/"
        + keys.length
        + "; MRU="
        + Arrays.toString(c.order())
        + "; windows="
        + Arrays.toString(slidingMax(latencies, window))
        + "; waits="
        + Arrays.toString(waitDays(latencies))
        + "; alert="
        + (score >= threshold);
  }

  static int[] slmLatencies() {
    return new int[] {210, 230, 220, 480, 260, 250, 240, 610, 300, 280, 270, 265, 520, 290, 255};
  }

  static Map<String, Integer> slmVariables() {
    return Map.of("p50", 140, "p95", 480, "err", 12, "base", 200);
  }

  static String[] slmRules() {
    return new String[] {
      "(p95 * 2 + p50) / 3",
      "p95 - base * 2",
      "err * 100 / (base - 150)",
      "(p95 + p50) / 2 - (err - 2) * 10"
    };
  }

  public static void main(String[] args) {
    System.out.println("rolling 5-min max: " + Arrays.toString(slidingMax(slmLatencies(), 5)));
    System.out.println("minutes to next worse: " + Arrays.toString(waitDays(slmLatencies())));
    for (String rule : slmRules())
      System.out.println(rule + " = " + evaluate(rule, slmVariables()));
    System.out.println(
        pipeline(new int[] {1, 2, 1, 3, 1}, new int[] {4, 4, 7, 3, 8}, 2, 3, "3+4*(2-1)*5", 20));
  }
}
