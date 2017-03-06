/**
 * https://open.kattis.com/problems/radar
 * Author: Micah Stairs
 * Solved On: March 5, 2017
 * 
 * This problem can be clearly identified as an application of the Chinese
 * Remainder Theorem. However, in order to deal with these ranges, we must
 * apply the theorem many times, trying each combination of t values.
 *
 * In order to make the solution fast enough, we can analyze the algorithm
 * and pull out and pre-compute various pieces of it, since we are running
 * the algorithm many times with very similar sets of values.
 *
 * In order to avoid overflow we must resort to the BigInteger class, since
 * intermediate calculations can overflow a long.
 **/

import java.util.*;
import java.io.*;
import java.math.*;

public class Radar {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read in wavelengths
    String[] line = br.readLine().split(" ");
    long m1 = Long.parseLong(line[0]);
    long m2 = Long.parseLong(line[1]);
    long m3 = Long.parseLong(line[2]);

    // Read in measurements
    line = br.readLine().split(" ");
    long x1 = Long.parseLong(line[0]);
    long x2 = Long.parseLong(line[1]);
    long x3 = Long.parseLong(line[2]);

    // Read in accuracies
    line = br.readLine().split(" ");
    int y1 = Integer.parseInt(line[0]);
    int y2 = Integer.parseInt(line[1]);
    int y3 = Integer.parseInt(line[2]);

    // Compute needed modular inverses
    long m = m1 * m2 * m3;
    BigInteger mod = BigInteger.valueOf(m);
    long inv1 = modInv(m / m1, m1);
    long inv2 = modInv(m / m2, m2);
    long inv3 = modInv(m / m3, m3);

    // Pre-compute each part of the CRT answers
    long[] z1 = new long[1 + y1 * 2];
    BigInteger tmp1 = BigInteger.valueOf(m / m1).multiply(BigInteger.valueOf(inv1)).mod(mod);
    for (int i = 0; i < z1.length; i++) {
      z1[i] = tmp1.multiply(BigInteger.valueOf(x1 - (i - y1))).mod(mod).longValue();
    }
    long[] z2 = new long[1 + y2 * 2];
    BigInteger tmp2 = BigInteger.valueOf(m / m2).multiply(BigInteger.valueOf(inv2)).mod(mod);
    for (int i = 0; i < z2.length; i++) {
      z2[i] = tmp2.multiply(BigInteger.valueOf(x2 - (i - y2))).mod(mod).longValue();
    }
    long[] z3 = new long[1 + y3 * 2];
    BigInteger tmp3 = BigInteger.valueOf(m / m3).multiply(BigInteger.valueOf(inv3)).mod(mod);
    for (int i = 0; i < z3.length; i++) {
      z3[i] = tmp3.multiply(BigInteger.valueOf(x3 - (i - y3))).mod(mod).longValue();
    }

    // Find smallest answer (combining pre-computed partial results of CRT)
    long min = Long.MAX_VALUE;
    for (int i = 0; i < z1.length; i++) {
      for (int j = 0; j < z2.length; j++) {
        for (int k = 0; k < z3.length; k++) {
          long z = z1[i] + z2[j] + z3[k];
          while (z >= m) z -= m;
          if (z < min) min = z;
        }
      }
    }

    // Output answer
    System.out.println(min);
  
  }

  // Used to compute modular inverse
  static long modInv(long x, long m) {
    return (egcd(x, m)[1] + m) % m;
  }
  static long[] egcd(long a, long b) {
    if (b == 0) return new long[] { a, 1, 0 };
    else {
      long[] ret = egcd(b, a % b);
      long tmp = ret[1] - ret[2] * (a / b);
      ret[1] = ret[2]; ret[2] = tmp; return ret;
    }
  }

}