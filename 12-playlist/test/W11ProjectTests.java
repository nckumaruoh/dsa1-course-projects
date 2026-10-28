import java.util.*;

public class W11ProjectTests {
  static long checks;

  static void ck(boolean b) {
    checks++;
    if (!b) throw new AssertionError("project check " + checks);
  }

  static void invalid(Runnable r) {
    try {
      r.run();
      throw new AssertionError("accepted invalid input");
    } catch (IllegalArgumentException expected) {
      checks++;
    }
  }

  static Week11ProjectReference.Replay checkTrace(int[] requests, int capacity) {
    List<Integer> recency = new ArrayList<>();
    int hits = 0, evictions = 0;
    for (int key : requests) {
      if (recency.remove((Integer) key)) hits++;
      else if (recency.size() == capacity) {
        recency.remove(recency.size() - 1);
        evictions++;
      }
      recency.add(0, key);
    }
    int[] before = requests.clone();
    var debug = Week11ProjectReference.replay(requests, capacity, true);
    var fast = Week11ProjectReference.replay(requests, capacity, false);
    for (var x : List.of(debug, fast)) {
      ck(x.hits == hits && x.misses == requests.length - hits && x.evictions == evictions);
      ck(x.putCalls == x.misses && x.dataNodeAllocations == x.misses);
      ck(Arrays.equals(x.order, recency.stream().mapToInt(i -> i).toArray()));
      ck(x.pointerWrites == 6L * hits + 4L * x.misses + 2L * evictions);
      ck(x.ratio().equals(requests.length == 0 ? "N/A" : hits + "/" + requests.length));
    }
    ck(Arrays.equals(before, requests));
    return debug;
  }

