/**
 * https://open.kattis.com/problems/string
 * Author: Micah Stairs
 * Solved On: January 26, 2017
 *
 * I tackled this problem with Finn Lidbetter. We initially tried to formulate
 * a DP approach to this problem, but couldn't find something that would be fast
 * enough. So we started to work on a backtracking approach with as much pruning
 * as possible.
 *
 * Although this worked for most cases, there were a few cases where this would be
 * way too slow for. Eventually I realized that we could simply add memoization to
 * our recursive backtracking method since there were only 200,000 possible inputs.
 *
 * This worked and we were actually able to remove a few of the optimizations that we
 * had made (to prune the state space). I replaced these with a simpler optimization
 * that was actually more effective. You see, we have to run this DP for each possible
 * outer-most quotation level (1-100). We do this in reverse order so that we can find
 * the largest one that works first. But the cost to clear the memo is actually fairly
 * expensive, especially if the visited state space is sparse. So  the optimization
 * is to have a second array which tells you which DP iteration that particular
 * sub-problem was last solved for. By incrementing the variable indicating the current
 * DP iteration, we've essentially cleared the memo in constant time. This is a trick
 * that can be used in a wide variety of problems.
 **/

import java.util.*;
import java.io.*;

public class StringTheory {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  static int DOWN = 0;
  static int UP = 1;
  static int MAX = 100;

  static int n;
  static int outermostDepth;
  static int[] arr;

  // Used to store memo
  static boolean[][][][] memo = new boolean[MAX][MAX+1][MAX+1][2];

  // Used to prevent the need to reset the memo each time
  static int[][][][] memoId = new int[MAX][MAX+1][MAX+1][2];

  public static void main(String[] args) throws IOException {
    
    // Read and store input
    n = Integer.parseInt(br.readLine());
    String[] split = br.readLine().split(" ");
    arr = new int[n];
    int total = 0;
    for (int i = 0; i < n; i++) {
      arr[i] = Integer.parseInt(split[i]);
      total += arr[i];
    }

    // Special case (odd number of quotes)
    if (total % 2 == 1) {
      System.out.println("no quotation");
      return;
    }

    // Initialize memo        
    for (int i = 0; i < MAX; i++) {
      for (int j = 0; j <= MAX; j++) {
        for (int k = 0; k <= MAX; k++) {
          Arrays.fill(memoId[i][j][k], -1);
        }
      }
    }

    // Starting at the 100-quotation, try them until one works
    for (int d = 100; d >= 1; d--) {
      outermostDepth = d;  
      if (f(0, arr[0], d, DOWN)) {
        System.out.println(d);
        return;
      }
    }

    // Not possible
    System.out.println("no quotation");
  
  }

  // Use recursive DP to check to see if it can be properly quoted with the specified outermost depth
  static boolean f(int index, int currentLeft, int depth, int direction) {

    int a = index;
    int b = currentLeft;
    int c = depth;
    int d = direction;

    // Reached illegal state
    if (currentLeft < 0 || depth > outermostDepth) return false;

    // Already computed this subproblem
    if (memoId[a][b][c][d] == outermostDepth) return memo[a][b][c][d];
    memoId[a][b][c][d] = outermostDepth;

    // Base case
    if (index == n - 1 && currentLeft == depth)
      return memo[a][b][c][d] = (depth == outermostDepth && direction == 1);

    // Move to next index
    if (currentLeft == 0) return memo[a][b][c][d] = f(index + 1, arr[index + 1], depth, direction);

    // If we've gone down to 1, we need to repeat 1 again before coming back up
    boolean needToRepeatOne = (depth == 1) && (direction == DOWN);

    // Switch direction
    if (depth == 1 && direction == DOWN) direction = UP;

    // Go up
    if (direction == UP && f(index, currentLeft - depth, needToRepeatOne ? depth : depth + 1, direction))
      return memo[a][b][c][d] = true;

    // Continue going down
    if (direction == DOWN && f(index, currentLeft - depth, depth - 1, direction))
      return memo[a][b][c][d] = true;

    // Repeat depth
    if (direction == UP && depth < outermostDepth && f(index, currentLeft - depth, depth, needToRepeatOne ? UP : DOWN))
      return memo[a][b][c][d] = true;

    // Was not possible
    return memo[a][b][c][d] = false;

  }

}