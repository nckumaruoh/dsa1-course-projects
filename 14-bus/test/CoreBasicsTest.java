import java.util.*;

public class CoreBasicsTest {
  static int checks;

  static void check(boolean condition) {
    checks++;
    if (!condition) throw new AssertionError("basic check " + checks);
  }

  static void rejects(Runnable operation) {
    checks++;
    try {
      operation.run();
    } catch (IllegalArgumentException expected) {
      return;
    }
    throw new AssertionError("expected rejection");
  }

  static String repeatsOracle(String[] ids) {
    int repeats = 0;
    String first = null;
    for (int i = 0; i < ids.length; i++) {
      boolean repeated = false;
      for (int j = 0; j < i; j++) if (ids[i].equals(ids[j])) repeated = true;
      if (repeated) {
        repeats++;
        if (first == null) first = ids[i];
      }
    }
    return "repeats="
        + repeats
        + ", distinct="
        + (ids.length - repeats)
        + ", first repeat="
        + (first == null ? "none" : first);
  }

  static String frequencyOracle(int[] ids) {
    if (ids.length == 0) return "most frequent=none (count 0), distinct=0";
    TreeSet<Integer> unique = new TreeSet<>();
    for (int x : ids) unique.add(x);
    int most = 0, winner = 0;
    for (int candidate : unique) {
      int count = 0;
      for (int x : ids) if (x == candidate) count++;
      if (count > most) {
        most = count;
        winner = candidate;
      }
    }
    return "most frequent=" + winner + " (count " + most + "), distinct=" + unique.size();
  }

  static int josephusOracle(int n, int k) {
    List<Integer> circle = new ArrayList<>();
    for (int i = 1; i <= n; i++) circle.add(i);
    int position = 0;
    while (circle.size() > 1) {
      position = (position + k - 1) % circle.size();
      circle.remove(position);
    }
    return circle.get(0);
  }

  static int regionOracle(String[] grid, int r, int c) {
    if (grid[r].charAt(c) == '#') return 0;
    Set<Integer> seen = new HashSet<>();
    seen.add(r * grid[0].length() + c);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (int row = 0; row < grid.length; row++)
        for (int col = 0; col < grid[0].length(); col++)
          if (grid[row].charAt(col) == '.' && !seen.contains(row * grid[0].length() + col)) {
            for (int[] d : new int[][] {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
              int nr = row + d[0], nc = col + d[1];
              if (nr >= 0
                  && nr < grid.length
                  && nc >= 0
                  && nc < grid[0].length()
                  && seen.contains(nr * grid[0].length() + nc)) {
                seen.add(row * grid[0].length() + col);
                changed = true;
                break;
              }
            }
          }
    }
    return seen.size();
  }

  public static void main(String[] args) {
    checks = 0;
    check(
        CoreBasics.frequent(new int[] {3, 2, 3, 2})
            .equals("most frequent=2 (count 2), distinct=2"));
    check(
        CoreBasics.repeats(new String[] {"a", "a", "a", "b", "b"})
            .equals("repeats=3, distinct=2, first repeat=a"));
    check(CoreBasics.repeats(new String[0]).equals("repeats=0, distinct=0, first repeat=none"));
    check(CoreBasics.total(null) == 0);
    check(
        CoreBasics.total(new CoreBasics.Node(5, new CoreBasics.Node(2), new CoreBasics.Node(3)))
            == 10);
    var shared = new CoreBasics.Node(1);
    rejects(() -> CoreBasics.total(new CoreBasics.Node(1, shared, shared)));
    rejects(() -> CoreBasics.total(new CoreBasics.Node(-1)));
    boolean overflow = false;
    try {
      CoreBasics.total(new CoreBasics.Node(Long.MAX_VALUE, new CoreBasics.Node(1)));
    } catch (ArithmeticException expected) {
      overflow = true;
    }
    check(overflow);
    check(CoreBasics.palindrome("Mess-Code: edoc ssem"));
    check(CoreBasics.palindrome("A-b-a"));
    check(!CoreBasics.palindrome("ab"));
    check(CoreBasics.palindrome("!"));
    check(CoreBasics.region(new String[] {"..#.", ".##.", "...#", "#..."}, 0, 0) == 9);
    check(CoreBasics.region(new String[] {"#"}, 0, 0) == 0);
    rejects(() -> CoreBasics.region(new String[] {"..", "."}, 0, 0));
    rejects(() -> CoreBasics.region(new String[] {"x"}, 0, 0));
    rejects(() -> CoreBasics.region(new String[] {"."}, 1, 0));
    rejects(() -> CoreBasics.winner(0, 1));
    rejects(() -> CoreBasics.frequent(new int[] {-1}));
    rejects(() -> CoreBasics.repeats(new String[] {null}));
    Random random = new Random(20261028);
    for (int trial = 0; trial < 500; trial++) {
      int[] ids = new int[random.nextInt(30)];
      String[] strings = new String[ids.length];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = random.nextInt(10);
        strings[i] = "id" + ids[i];
      }
      check(CoreBasics.repeats(strings).equals(repeatsOracle(strings)));
      check(CoreBasics.frequent(ids).equals(frequencyOracle(ids)));
      check(CoreBasics.frequentCountArray(ids, 10).equals(frequencyOracle(ids)));
      String[] numeric = new String[ids.length];
      for (int i = 0; i < ids.length; i++) numeric[i] = Integer.toString(ids[i]);
      check(CoreBasics.repeatsCountArray(ids, 10).equals(repeatsOracle(numeric)));
      int target = random.nextInt(20);
      long pairs = 0;
      for (int i = 0; i < ids.length; i++)
        for (int j = i + 1; j < ids.length; j++) if (ids[i] + ids[j] == target) pairs++;
      check(CoreBasics.pairs(ids, target) == pairs);
      StringBuilder text = new StringBuilder();
      for (int i = 0; i < random.nextInt(40); i++) text.append("aB-12 !".charAt(random.nextInt(7)));
      String filtered = text.toString().replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.ROOT);
      check(
          CoreBasics.palindrome(text.toString())
              == filtered.equals(new StringBuilder(filtered).reverse().toString()));
      int n = 1 + random.nextInt(30), k = 1 + random.nextInt(30);
      check(CoreBasics.winner(n, k) == josephusOracle(n, k));
      String[] grid = new String[1 + random.nextInt(5)];
      int columns = 1 + random.nextInt(5);
      for (int r = 0; r < grid.length; r++) {
        StringBuilder row = new StringBuilder();
        for (int c = 0; c < columns; c++) row.append(random.nextBoolean() ? '.' : '#');
        grid[r] = row.toString();
      }
      int r = random.nextInt(grid.length), c = random.nextInt(columns);
      check(CoreBasics.region(grid, r, c) == regionOracle(grid, r, c));
    }
    int[] same = new int[100000];
    check(CoreBasics.pairs(same, 0) == 4_999_950_000L);
    System.out.println(
        "PASS: " + checks + " basic primitive checks; fixed seed, independent oracles.");
  }
}
