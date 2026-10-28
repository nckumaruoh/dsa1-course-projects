/**
 * Frozen half-open aggregate. Signed corrections are allowed. Long overflow raises
 * ArithmeticException. Rebuild after source updates.
 */
public final class PulseLogWeek5 {
  private final long[] p;

  public PulseLogWeek5(long[] minuteTotals) {
    if (minuteTotals == null || minuteTotals.length > 100000)
      throw new IllegalArgumentException("Require nonnull totals, n<=100000");
    p = new long[minuteTotals.length + 1];
    for (int i = 0; i < minuteTotals.length; i++) p[i + 1] = Math.addExact(p[i], minuteTotals[i]);
  }

  public long query(int start, int end) {
    if (start < 0 || end < start || end >= p.length)
      throw new IndexOutOfBoundsException("Require 0 <= start <= end <= n");
    return Math.subtractExact(p[end], p[start]);
  }
}
