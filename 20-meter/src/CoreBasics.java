import java.util.*;

/** Course-project primitives for synthetic IDs and bounded recursive structures. */
public final class CoreBasics {
  private CoreBasics() {}

  static void array(int[] a) {
    if (a == null || a.length > 100000) throw new IllegalArgumentException("array count");
    for (int x : a) if (x < 0 || x > 100000) throw new IllegalArgumentException("ID 0..100000");
  }

  public static String repeats(String[] ids) {
    if (ids == null || ids.length > 100000) throw new IllegalArgumentException("ID count");
    Set<String> seen = new HashSet<>();
    int repeats = 0;
    String first = null;
    for (String id : ids) {
      if (id == null || id.length() > 100) throw new IllegalArgumentException("ID text");
      if (!seen.add(id)) {
        repeats++;
        if (first == null) first = id;
      }
    }
    return "repeats="
        + repeats
        + ", distinct="
        + seen.size()
        + ", first repeat="
        + (first == null ? "none" : first);
  }

  /** Highest frequency; smallest numeric ID wins a tie. */
  public static String frequent(int[] ids) {
    array(ids);
    if (ids.length == 0) return "most frequent=none (count 0), distinct=0";
    Map<Integer, Integer> frequencies = new HashMap<>();
    int winner = Integer.MAX_VALUE, most = 0;
    for (int id : ids) {
      int count = frequencies.merge(id, 1, Integer::sum);
      if (count > most || count == most && id < winner) {
        winner = id;
        most = count;
      }
    }
    return "most frequent=" + winner + " (count " + most + "), distinct=" + frequencies.size();
  }

  public static long pairs(int[] weights, int target) {
    array(weights);
    if (target < 0 || target > 200000) throw new IllegalArgumentException("target");
    Map<Integer, Long> seen = new HashMap<>();
    long count = 0;
    for (int value : weights) {
      count += seen.getOrDefault(target - value, 0L);
      seen.merge(value, 1L, Long::sum);
    }
    return count;
  }

  /** Numeric count-array comparison with a declared bounded universe [0,U). */
  public static String frequentCountArray(int[] ids, int universe) {
    array(ids);
    if (universe < 1 || universe > 100001) throw new IllegalArgumentException("universe");
    int[] counts = new int[universe];
    int distinct = 0, winner = -1, most = 0;
    for (int id : ids) {
      if (id >= universe) throw new IllegalArgumentException("ID outside universe");
      if (counts[id]++ == 0) distinct++;
    }
    for (int id = 0; id < universe; id++)
      if (counts[id] > most) {
        most = counts[id];
        winner = id;
      }
    return "most frequent="
        + (winner < 0 ? "none" : winner)
        + " (count "
        + most
        + "), distinct="
        + distinct;
  }

  public static String repeatsCountArray(int[] ids, int universe) {
    array(ids);
    if (universe < 1 || universe > 100001) throw new IllegalArgumentException("universe");
    int[] counts = new int[universe];
    int repeats = 0, distinct = 0, first = -1;
    for (int id : ids) {
      if (id >= universe) throw new IllegalArgumentException("ID outside universe");
      if (counts[id]++ == 0) distinct++;
      else {
        repeats++;
        if (first < 0) first = id;
      }
    }
    return "repeats="
        + repeats
        + ", distinct="
        + distinct
        + ", first repeat="
        + (first < 0 ? "none" : first);
  }

  public static final class Node {
    public final long own;
    public final List<Node> children;

    public Node(long own, Node... children) {
      this.own = own;
      this.children = List.of(children);
    }
  }

  public static long total(Node root) {
    Set<Node> visited = Collections.newSetFromMap(new IdentityHashMap<>());
    return total(root, 0, visited);
  }

  private static long total(Node node, int depth, Set<Node> visited) {
    if (node == null) return 0;
    if (depth > 16 || node.own < 0 || !visited.add(node) || visited.size() > 1000)
      throw new IllegalArgumentException("bounded rooted tree required");
    long sum = node.own;
    for (Node child : node.children) sum = Math.addExact(sum, total(child, depth + 1, visited));
    return sum;
  }

  public static boolean palindrome(String text) {
    if (text == null || text.length() > 1000) throw new IllegalArgumentException("text length");
    int left = 0, right = text.length() - 1;
    while (left < right) {
      if (!Character.isLetterOrDigit(text.charAt(left))) {
        left++;
        continue;
      }
      if (!Character.isLetterOrDigit(text.charAt(right))) {
        right--;
        continue;
      }
      if (Character.toLowerCase(text.charAt(left++)) != Character.toLowerCase(text.charAt(right--)))
        return false;
    }
    return true;
  }

  public static int winner(int n, int k) {
    if (n < 1 || n > 1000 || k < 1 || k > 1000) throw new IllegalArgumentException("n,k 1..1000");
    int result = 0;
    for (int size = 2; size <= n; size++) result = (result + k) % size;
    return result + 1;
  }

  public static int region(String[] grid, int startRow, int startCol) {
    if (grid == null
        || grid.length < 1
        || grid.length > 30
        || grid[0] == null
        || grid[0].length() < 1
        || grid[0].length() > 30) throw new IllegalArgumentException("grid bounds");
    int rows = grid.length, cols = grid[0].length();
    for (String row : grid) {
      if (row == null || row.length() != cols)
        throw new IllegalArgumentException("rectangular grid");
      for (char cell : row.toCharArray())
        if (cell != '.' && cell != '#') throw new IllegalArgumentException("grid cell");
    }
    if (startRow < 0 || startRow >= rows || startCol < 0 || startCol >= cols)
      throw new IllegalArgumentException("start");
    if (grid[startRow].charAt(startCol) == '#') return 0;
    boolean[][] visited = new boolean[rows][cols];
    ArrayDeque<int[]> queue = new ArrayDeque<>();
    queue.add(new int[] {startRow, startCol});
    visited[startRow][startCol] = true;
    int total = 0;
    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    while (!queue.isEmpty()) {
      int[] cell = queue.remove();
      total++;
      for (int[] direction : directions) {
        int r = cell[0] + direction[0], c = cell[1] + direction[1];
        if (r >= 0
            && r < rows
            && c >= 0
            && c < cols
            && !visited[r][c]
            && grid[r].charAt(c) == '.') {
          visited[r][c] = true;
          queue.add(new int[] {r, c});
        }
      }
    }
    return total;
  }
}
