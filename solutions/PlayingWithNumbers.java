/**
 * https://open.kattis.com/problems/playingwithnumbers
 * Author: Micah Stairs
 * Solved On: February 25, 2017
 * 
 * Finn Lidbetter and I worked out the solution for this problem together. By working
 * out some examples by hand and making a few observations, we realized there are only
 * a few distinct numbers that can actually be present in the output. This led to the 
 * discovery of a simple linear solution.
 **/

import java.util.*;
import java.io.*;

public class PlayingWithNumbers {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read input size
    int n = Integer.parseInt(br.readLine());

    // Read and store input, keeping track of min/max values
    int[] a = new int[n];
    int[] b = new int[n];
    int minA = Integer.MAX_VALUE;
    int minB = Integer.MAX_VALUE;
    int maxA = Integer.MIN_VALUE;
    int maxB = Integer.MIN_VALUE;
    for (int i = 0; i < n; i++) {
      String[] line = br.readLine().split(" ");
      a[i] = Integer.parseInt(line[0]);
      b[i] = Integer.parseInt(line[1]);
      if (a[i] < minA) minA = a[i];
      if (b[i] < minB) minB = b[i];
      if (a[i] > maxA) maxA = a[i];
      if (b[i] > maxB) maxB = b[i];
    }

    // Find the top 2 extrema for both A's and B's
    int lowestA = Integer.MAX_VALUE;
    int secondLowestA = Integer.MAX_VALUE;
    int highestA = Integer.MIN_VALUE;
    int secondHighestA = Integer.MIN_VALUE;
    int lowestB = Integer.MAX_VALUE;
    int secondLowestB = Integer.MAX_VALUE;
    int highestB = Integer.MIN_VALUE;
    int secondHighestB = Integer.MIN_VALUE;
    for (int i = 0; i < n; i++) {
      if (a[i] < lowestA) { secondLowestA = lowestA; lowestA = a[i]; }
      else if (a[i] < secondLowestA) secondLowestA = a[i];
      if (b[i] < lowestB) { secondLowestB = lowestB; lowestB = b[i]; }
      else if (b[i] < secondLowestB) secondLowestB = b[i];
      if (a[i] > highestA) { secondHighestA = highestA; highestA = a[i]; }
      else if (a[i] > secondHighestA) secondHighestA = a[i];
      if (b[i] > highestB) { secondHighestB = highestB; highestB = b[i]; }
      else if (b[i] > secondHighestB) secondHighestB = b[i];
    }

    // Try to maximize (for first two columns)
    int value1 = 0;
    int value2 = 0;
    for (int i = 0; i < n; i++) {
      int minOfOtherA = lowestA == a[i] ? secondLowestA : lowestA;
      int result1 = a[i] > minOfOtherA ? a[i] : minOfOtherA;
      int minOfOtherB = lowestB == b[i] ? secondLowestB : lowestB;
      int result2 = b[i] > minOfOtherB ? b[i] : minOfOtherB;
      if (i == 0 || cmp(result1, result2, value1, value2) > 0) {
        value1 = result1;
        value2 = result2;
      }
    }

    // Try to minimize (for last two columns)
    int value3 = 0;
    int value4 = 0;
    for (int i = 0; i < n; i++) {
      int maxOfOtherA = highestA == a[i] ? secondHighestA : highestA;
      int result3 = a[i] < maxOfOtherA ? a[i] : maxOfOtherA;
      int maxOfOtherB = highestB == b[i] ? secondHighestB : highestB;
      int result4 = b[i] < maxOfOtherB ? b[i] : maxOfOtherB;
      if (i == 0 || cmp(result3, result4, value3, value4) < 0) {
        value3 = result3;
        value4 = result4;
      }
    }

    // Output answers
    for (int i = 0; i < n; i++) {
      int ans1 = i == n - 1 ? minA : i == n - 2 ? value1 : maxA;
      int ans2 = i == n - 1 ? minB : i == n - 2 ? value2 : maxB;
      int ans3 = i == 0 ? maxA : i == 1 ? value3 : minA;
      int ans4 = i == 0 ? maxB : i == 1 ? value4 : minB;
      sb.append(ans1 + " " + ans2 + " " + ans3 + " " + ans4 + "\n");
    }
    System.out.print(sb);
  
  }

  // Compare 2^(a1) * 3^(b1) and 2^(a2) * 3^(b2)
  static final double LOG_2 = Math.log(2);
  static final double LOG_3 = Math.log(3);
  static int cmp(int a1, int b1, int a2, int b2) {
    if (a1 <= a2 && b1 <= b2) return -1;
    if (a1 >= a2 && b1 >= b2) return 1;
    double val1 = a1 * LOG_2 + b1 * LOG_3;
    double val2 = a2 * LOG_2 + b2 * LOG_3;
    return val1 < val2 ? -1 : 1;
  }

}