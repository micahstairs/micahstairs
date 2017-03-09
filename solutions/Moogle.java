/**
 * https://open.kattis.com/problems/moogle
 * Author: Micah Stairs
 * Solved On: March 8, 2017
 * 
 * I dicussed this solution with James Black. We came up with a dynamic programming
 * approach which is O(n^3). It depends on having the interpolation error
 * pre-compututed, which can be done in O(n^3) as well. Otherwise this dynamic
 * programming approach would be O(n^4).
 **/

import java.util.*;
import java.io.*;

public class Moogle {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static int h;
  static double[][] costs;
  static Double[][] memo;

  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.parseInt(br.readLine());
    while (t-- > 0) {

      // Read input
      String[] line = br.readLine().split(" ");
      h = Integer.parseInt(line[0]);
      int c = Integer.parseInt(line[1]);
      double[] arr = new double[h];
      line = br.readLine().split(" ");
      for (int i = 0; i < h; i++) {
        arr[i] = Double.parseDouble(line[i]);
      }

      // Pre-compute interpolation costs
      costs = new double[h][h];
      for (int s = 0; s < h; s++) {
        for (int e = s + 1; e < h; e++) {
          for (int i = s + 1; i < e; i++) {
            double interpolated = arr[s] + (arr[e] - arr[s]) * ((i - s) / (double) (e - s));
            costs[s][e] += Math.abs(arr[i] - interpolated);
          }
        }
      }

      // Compute answer
      memo = new Double[h][c];
      sb.append((f(0, c-1) / h) + "\n");
    
    }

    // Output answers
    System.out.print(sb);
  
  }

  // Recursive method used to minimize the interpolation error of selecting houses
  static double f(int index, int nSelectionsLeft) {
      
    // Base case
    if (nSelectionsLeft == 1) return costs[index][h-1];

    // Already computed this subproblem
    if (memo[index][nSelectionsLeft] != null) return memo[index][nSelectionsLeft];

    double minError = Double.POSITIVE_INFINITY;

    // Try each option for the next selected house
    for (int i = index + 1; i < h; i++) {
      minError = Math.min(minError, costs[index][i] + f(i, nSelectionsLeft - 1));
    }

    // Store and return the result
    return memo[index][nSelectionsLeft] = minError;

  }

}