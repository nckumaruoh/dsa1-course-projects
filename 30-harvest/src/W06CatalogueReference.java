import java.util.*;

public class W06CatalogueReference {
  static void array(int[] a) {
    if (a == null || a.length > 1000) throw new IllegalArgumentException("nonnull n<=1000");
  }

  static int[] pair(int[] values, int target) {
    array(values);
    int[] a = values.clone();
    Arrays.sort(a);
    int l = 0, r = a.length - 1;
    while (l < r) {
      long sum = (long) a[l] + a[r];
      if (sum == target) return new int[] {a[l], a[r]};
      if (sum < target) l++;
      else r--;
    }
    return new int[0];
  }

  static void sorted(int[] a) {
    array(a);
    for (int i = 1; i < a.length; i++)
      if (a[i] < a[i - 1]) throw new IllegalArgumentException("sorted input required");
  }

  static int[] merge(int[] a, int[] b) {
    sorted(a);
    sorted(b);
    if ((long) a.length + b.length > 1000) throw new IllegalArgumentException("combined n<=1000");
    int[] out = new int[a.length + b.length];
    int i = 0, j = 0, k = 0;
    while (i < a.length || j < b.length)
      out[k++] = j == b.length || (i < a.length && a[i] <= b[j]) ? a[i++] : b[j++];
    return out;
  }

  static int maxSlots(int[][] input) {
    if (input == null || input.length > 1000) throw new IllegalArgumentException("nonnull n<=1000");
    int[][] a = new int[input.length][];
    for (int i = 0; i < a.length; i++) {
      if (input[i] == null || input[i].length != 2 || input[i][0] >= input[i][1])
        throw new IllegalArgumentException("start<end required");
      a[i] = input[i].clone();
    }
    Arrays.sort(a, Comparator.comparingInt(x -> x[1]));
    boolean first = true;
    int end = 0, count = 0;
    for (int[] x : a)
      if (first || x[0] >= end) {
        count++;
        end = x[1];
        first = false;
      }
    return count;
  }

  static long water(int[] a) {
    array(a);
    for (int x : a)
      if (x < 0 || x > 1000000000) throw new IllegalArgumentException("height0..10^9");
    int l = 0, r = a.length - 1, left = 0, right = 0;
    long total = 0;
    while (l <= r) {
      if (left <= right) {
        left = Math.max(left, a[l]);
        total += left - a[l++];
      } else {
        right = Math.max(right, a[r]);
        total += right - a[r--];
      }
    }
    return total;
  }
}
