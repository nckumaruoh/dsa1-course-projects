import java.util.*;

/** One-use, nonnegative integer loads. Dense memo is for a checked small capacity budget. */
public final class CapacityPlanner {
  private final int[] loads;
  private final int capacity;
  private final Boolean[][] memo;
  public long calls, hits, misses;

  public CapacityPlanner(int[] input, int capacity) {
    if (input == null || input.length > 30 || capacity < 0 || capacity > 5000)
      throw new IllegalArgumentException("require n<=30 and capacity0..5000");
    loads = input.clone();
    for (int load : loads)
      if (load < 0 || load > 5000) throw new IllegalArgumentException("load outside0..5000");
    this.capacity = capacity;
    memo = new Boolean[loads.length][capacity + 1];
  }

  public boolean exact(int target) {
    if (target < 0 || target > capacity)
      throw new IllegalArgumentException("target outside memo budget");
    return reachable(0, target);
  }

  private boolean reachable(int i, int remain) {
    calls++;
    if (remain == 0) return true;
    if (i == loads.length) return false;
    if (memo[i][remain] != null) {
      hits++;
      return memo[i][remain];
    }
    misses++;
    boolean ok = reachable(i + 1, remain); // skip first, as in the lecture trace
    if (!ok && loads[i] <= remain) ok = reachable(i + 1, remain - loads[i]);
    memo[i][remain] = ok; // false is a computed result too
    return ok;
  }

  public int bestTotal() {
    for (int target = capacity; target >= 0; target--) if (exact(target)) return target;
    throw new AssertionError("zero is always reachable");
  }

  public List<Integer> selectedIndices(int target) {
    if (!exact(target)) throw new IllegalArgumentException("unreachable target");
    List<Integer> selected = new ArrayList<>();
    for (int i = 0; i < loads.length && target > 0; i++) {
      if (loads[i] <= target && reachable(i + 1, target - loads[i])) {
        selected.add(i);
        target -= loads[i];
      }
    }
    if (target != 0) throw new AssertionError("reconstruction failed");
    return selected;
  }
}
