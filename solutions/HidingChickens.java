/**
 * https://open.kattis.com/problems/hidingchickens
 * Author: Micah Stairs
 * Solved On: March 28, 2017
 * 
 * I got MLE on first DP solution to this problem despite the fact that I had
 * roughly 63 million subproblems (which I thought would be manageable). By
 * taking advantage of some of the properties in the problem I reduced this
 * to only 1 million subproblems. The tradeoff was that each recursive call
 * had a higher time complexity.
 *
 * The basic idea is that a DP subproblem is identified by the subset of holes
 * which have already been filled. We can assume the fox is always at the roost
 * at the start of each subproblem.
 **/

import java.util.*;
import java.io.*;

public class HidingChickens {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  static final int BLANK = -1;

  static int n;
  static double[][] dist;
  static double[] memo;

  public static void main(String[] args) throws IOException {

    // Read and store input
    String[] line = br.readLine().split(" ");
    double roostX = Double.parseDouble(line[0]);
    double roostY = Double.parseDouble(line[1]);
    n = Integer.parseInt(br.readLine());
    double[] x = new double[n+1];
    double[] y = new double[n+1];
    for (int i = 0; i < n; i++) {
      line = br.readLine().split(" ");
      x[i] = Double.parseDouble(line[0]);
      y[i] = Double.parseDouble(line[1]);
    }
    x[n] = roostX;
    y[n] = roostY;

    // Pre-compute distances
    dist = new double[n+1][n+1];
    for (int i = 0; i <= n; i++) {
      for (int j = i + 1; j <= n; j++) {
        dist[i][j] = dist[j][i] = (double) Math.hypot(x[i] - x[j], y[i] - y[j]);
      }
    }

    // Setup memo table
    memo = new double[1<<n];
    Arrays.fill(memo, BLANK);

    // Compute and output the result
    System.out.println(minimize(0));

  }

  // Use recursion with memoization to minimize the distance the fox needs to travel
  static double minimize(int holesFilled) {

    // Check memo
    if (memo[holesFilled] != BLANK) return memo[holesFilled];

    double min = Double.POSITIVE_INFINITY;

    // Try going to each hole
    for (int i = 0; i < n; i++) {
      if (isSet(holesFilled, i)) continue;

      // Only go to this one hole and then go back to the roost (or stop because we are done)
      int holesFilledAfterFirst = setBit(holesFilled, i);
      if (holesFilledAfterFirst == (1 << n) - 1) min = Math.min(min, dist[n][i]);
      else min = Math.min(min, 2 * dist[n][i] + minimize(holesFilledAfterFirst));

      // Try going to another hole afterwards and then back to the roost (or stop because we are done)
      for (int j = 0; j < n; j++) {
        if (isSet(holesFilledAfterFirst, j)) continue;
        int holesFilledAfterSecond = setBit(holesFilledAfterFirst, j);
        if (holesFilledAfterSecond == (1 << n) - 1) min = Math.min(min, dist[n][i] + dist[i][j]);
        else min = Math.min(min, dist[n][i] + dist[i][j] + dist[j][n] + minimize(holesFilledAfterSecond));
      }

    }

    // Store and return result
    return memo[holesFilled] = min;

  }

  // Check to see if a bit is set
  static boolean isSet(int set, int i) {
    return (set & (1 << i)) != 0;
  }

  // Set a bit
  static int setBit(int set, int i) {
    return set | (1 << i);
  }

}