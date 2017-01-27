/**
 * https://open.kattis.com/problems/coke
 * Author: Micah Stairs
 * Solved On: January 27, 2017
 *
 * I initially tackled this problem last year, but I was never able to actually
 * get it. After talking about it with Michael Bradet-Legris, he pointed out a
 * case that I was missing. With this in hand, I was able to get a passing solution.
 *
 * Although there might be a greedy solution, I opted for the safer DP approach.
 * In order to reduce the amount of memory needed to store the answers, I took
 * advantage of the fact that if you know the total amount of money that is in
 * play, then you only need 3 of out of 4 variables and you can then deduce the
 * last one. For example, if you know how many cokes you still need to buy, and
 * how many 5's and 10's you have, you can compute the number of 1's you must have.
 * This reduces the amount of memory needed to a manageable amount. The only
 * disadvantage is that we cannot re-use these memoized answers for other test cases
 * since the total amount of money in play could change.
 **/

import java.util.*;
import java.io.*;

public class BuyingCoke {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static final int BLANK = -1;
  static int[][][] memo = new int[151][151][51];

  static int totalMoney;
  
  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.valueOf(br.readLine());
    while (t-- > 0) {

      // Reset memo
      for (int[][] arr1 : memo) {
        for (int[] arr2 : arr1) {
          Arrays.fill(arr2, BLANK);
        }
      }

      // Read input
      String[] line = br.readLine().split(" ");
      int c = Integer.valueOf(line[0]);
      int n1 = Integer.valueOf(line[1]);
      int n5 = Integer.valueOf(line[2]);
      int n10 = Integer.valueOf(line[3]);

      // Store the total amount of money
      totalMoney = n1 + n5 * 5 + n10 * 10;

      // Compute and output answer
      sb.append(minCoins(c, n5, n10) + "\n");

    }
  }

  // Recursively compute the minimum number of counts which need to be inserted with memoization
  static int minCoins(int nCokes, int n5, int n10) {

    // Base case
    if (nCokes == 0) return 0;

    // Determine how many 1's we must have
    int n1 = totalMoney - n5 * 5 - n10 * 10;

    // Reduce the state, if possible
    n1 = Math.min(nCokes * 8, n1);
    n5 = Math.min(nCokes * 2, n5);
    n10 = Math.min(nCokes, n10);

    // Check to see if this state has already been solved
    if (memo[nCokes][n5][n10] != BLANK) return memo[nCokes][n5][n10];

    // All 1's
    int min = nCokes * 8;

    // 10 -> 1 1
    if (n10 > 0)
      min = Math.min(min, 1 + minCoins(nCokes - 1, n5, n10 - 1));

    // 5 1 1 1 -> 0
    if (n5 >= 1 && n1 >= 3)
      min = Math.min(min, 4 + minCoins(nCokes - 1, n5 - 1, n10));

    // 5 5 -> 1 1
    if (n5 >= 2)
      min = Math.min(min, 2 + minCoins(nCokes - 1, n5 - 2, n10));

    // 1 1 1 1 1 1 1 1 -> 0
    if (n1 >= 8)
      min = Math.min(min, 8 + minCoins(nCokes - 1, n5, n10));

    // 10 1 1 1 -> 5
    if (n10 >= 1 && n1 >= 3)
      min = Math.min(min, 4 + minCoins(nCokes - 1, n5 + 1, n10 - 1));

    // Store and return result
    return memo[nCokes][n5][n10] = min;

  }

}