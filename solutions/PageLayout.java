/**
 * https://open.kattis.com/problems/pagelayout
 * Author: Micah Stairs
 * Solved On: March 17, 2017
 * 
 * William Fiset initially suggested using a recursive backtracking to approach
 * this problem. This is more efficient than simply trying all combinations of
 * rectangles by a factor of O(n). The fact that some branches will be pruned
 * also helps too. Before running the backtracking we pre-compute which
 * rectangles overlap each other.
 **/

import java.util.*;
import java.io.*;

public class PageLayout {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static int n;
  static boolean[][] overlap;
  static int[] areas;

  public static void main(String[] args) throws IOException {

    // Process each test case
    while (true) {
      
      // Read the first line of input
      n = Integer.parseInt(br.readLine());
    
      // End of test cases
      if (n == 0) break;
  
      // Read and store input      
      int[] w = new int[n];
      int[] h = new int[n];
      int[] x = new int[n];
      int[] y = new int[n];
      areas = new int[n];
      for (int i = 0; i < n; i++) {
        String[] line = br.readLine().split(" ");
        w[i] = Integer.parseInt(line[0]);
        h[i] = Integer.parseInt(line[1]);
        x[i] = Integer.parseInt(line[2]);
        y[i] = Integer.parseInt(line[3]);
        areas[i] = w[i] * h[i];
      }

      // Figure out which pairs of rectangles overlap
      overlap = new boolean[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          boolean toLeft  = x[i] + w[i] <= x[j];
          boolean toRight = x[j] + w[j] <= x[i];
          boolean above   = y[i] + h[i] <= y[j];
          boolean below   = y[j] + h[j] <= y[i];
          if (!toLeft && !toRight && !above && !below) {
            overlap[i][j] = overlap[j][i] = true;
          }
        }
      }

      // Compute and store answer
      sb.append(maximizeArea(0, new boolean[n]) + "\n");
    
    }

    // Output all of the answers
    System.out.print(sb);
  
  }

  // Use recursive backtracking to maximize the combined area of non-overlapping rectangles
  static int maximizeArea(int index, boolean[] chosen) {

    // Base case
    if (index == n) return 0;

    int max = 0;

    // Include this rectangle
    chosen[index] = true;
    boolean valid = true;
    for (int i = 0; i < index; i++) {
      if (chosen[i] && overlap[index][i]) {
        valid = false;
        break;
      }
    }
    if (valid) max = areas[index] + maximizeArea(index + 1, chosen);
    chosen[index] = false;

    // Try not including this rectangle
    return Math.max(max, maximizeArea(index + 1, chosen));

  }

}