/**
 * https://open.kattis.com/problems/figurinefigures
 * Author: Micah Stairs
 * Solved On: April 1, 2017
 * 
 * In this problem, we are asked to output 4 different values. Two of these
 * values (the minimum and maximum) are very easy to compute. The mean is
 * also straight-forward to compute once a trick is found. It turns out that
 * all we need to do is divide the sum of the numbers by n (which gives us the
 * average number) and then multiply by 4 (since we always choose 4 numbers).
 * Computing the number of unique sums can be done using FFT. This is accomplished
 * by taking the polynomial and putting it to the power of 4.
 **/

import java.util.*;
import java.io.*;

public class FigurineFigures {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read input
    int n = Integer.parseInt(br.readLine());
    String[] split = br.readLine().split(" ");
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = Integer.parseInt(split[i]);
    }

    // Find the minimum, maximimum, and sum of the numbers, and build the polynomial for FFT
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    double sum = 0;
    int[] poly = new int[60001];
    for (int i = 0; i < n; i++) {
      min = Math.min(min, arr[i]);
      max = Math.max(max, arr[i]);
      sum += arr[i];
      poly[arr[i]]++;
    }

    // Use FFT to count the number of unique sums we can get from adding exactly 4 numbers together
    int[] square = FFT.multiply(poly, poly);
    int[] quad = FFT.multiply(square, square);
    int nUnique = 0;
    for (int i : quad) {
      if (i > 0) nUnique++;
    }

    // Finishing computing answer and then output it
    System.out.printf("%d %d %d %f\n", max * 4, min * 4, nUnique, sum * (4.0 / n));
  
  }

}

// Fast Fourier Transform snippet
class FFT {
  static final int MOD = 2_013_265_921, ROOT = 137, ROOT_INV = 749_463_956;
  static int[] multiply(int[] a, int[] b) {
    int minN = a.length - 1 + b.length, logN = 0;
    while ((1 << logN) < minN) logN++;
    int[] tA = transform(a, logN, ROOT), tB = transform(b, logN, ROOT);
    int[] tC = tA;
    for (int j = 0; j < tC.length; j++) tC[j] = addMult(0, tA[j], tB[j]);
    int[] nC = transform(tC, logN, ROOT_INV), c = new int[minN];
    int nInverse = MOD - ((MOD - 1) >>> logN);
    for (int j = 0; j < c.length; j++) c[j] = addMult(0, nInverse, nC[j]);
    return c;
  }
  static int addMult(int x, int y, int z) {
    return (int) ((x + y * (long) z) % MOD);
  }
  static int[] transform(int[] a, int logN, int primRoot) {
    int[] tA = new int[1 << logN];
    for (int j = 0; j < a.length; j++) {
      int k = j << (32 - logN);
      k = ((k >>> 1) & 0x55555555) | ((k & 0x55555555) << 1);
      k = ((k >>> 2) & 0x33333333) | ((k & 0x33333333) << 2);
      k = ((k >>> 4) & 0x0f0f0f0f) | ((k & 0x0f0f0f0f) << 4);
      k = ((k >>> 8) & 0x00ff00ff) | ((k & 0x00ff00ff) << 8);
      tA[(k >>> 16) | (k << 16)] = a[j];
    }
    int[] root = new int[27];
    root[root.length - 1] = primRoot;
    for (int i = root.length - 1; i > 0; i--) {
      root[i-1] = addMult(0, root[i], root[i]);
    }
    for (int i = 0; i < logN; i++) {
      int twiddle = 1;
      for (int j = 0; j < (1 << i); j++) {
        for (int k = j; k < tA.length; k += 2 << i) {
          int x = tA[k], y = tA[k + (1 << i)];
          tA[k] = addMult(x, twiddle, y);
          tA[k + (1 << i)] = addMult(x, MOD - twiddle, y);
        }
        twiddle = addMult(0, root[i], twiddle);
      } 
    }
    return tA;
  }
}