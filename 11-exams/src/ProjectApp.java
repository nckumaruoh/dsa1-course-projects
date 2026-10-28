import java.util.*;

/** ExamDesk: Results and Evaluation — six integrated, synthetic-data increments. */
public final class ProjectApp {
  static String display(Object result) {
    if (result instanceof int[] a) return Arrays.toString(a);
    if (result instanceof long[] a) return Arrays.toString(a);
    if (result instanceof Object[] a) return Arrays.deepToString(a);
    return String.valueOf(result);
  }

  public static void main(String[] args) {
    System.out.println("ExamDesk: Results and Evaluation");
    System.out.println(
        "1. Duplicate attendance: "
            + display(CoreBasics.repeats(new String[] {"101", "102", "103", "101", "104", "102"})));
    System.out.println(
        "2. Grace marks: " + display(W04CatalogueReference.minCoins(new int[] {1, 2, 5}, 13)));
    System.out.println(
        "3. Marks in any roll range: "
            + display(
                W05CatalogueReference.rangeSums(
                    new long[] {72L, 85L, 64L, 91L, 58L, 77L},
                    new int[][] {new int[] {0, 2}, new int[] {3, 5}})));
    System.out.println(
        "4. Copying check: " + display(Week7ProjectReference.lcs("abcbdacb", "abdcabcb")));
    System.out.println(
        "5. Evaluator load: "
            + display(
                Week9ProjectReference.min_capacity(new int[] {42, 38, 51, 29, 47, 33, 60, 25}, 3)));
    System.out.println(
        "6. Rank list: "
            + display(
                Week10ProjectReference.rank(
                    new String[] {"Asha", "Bala", "Chitra", "Dev"},
                    new int[] {410, 425, 410, 398},
                    new int[] {88, 90, 95, 99},
                    true)));
  }
}
