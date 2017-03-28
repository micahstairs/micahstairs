/**
 * https://open.kattis.com/problems/dinner
 * Author: Micah Stairs
 * Solved On: March 27, 2017
 * 
 * This is a pretty straightforward problem and backtracking seemed like a nice
 * way to tackle it, but I was afraid that the input size was too large. However,
 * it did seem as though a lot of pruning would take place so I decided to attempt
 * it anyway. Fortunately this solution was fast enough!
 **/

import java.util.*;
import java.io.*;

public class Dinner {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static final int FIRST_YEAR = 1948;
  static final int LAST_YEAR = 2008;

  static int n;
  static double maxGroupSize;
  static int[][] yearMet;

  public static void main(String[] args) throws IOException {

    // Read input size
    String[] line = br.readLine().split(" ");
    n = Integer.parseInt(line[0]);
    int c = Integer.parseInt(line[1]);

    // Compute the largest that a group can be
    maxGroupSize = (double) n * (2.0 / 3.0);

    // Store the years that each pair met
    yearMet = new int[n][n];
    for (int i = 0; i < n; i++) Arrays.fill(yearMet[i], LAST_YEAR);
    for (int i = 0; i < c; i++) {
      line = br.readLine().split(" ");
      int a = Integer.parseInt(line[0]) - 1;
      int b = Integer.parseInt(line[1]) - 1;
      int y = Integer.parseInt(line[2]);
      yearMet[a][b] = yearMet[b][a] = y;
    }

    // Try each year, stopping once we found a division which worked with that year
    for (int y = FIRST_YEAR; y <= LAST_YEAR; y++) {
      if (canDivide(y, 0, 0, 0, new int[n])) {
        System.out.println(y);
        return;
      }
    }

    // Indicate that it was impossible
    System.out.println("Impossible");
  
  }

  // Use recursive backtracking to determine if a division exists
  static boolean canDivide(int year, int index, int size1, int size2, int[] groups) {

    // One of the groups exceeded the maximum size
    if (size1 > maxGroupSize || size2 > maxGroupSize) return false;

    // All people have been processed
    if (index == n) return true;

    // Try putting it in the first group
    boolean canPutInFirstGroup = true;
    for (int i = 0; i < index; i++) {
      if (groups[i] == 1 && yearMet[i][index] >= year) {
        canPutInFirstGroup = false;
        break;
      }
    }
    if (canPutInFirstGroup) {
      groups[index] = 1;
      if (canDivide(year, index + 1, size1 + 1, size2, groups)) return true;
      groups[index] = 0;
    }

    // Try putting it in the second group
    boolean canPutInSecondGroup = true;
    for (int i = 0; i < index; i++) {
      if (groups[i] == 2 && yearMet[i][index] < year) {
        canPutInSecondGroup = false;
        break;
      }
    }
    if (canPutInSecondGroup) {
      groups[index] = 2;
      if (canDivide(year, index + 1, size1, size2 + 1, groups)) return true;
      groups[index] = 0;
    }

    // Indicate it was impossible
    return false;

  }

}