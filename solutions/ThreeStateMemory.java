/**
 * Problem Link: https://open.kattis.com/problems/memory
 * Author: Micah Stairs
 * Solved On: January 18, 2017
 *
 * I initially discussed this problem with William Fiset and Finn Lidbetter,
 * however we did not come to a complete solution at the time, although we
 * had a vague idea of how the DP solution would be structured.
 *
 * One important insight to recognize is that if we can shift 1's over to get
 * 2's in the spot to the right, but we can't shift anything over unless there
 * is a 0 there. This means a bitstring like "10000" can be transformed like this:
 * "10000" -> "02000" -> "01200" -> "01120" -> "01112". Notice how we get these
 * leading 1's? This prevents other 1's more than one index past us.
 *
 * So in this solution, we are constructing a 2D dynamic programming table, where
 * the rows represent the suffix length of the bitstring and the columns
 * represent where the leading zeroes would be. This DP table is counting how many
 * bitstrings can be constructed in this new representation where we have only
 * considered a certain suffix of the initial bitsring and we know exactly where the
 * least significant is. In order to compute the next row, we need to keep track of
 * a sum of the previous row and the sum of that sum. If we have a zero, we just copy
 * previous row down, since nothing changes (since 1101 and 01101 have the same
 * number of answers, for example).
 *
 * Although this solution is O(n^2) in both space and time, it passes just fine.
 * However it can be optimized further. We can reduce it to linear space by only
 * maintaining 2 rows of the DP table at a time, which is a common trick used.
 * This is the solution you actually see below.
 *
 * This solution could be optimized even further, however. In each row of our table,
 * it turns out we only ever have up to 2 non-zero values, which means we could just
 * use O(1) space to represent these values. This, of course, would make the
 * implementation a bit more conflicted. Taking this a step further, however, I
 * believe it would even be possible to achieve O(n) time if we did some multiplicaton,
 * as opposed to O(n^2) time.
 **/

import java.util.*;
import java.io.*;

public class ThreeStateMemory {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  static final long MOD = 1_000_000_009L;

  public static void main(String[] args) throws IOException {

    // Get input
    char[] arr = reverse(br.readLine()).toCharArray();
    int n = arr.length;

    // Only maintain 2 rows of the DP table at a time
    long[] prev = new long[n+1];
    long[] cur = new long[n+1];

    // Initialize DP
    if (arr[0] == '1') prev[1] = 1;
    else prev[0] = 1;

    // Used for an optimization so that we do not go over empty portions of the table
    int firstIndex = 0;
    
    // Process each row
    for (int i = 1; i < n; i++) {

      // We don't need to do anything for 0's since we would be just duplicating
      // the previous row
      if (arr[i] == '1') {

        // Keep track of the sum seen so far in the previous row
        long sum = 0;

        // Also keep track of the sum of sums seen so far
        long sumOfSums = 0;

        // Compute each needed cell in this row of the DP table
        for (int j = firstIndex; j <= i + 1; j++) {

          // Update sum
          sum += prev[j];
          if (sum >= MOD) sum -= MOD;

          // Notice how each row will have at most 2 non-zero values, we could
          // optimize this soution to only use O(1) space for our DP table
          if (j == i) { cur[j] = sumOfSums; firstIndex = j; }
          else if (j == i + 1) cur[j] = sum;
          else cur[j] = 0;

          // Update sum of sums
          sumOfSums += sum;
          if (sumOfSums >= MOD) sumOfSums -= MOD;
          
          // Copy row back so that arrays can be re-used
          prev[j] = cur[j];

        }

      }

    }

    // Answer is the sum of the last row
    long sum = 0;
    for (int i = 0; i <= n; i++) {
      sum += prev[i];
      if (sum >= MOD) sum -= MOD;
    }

    // Output answer
    System.out.println(sum);
  
  }

  // Reverse the string
  static String reverse(String str) {
    return (new StringBuilder(str)).reverse().toString();
  }

}