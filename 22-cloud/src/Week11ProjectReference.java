import java.util.*;

public class Week11ProjectReference {
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

  static class Replay {
    final int capacity, total, hits, misses, evictions;
    final int[] order;
    final long putCalls, dataNodeAllocations, pointerWrites;

    Replay(LRUCache c, int total) {
      capacity = c.capacity;
      this.total = total;
      hits = (int) c.getHits;
      misses = (int) c.getMisses;
      evictions = (int) c.evictions;
      order = c.order();
      putCalls = c.putCalls;
      dataNodeAllocations = c.dataNodeAllocations;
      pointerWrites = c.pointerWrites;
    }

    String ratio() {
      return total == 0 ? "N/A" : hits + "/" + total;
    }

    double percent() {
      return total == 0 ? Double.NaN : 100.0 * hits / total;
    }
  }

  static void validate(int[] requests, int capacity) {
    if (requests == null || requests.length > 200000)
      throw new IllegalArgumentException("requests0..200000");
    if (capacity < 1 || capacity > 3000) throw new IllegalArgumentException("capacity1..3000");
    for (int key : requests) LRUCache.key(key);
  }

  static Replay replay(int[] requests, int capacity, boolean debug) {
    validate(requests, capacity);
    LRUCache c = new LRUCache(capacity);
    for (int key : requests) {
      if (c.get(key) == -1) c.put(key, key);
      if (debug) c.integrity();
    }
    return new Replay(c, requests.length);
  }

  static String simulate(int[] requests, int capacity) {
    // Keep expensive per-request structural scans opt-in for explicit debug replay.
    Replay r = replay(requests, capacity, false);
    return "hits="
        + r.hits
        + " of "
        + r.total
        + ", final order (most recent first) "
        + Arrays.toString(r.order);
  }

  // Exact SLM synthetic one-hour workload;not measured classroom/production traffic.
  static int[] slmRequests() {
    int[] requests = new int[12000];
    long x = 2024;
    for (int i = 0; i < requests.length; i++) {
      x = (x * 1103515245L + 12345L) % 2147483648L;
      int r = (int) (x % 100);
      requests[i] = r < 60 ? (int) (x / 100 % 50) : 50 + (int) (x / 100 % 5000);
    }
    return requests;
  }

  static List<Replay> compareCapacities(int[] requests, int[] capacities, boolean debug) {
    if (capacities == null || capacities.length == 0)
      throw new IllegalArgumentException("nonempty capacities");
    List<Replay> out = new ArrayList<>();
    for (int c : capacities) validate(requests, c);
    for (int c : capacities) out.add(replay(requests, c, debug));
    return out;
  }

  public static void main(String[] args) {
    for (Replay r : compareCapacities(slmRequests(), new int[] {10, 50, 100, 500, 2000}, false))
      System.out.printf(
          Locale.ROOT,
          "capacity %4d: hit ratio %5.1f%%  (%d hits, %d misses, %d evictions)%n",
          r.capacity,
          r.percent(),
          r.hits,
          r.misses,
          r.evictions);
  }
}
