/**
 * https://open.kattis.com/problems/fakescoreboard
 * Author: Micah Stairs
 * Solved On: February 8, 2017
 * 
 * I initially tackled this problem with Finn Lidbetter and William Fiset.
 * Although we quickly identified this as a maximum flow problem, we were
 * not able to come up with a way to get the lexicographically smallest
 * solution.
 *
 * After more research and many more attempts, I decided to look at the
 * judge's solution to see what technique we must be missing. Two different
 * solutions were outlined, including a maximum flow solution.
 *
 * The trick, however, is to start with any maximum flow solution (not worrying
 * about the lexicographical order), and then going through and trying to turn
 * the earlier Y's to N's, one by one. To check to see if we can turn a Y into
 * a N, we first undo the flow path which was added when this letter was picked.
 * And we must also remove the particular edge (corresponding to the cell). If
 * we can push more flow through the network, then we are able to change the Y
 * to an N. Otherwise it must remain a Y. Additionally, if the cell was a N already,
 * then we must also remove the edge so that it doesn't get chosen later when we try
 * pushing more flow through the network.
 **/

import java.util.*;
import java.io.*;

public class FakeScoreboard {
  
  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    while (true) {

      // Read first line of input
      String[] line = br.readLine().split(" ");
      int n = Integer.parseInt(line[0]);
      int m = Integer.parseInt(line[1]);

      // End of input
      if (n == 0 && m == 0) break;

      // Setup graph for max flow
      int nNodes = n + m + 2;
      final int SOURCE = nNodes - 2;
      final int SINK = nNodes - 1;
      int[][] cap = new int[nNodes][nNodes];

      // Read and store input, keeping track of sums
      int sum1 = 0;
      int sum2 = 0;
      line = br.readLine().split(" ");
      int[] arr1 = new int[n];
      for (int i = 0; i < n; i++) {
        arr1[i] = Integer.parseInt(line[i]);
        cap[SOURCE][i] = arr1[i];
        sum1 += arr1[i];
      }
      int[] arr2 = new int[m];
      line = br.readLine().split(" ");
      for (int i = 0; i < m; i++) {
        arr2[i] = Integer.parseInt(line[i]);
        cap[n+i][SINK] = arr2[i];
        sum2 += arr2[i];
      }
      br.readLine();

      // It's impossible if the sums don't add up to the same thing
      if (sum1 != sum2) {
        sb.append("IMPOSSIBLE\n\n");
        continue;
      }

      // Set the capacity of each cell to be 1
      for (int j = 0; j < m; j++) {
        for (int i = 0; i < n; i++) {
          cap[i][n+j] = 1;
        }
      }
        
      // Compute the maximum flow
      int flow = maxFlow(cap, SOURCE, SINK);

      // Indicate if it is impossible
      if (sum1 != flow) {
        sb.append("IMPOSSIBLE\n\n");
        continue;
      }

      // Go through the grid one cell at a time, trying to change the existing
      // flow to make it lexicographically smaller
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {

          // Try to change the Y to a N
          if (cap[i][n+j] == 0) {

            // "Remove" this path
            cap[SOURCE][i]++;
            cap[n+j][SINK]++;

            // Try to see if another path exists
            flow = maxFlow(cap, SOURCE, SINK);

            // Could not change it to a N
            if (flow == 0) {
              cap[SOURCE][i]--;
              cap[n+j][SINK]--;
              sb.append("Y");

            // Could change it to a N
            } else {
              sb.append("N");
            }

          // Already a N
          } else {
            cap[i][n+j] = 0; // Make it so that this cannot be chosen later
            sb.append("N");
          }
        }
        sb.append("\n");
      }
      
      // Add blank line after each test case
      sb.append("\n");

   }

   // Output all of the answers
   System.out.print(sb);
    
  }

  // Ford-Fulkerson algorithm implemented with DFS
  static int maxFlow(int[][] cap, int s, int t) {
    for (int flow = 0;;) {
      boolean[] mincut = new boolean[cap.length];
      int df = findPath(cap, mincut, s, t, Integer.MAX_VALUE);
      if (df == 0) return flow;
      flow += df;
    }
  }
  static int findPath(int[][] cap, boolean[] vis, int u, int t, int f) {
    if (u == t) return f;
    vis[u] = true;
    int len = cap.length;
    for (int v = 0; v < len; v++) {
      if (!vis[v] && cap[u][v] > 0) {
        int df = findPath(cap, vis, v, t, Math.min(f, cap[u][v]));
        if (df > 0) { cap[u][v] -= df; cap[v][u] += df; return df; }
      }
    }
    return 0;
  }

}