  public static void main(String[] args) {
    Random random = new Random(2611021);
    int[][] samples = {
      {1, 2, 3, 1, 4, 2, 1, 5, 1}, {4, 2, 4, 7, 9, 2, 4, 7}, {101, 102, 101, 103, 104, 101, 102}
    };
    // Third catalogue trace is injected from the live source snapshot by verification script.
    {
      var result = checkTrace(new int[] {1, 2, 3, 1, 4, 2, 1, 5, 1}, 3);
      ck(result.hits == 3 && Arrays.equals(result.order, new int[] {1, 5, 2}));
      checkTrace(new int[] {4, 4, 5, 4}, 1);
      checkTrace(new int[] {}, 2);
      invalid(() -> Week11ProjectReference.replay(new int[] {}, 0, false));
      var c = new Week11ProjectReference.LRUCache(1);
      c.put(1, 10);
      c.put(1, 11);
      ck(c.get(1) == 11 && c.map.size() == 1);
      int[] o = c.order();
      ck(c.get(2) == -1 && Arrays.equals(o, c.order()));
    }
    {
      var result = checkTrace(new int[] {4, 2, 4, 7, 9, 2, 4, 7}, 3);
      ck(result.hits == 1 && Arrays.equals(result.order, new int[] {7, 4, 2}));
      checkTrace(new int[] {4, 4, 5, 4}, 1);
      checkTrace(new int[] {}, 2);
      invalid(() -> Week11ProjectReference.replay(new int[] {}, 0, false));
      var c = new Week11ProjectReference.LRUCache(1);
      c.put(1, 10);
      c.put(1, 11);
      ck(c.get(1) == 11 && c.map.size() == 1);
      int[] o = c.order();
      ck(c.get(2) == -1 && Arrays.equals(o, c.order()));
    }
    {
      var result = checkTrace(new int[] {1, 2, 1, 3, 2, 1, 3}, 2);
      ck(result.hits == 1 && Arrays.equals(result.order, new int[] {3, 1}));
      checkTrace(new int[] {4, 4, 5, 4}, 1);
      checkTrace(new int[] {}, 2);
      invalid(() -> Week11ProjectReference.replay(new int[] {}, 0, false));
      var c = new Week11ProjectReference.LRUCache(1);
      c.put(1, 10);
      c.put(1, 11);
      ck(c.get(1) == 11 && c.map.size() == 1);
      int[] o = c.order();
      ck(c.get(2) == -1 && Arrays.equals(o, c.order()));
    }
    for (int cap = 1; cap <= 10; cap++)
      for (int t = 0; t < 200; t++) {
        int[] request = new int[random.nextInt(100)];
        for (int i = 0; i < request.length; i++) request[i] = random.nextInt(15);
        checkTrace(request, cap);
      }
    for (int capacity = 1; capacity <= 8; capacity++) {
      var c = new Week11ProjectReference.LRUCache(capacity);
      Map<Integer, Integer> values = new HashMap<>();
      List<Integer> order = new ArrayList<>();
      long gets = 0, puts = 0, hits = 0, misses = 0, allocations = 0, evictions = 0, writes = 0;
      for (int t = 0; t < 10000; t++) {
        int key = random.nextInt(20);
        if (random.nextBoolean()) {
          gets++;
          boolean present = values.containsKey(key);
          ck(c.get(key) == values.getOrDefault(key, -1));
          if (present) {
            hits++;
            writes += 6;
            order.remove((Integer) key);
            order.add(0, key);
          } else misses++;
        } else {
          puts++;
          int value = random.nextInt(100001);
          if (values.containsKey(key)) {
            writes += 6;
            order.remove((Integer) key);
          } else {
            writes += 4;
            allocations++;
          }
          order.add(0, key);
          values.put(key, value);
          c.put(key, value);
          if (order.size() > capacity) {
            values.remove(order.remove(order.size() - 1));
            evictions++;
            writes += 2;
          }
        }
        c.integrity();
        ck(Arrays.equals(c.order(), order.stream().mapToInt(x -> x).toArray()));
        ck(c.map.size() == values.size());
        for (var e : values.entrySet()) ck(c.map.get(e.getKey()).value == e.getValue());
        ck(c.getCalls == gets && c.putCalls == puts && c.getHits == hits && c.getMisses == misses);
        ck(
            c.evictions == evictions
                && c.dataNodeAllocations == allocations
                && c.pointerWrites == writes);
      }
    }
    for (int capacity : new int[] {0, -1, 3001})
      invalid(() -> new Week11ProjectReference.LRUCache(capacity));
    var c = new Week11ProjectReference.LRUCache(1);
    for (int key : new int[] {-1, 10001}) {
      invalid(() -> c.get(key));
      invalid(() -> c.put(key, 1));
    }
    for (int value : new int[] {-1, 100001}) invalid(() -> c.put(1, value));
    ck(c.getCalls == 0 && c.putCalls == 0 && c.map.isEmpty());
    invalid(() -> Week11ProjectReference.replay(null, 1, false));
    invalid(() -> Week11ProjectReference.replay(new int[200001], 1, false));
    invalid(() -> Week11ProjectReference.replay(new int[] {-1}, 1, false));
    invalid(() -> Week11ProjectReference.compareCapacities(new int[] {1}, null, false));
    invalid(() -> Week11ProjectReference.compareCapacities(new int[] {1}, new int[] {}, false));
    invalid(() -> Week11ProjectReference.compareCapacities(new int[] {1}, new int[] {1, 0}, false));
    var empty = checkTrace(new int[] {}, 2);
    ck(Double.isNaN(empty.percent()));
    checkTrace(new int[] {4, 4, 5, 4}, 1);
    checkTrace(new int[] {1, 2, 1, 3, 2, 1, 3}, 2);
    checkTrace(new int[] {0, 10000, 0, 10000}, 3000);
    var last = new Week11ProjectReference.LRUCache(1);
    last.put(1, 10);
    last.put(1, 11);
    ck(last.get(1) == 11 && last.map.size() == 1);
    int[] before = last.order();
    ck(last.get(2) == -1 && Arrays.equals(before, last.order()));
    last.integrity();
    int[] max = new int[200000];
    for (int i = 0; i < max.length; i++) max[i] = i % 10001;
    var big = Week11ProjectReference.replay(max, 3000, false);
    ck(big.total == 200000 && big.hits + big.misses == 200000 && big.order.length == 3000);
    int[] slm = Week11ProjectReference.slmRequests();
    int previous = -1;
    for (var result :
        Week11ProjectReference.compareCapacities(slm, new int[] {10, 50, 100, 500, 2000}, false)) {
      LinkedHashMap<Integer, Boolean> oracle = new LinkedHashMap<>(16, 0.75f, true);
      int hits = 0, evictions = 0;
      for (int key : slm) {
        if (oracle.get(key) != null) hits++;
        else {
          oracle.put(key, true);
          if (oracle.size() > result.capacity) {
            oracle.remove(oracle.keySet().iterator().next());
            evictions++;
          }
        }
      }
      ck(result.hits == hits && result.misses == 12000 - hits && result.evictions == evictions);
      List<Integer> order = new ArrayList<>(oracle.keySet());
      Collections.reverse(order);
      ck(Arrays.equals(result.order, order.stream().mapToInt(x -> x).toArray()));
      ck(hits >= previous);
      previous = hits;
    }
    // Exercise the normal public API at the maximum declared request count.
    // A cycle longer than capacity has no hits; the last C requests are resident.
    int[] publicRequests = new int[200000];
    for (int i = 0; i < publicRequests.length; i++) publicRequests[i] = i % 10001;
    int[] publicOrder = new int[3000];
    for (int i = 0; i < publicOrder.length; i++) publicOrder[i] = (199999 - i) % 10001;
    ck(
        Week11ProjectReference.simulate(publicRequests, 3000)
            .equals(
                "hits=0 of 200000, final order (most recent first) "
                    + Arrays.toString(publicOrder)));
    System.out.println(
        "PASS: "
            + checks
            + " project assertions;3 catalogue traces,15 repeated listed checks,cache"
            + " counters,validation,SLM capacity experiment.");
  }
}
