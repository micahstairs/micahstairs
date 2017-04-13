/**
 * https://open.kattis.com/problems/paintings
 * Author: Micah Stairs
 * Solved On: April 1, 2017
 * 
 * This problem can easily be solved using recursive backtracking. We set up
 * the backtracking in such a way that the first valid painting we encounter
 * is the favorite painting. If we reach this base case again then it does
 * not overwrite the favorite we originally found, but we increment the counter.
 **/

import java.util.*;
import java.io.*;

public class Paintings {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  static Map<String, Integer> map;
  static Map<Integer, String> inv;
  static int n;
  static boolean[][] badPair;

  static int count;
  static String favorite;

  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.parseInt(br.readLine());
    while (t-- > 0) {
      
      // Get the number of colours
      n = Integer.parseInt(br.readLine());

      // Store the colours
      String[] colours = br.readLine().split(" ");
      map = new HashMap<>();
      inv = new HashMap<>();
      for (int i = 0; i < n; i++) {
        map.put(colours[i], i);
        inv.put(i, colours[i]);
      }

      // Process bad pairs of colours
      int m = Integer.parseInt(br.readLine());
      badPair = new boolean[n][n];
      for (int i = 0; i < m; i++) {
        String[] line = br.readLine().split(" ");
        int a = map.get(line[0]);
        int b = map.get(line[1]);
        badPair[a][b] = badPair[b][a] = true;
      }

      // Compute and output result
      count = 0;
      favorite = null;
      backtrack(new int[n], 0, new boolean[n], -1);
      System.out.println(count);
      System.out.println(favorite);
    
    }

  }

  // Use recursive backtracking to explore all paintings in order of preference
  static void backtrack(int[] arrangement, int index, boolean[] used, int previous) {

    // Base case
    if (index == n) {
      count++;
      if (favorite == null) {
        favorite = "";
        for (int i = 0; i < n; i++) {
          if (i > 0) favorite += " ";
          favorite += inv.get(arrangement[i]);
        }
      }
    }

    // Recursive cases
    outer: for (int i = 0; i < n; i++) {
      if (used[i]) continue;
      if (previous != -1 && badPair[previous][i]) continue outer;
      arrangement[index] = i;
      used[i] = true;
      backtrack(arrangement, index + 1, used, i);
      used[i] = false;
    }

  }

}