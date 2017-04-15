/**
 * https://open.kattis.com/problems/linearrecurrence
 * Author: Micah Stairs
 * Solved On: April 14, 2017
 * 
 * I solved this problem by modifying a snippet that William Fiset found and 
 * modified. I needed to modify his snippet in order to handle the modular
 * arithmetic. I got WA on my first submission because I was not properly
 * ensuring that the result was positive.
 **/

import java.util.*;
import java.io.*;

public class LinearRecurrences {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read in the size
    int n = Integer.parseInt(br.readLine());
    
    // Read in the recurrence
    String[] line = br.readLine().split(" ");
    long[] coeffs = new long[n];
    long k = Long.parseLong(line[0]);
    for (int i = 0; i < n; i++) {
      coeffs[i] = Long.parseLong(line[i+1]);
    }

    // Read in the initial values
    line = br.readLine().split(" ");
    long[] initialValues = new long[n];
    for (int i = 0; i < n; i++) {
      initialValues[i] = Long.parseLong(line[i]);
    }    

    // Process queries
    int q = Integer.parseInt(br.readLine());
    while (q-- > 0) {
      line = br.readLine().split(" ");
      long t = Long.parseLong(line[0]);
      long m = Long.parseLong(line[1]);
      sb.append(LinearRecurrenceSolver.solveRecurrence(coeffs, initialValues, k, t, m) + "\n");
    }
    
    // Output answers
    System.out.print(sb);
  
  }

}

// A snippet used to efficiently find a specific term in a sequence generated using linear recurrences
class LinearRecurrenceSolver {
  static long solveRecurrence(long[] coeffs, long[] initialValues, long k, long n, long mod) {
    if (n < initialValues.length) return initialValues[(int) n] % mod;
    int size = initialValues.length + 1;
    long[][] result = matrixPower(getTransform(coeffs, size, mod), n, mod);
    long ans = 0L;
    for (int j = 0; j < size; j++) {
      if (j == size - 1) ans = (ans + result[0][j] * k) % mod;
      else ans = (ans + result[0][j] * initialValues[j]) % mod;
    }
    return ((ans % mod) + mod) % mod;
  }
  static long[] computeInitialValues(long[] coeffs, long f_0, long k, long mod) {
    int len = coeffs.length;
    long[] dp = new long[len];
    dp[0] = f_0;
    for (int n = 1; n < len; n++) {
      for (int i = 1; i <= n; i++) dp[n] = (dp[n] + dp[n-i] * coeffs[i-1]) % mod;
      dp[n] = (dp[n] + k) % mod;
    }
    return dp;
  }
  static long[][] matrixPower(long[][] matrix, long n, long mod) {
    int len = matrix.length;
    long[][] result = null;
    if (n == 0) {
      result = new long[len][len];
      for (int i = 0; i < len; i++) result[i][i] = 1L;
    } else {
      long[][] arr = deepCopy(matrix);
      while (n > 0) {
        if ((n & 1L) == 1L) {
          result = (result == null ? deepCopy(arr) : squareMatrixMult(result, arr, mod));
        }
        arr = squareMatrixMult(arr, arr, mod);
        n >>= 1L;
      }
    }
    return result;
  }
  static long[][] squareMatrixMult(long[][] a, long[][] b, long mod) {
    int len = a.length;
    long[][] c = new long[len][len];
    for (int i = 0; i < len; i++)
      for (int j = 0; j < len; j++)
        for (int k = 0; k < len; k++)
          c[i][j] = (c[i][j] + a[i][k] * b[k][j]) % mod;
    return c;
  }
  static long [][] getTransform(long[] coeffs, int sz, long mod) {
    long[][] t = new long[sz][sz];
    for (int i = 0; i+1 < sz; i++) t[i][i+1] = 1L;
    for (int i = 0; i < sz-1; i++) t[sz-2][i] = coeffs[coeffs.length-i-1] % mod;
    t[sz-1][sz-1] = t[sz-2][sz-1] = 1L;
    return t;
  }
  static long[][] deepCopy(long[][] arr) {
    final int len = arr.length;
    long[][] copy = new long[len][len];
    for (int i = 0; i < len; i++) copy[i] = arr[i].clone();
    return copy;
  }
}