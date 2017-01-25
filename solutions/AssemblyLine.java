/**
 * https://open.kattis.com/problems/assemblyline
 * Author: Micah Stairs
 * Solved On: January 24, 2017
 * 
 * William Fiset and Finn Lidbetter were the ones who first looked at this
 * problem and identified the solution (essentially the same as "Mixing Colours").
 * I then coded it up independently.
 *
 * This problem is solved using a straight-forward iterative dynamic programming
 * approach (formerly referred to as the CYK algorithm). Only minor optimizations
 * were needed in order to make the solution run fast enough.
 **/

import java.util.*;
import java.io.*;

public class AssemblyLine {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static int INF = 1_000_000_000;

  static int MAX_CHARS = 26;
  static int MAX_LEN = 200;
  static int[][][] dp = new int[MAX_LEN][MAX_LEN][MAX_CHARS];

  static Map<Character, Integer> charToIndex = new HashMap<>();
  static Map<Integer, Character> indexToChar = new HashMap<>();

  public static void main(String[] args) throws IOException {

    // Process test cases
    while (true) {
    
      // Get the number of characters
      int n = Integer.parseInt(br.readLine());
    
      // End of input
      if (n == 0) break;
        
      // Store symbols
      String[] line = br.readLine().split(" ");
      for (int i = 0; i < n; i++) {
        charToIndex.put(line[i].charAt(0), i);
        indexToChar.put(i, line[i].charAt(0));
      }

      // Store production rules
      int[][] costs = new int[n][n];
      int[][] products = new int[n][n];
      for (int i = 0; i < n; i++) {
        line = br.readLine().split(" ");
        for (int j = 0; j < n; j++) {
          String str = line[j];
          costs[i][j] = Integer.parseInt(str.substring(0, str.length() - 2));
          products[i][j] = charToIndex.get(str.charAt(str.length() - 1));
        }
      }

      // Process each string
      int q = Integer.parseInt(br.readLine());
      while (q-- > 0) {
        
        // Read string
        String str = br.readLine();
        int m = str.length();
        
        // Initialize DP
        for (int i = 0; i < m; i++) {
          for (int j = 0; j < m; j++) {
            Arrays.fill(dp[i][j], INF);
          }
        }
        for (int i = 0; i < m; i++) {
          int index = charToIndex.get(str.charAt(i));
          dp[i][i][index] = 0;
        }

        // Do iterative DP
        for (int len = 2; len <= m; len++) {
          for (int start = 0; start < m; start++) {
            int end = start + len - 1;
            if (end >= m) continue;
            for (int mid = start; mid < end; mid++) {
              for (int i = 0; i < n; i++) {
                if (dp[start][mid][i] == INF) continue;
                for (int j = 0; j < n; j++) {
                  int result = dp[start][mid][i] + dp[mid+1][end][j] + costs[i][j];
                  int p = products[i][j];
                  if (result < dp[start][end][p]) {
                    dp[start][end][p] = result;
                  }
                }
              }
            }
          }
        }

        // Extract the cheapest character that can get produced
        int min = INF;
        char ch = '?';
        for (int i = 0; i < n; i++) {
          if (dp[0][m-1][i] < min) {
            min = dp[0][m-1][i];
            ch = indexToChar.get(i);
          }
        }

        // Append answer
        sb.append(min + "-" + ch + "\n");

      }

      // Append new line
      sb.append("\n");
    
    }

    // Output all of the answers
    System.out.print(sb);
  
  }

}