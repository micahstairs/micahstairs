/**
 * https://open.kattis.com/problems/genius
 * Author: Micah Stairs
 * Solved On: April 1, 2017
 * 
 * This is a pretty straight forward probability problem that can be solved using
 * dynamic programming. To simplify some of my code I take advantage of the XOR
 * bit operation, allowing me to easily hop around the tournament tree.
 **/

import java.util.*;
import java.io.*;

public class Genius {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read input and generate sequence
    String[] line = br.readLine().split(" ");
    int k = Integer.parseInt(line[0]);
    int t = Integer.parseInt(line[1]);
    int p = Integer.parseInt(line[2]);
    int q = Integer.parseInt(line[3]);
    int[] x = new int[t];
    x[0] = Integer.parseInt(line[4]);
    for (int i = 1; i < t; i++) {
      x[i] = (x[i-1] * p) % q;
    }
    for (int i = 0; i < t; i++) {
      x[i] %= 4;
    }
    int[][] w = new int[t][4];
    for (int i = 0; i < t; i++) {
      line = br.readLine().split(" ");
      for (int j = 0; j < 4; j++) {
        w[i][j] = Integer.parseInt(line[j]);
      }
    }

    // Use iterative DP to fill a table (prob[i][j] represents the probability of getting i out of j correct guesses)
    double[][] prob = new double[t+1][t+1];
    prob[0][0] = 1.0;
    for (int i = 0; i < t; i++) {
      int predictedWinner = x[i];
      int a = w[i][x[i]];
      int b = w[i][x[i]^1];
      int c = w[i][x[i]^2];
      int d = w[i][x[i]^3];
      double probabilityOfSuccess = p(a, b) * (p(c, d) * p(a, c) + p(d, c) * p(a, d));
      double probabilityOfFailure = 1.0 - probabilityOfSuccess;
      for (int j = 0; j <= i; j++) {
        prob[j+1][i+1] += probabilityOfSuccess * prob[j][i];
        prob[j][i+1] += probabilityOfFailure * prob[j][i];
      }
    }

    // Sum probability of getting at least k/t right
    double total = 0;
    for (int i = k; i <= t; i++) {
      total += prob[i][t];
    }

    // Output answer
    System.out.println(total);
  
  }

  // Find probability that the first one will beat the second one
  static double p(int a, int b) {
    return a / (double) (a + b);
  }

}