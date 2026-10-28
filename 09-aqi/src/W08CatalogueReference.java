public class W08CatalogueReference {
  static Week8ProjectReference build(int[] input) {
    if (input == null || input.length > 100000)
      throw new IllegalArgumentException("nonnull n<=100000");
    long[] a = new long[input.length];
    for (int i = 0; i < a.length; i++) a[i] = input[i];
    return new Week8ProjectReference(a);
  }

  // One-call adapters include O(n) validation/copy. Retain a built index for repeated O(log(n+1))
  // queries.
  static int lower(int[] a, int target) {
    return build(a).lower(target);
  }

  static int count(int[] a, int lo, int hi) {
    return build(a).halfOpen(lo, hi);
  }

  static int count(String[] a, String lo, String hi) {
    return new W08WordIndex(a).count(lo, hi);
  }
}
