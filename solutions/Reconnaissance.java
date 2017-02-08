/**
 * https://open.kattis.com/problems/reconnaissance
 * Author: Micah Stairs
 * Solved On: February 8, 2017
 * 
 * Initially William Fiset spent a while working on this problem with Finn Lidbetter.
 * I suggested that a ternary search might work, but we were not able to convince
 * ourselves that the function of the window size over size would be strictly
 * decreasing then strictly increasing.
 *
 * Later, with Finn Lidbetter, we were able to convince outselves that a ternary
 * search should work. You see, the right endpoint of this window never moves to
 * the left after it has started moving to the right. Similarly, the left endpoint
 * never moves to the right after it has started moving to the left. We can then 
 * justify that this function does not decrease after it begins to increase. This
 * function could possibly be flat in places (and therefore not strictly increasing
 * or strictly decreasing), but we believe this only happens at the minimum
 * anyway, and therefore will not mess up the ternary search.
 **/

import java.util.*;
import java.io.*;

public class Reconnaissance {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  static int n;
  static int[] x, v;

  public static void main(String[] args) throws IOException {

    // Read and store the input
    n = Integer.parseInt(br.readLine());
    x = new int[n];
    v = new int[n];
    for (int i = 0; i < n; i++) {
      String[] line = br.readLine().split(" ");
      x[i] = Integer.parseInt(line[0]);
      v[i] = Integer.parseInt(line[1]);
    }
    
    // Use ternary search to find answer and then output it
    System.out.printf("%.4f\n", ternarySearch(0, 200001));

  }

  // Ternary search a function which decreases and then increases
  static double ternarySearch(double low, double high) {
    Double prev = null;
    while (true) {
      double mid1 = (2*low + high)/3;
      double mid2 = (low + 2*high)/3;
      double result1 = function(mid1);
      double result2 = function(mid2);
      if (result1 > result2) low = mid1;
      else high = mid2;
      if (prev != null && Math.abs(prev - result1) < 0.000000001) break;
      prev = result1;
    }
    return prev;
  }

  // Compute width of window at a particular point in time
  static double function(double time) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < n; i++) {
      double pos = getPos(time, i);
      if (pos < min) min = pos;
      if (pos > max) max = pos;
    }
    return max - min;
  }

  // Get the position of a vehicle at a particular point in time
  static double getPos(double time, int index) {
    return x[index] + time * v[index];
  }

}