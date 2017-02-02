/**
 * https://open.kattis.com/problems/srednji
 * Author: Micah Stairs
 * Solved On: February 1, 2017
 * 
 * The trick to solving this problem is to start by simplifying the input.
 * If B is our median, then all we care about is whether a number is larger
 * or smaller than B. We want to count subsequence the number of subsequences
 * which include B that have the same number of smaller and larger numbers.
 *
 * So we can represent the input as with a 0 for the median, a -1 for numbers
 * smaller than the median, and a 1 if it is larger. Starting at the index
 * which contains B, we spread outwards (considering each side separately).
 * Keeping a running sum, we keep a count of each sum encountered on each side.
 * For example, if we have a two instances of -2 on the left and three instances
 * of 2 on the right, then this gives us 2*3=6 subsequences. We sum all similar
 * products to get our final answer.
 **/

import java.util.*;
import java.io.*;

public class Srednji {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read first line of input
    String[] line = br.readLine().split(" ");
    int n = Integer.parseInt(line[0]);
    int b = Integer.parseInt(line[1]);

    // Simplify the input (0 represents the median, -1 means the number is
    // smaller than the median, 1 means it is larger)
    line = br.readLine().split(" ");
    int[] arr = new int[n];
    int center = -1;
    for (int i = 0; i < n; i++) {
      int val = Integer.parseInt(line[i]);
      if (val == b) center = i;
      else if (val < b) arr[i] = -1;
      else arr[i] = 1;
    }

    // Try starting from the median and going outwards, summing the -1's and +1's
    final int OFFSET = n;
    int[] counts1 = new int[n * 2 + 1];
    int sum = 0;
    for (int i = center; i >= 0; i--) {
      sum += arr[i];
      counts1[OFFSET + sum]++;
    }
    int[] counts2 = new int[n * 2 + 1];
    sum = 0;
    for (int i = center; i < n; i++) {
      sum += arr[i];
      counts2[OFFSET + sum]++;
    }

    // Use computed information to count the number of subsequences A
    // whose median is B
    long total = 0;
    for (int i = 0; i <= n; i++) {
      int negative = OFFSET - i;
      int positive = OFFSET + i;
      total += counts1[negative] * counts2[positive];
      if (negative != positive) {
        total += counts1[positive] * counts2[negative];
      }
    }

    // Output answer
    System.out.println(total);
  
  }

}