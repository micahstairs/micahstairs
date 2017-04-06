/**
 * https://open.kattis.com/problems/delivery
 * Author: Micah Stairs
 * Solved On: April 6, 2017
 * 
 * We can use a greedy approach to solve this problem. We can break apart and
 * solve the negative X values separately from the positive X values since the
 * truck would need to pass by the origin anyway to go from one side to the other
 * and it might as well refill with letters. We can process these stops starting
 * from the furthest from the origin and working back to the origin. At each stop
 * we compute the number of full trips we need and then with whatever leftover space
 * we have left, we deliver the next closest letters at the same time (since this
 * adds nothing to the distance).
 **/

import java.util.*;
import java.io.*;

public class PostalDelivery {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read and store input
    String[] line = br.readLine().split(" ");
    int n = Integer.parseInt(line[0]);
    int k = Integer.parseInt(line[1]);
    int[] x = new int[n];
    int[] t = new int[n];
    for (int i = 0; i < n; i++) {
      line = br.readLine().split(" ");
      x[i] = Integer.parseInt(line[0]);
      t[i] = Integer.parseInt(line[1]);
    }

    long total = 0;

    // Take care of negative values
    for (int i = 0; i < n && x[i] < 0; i++) {
      if (t[i] > 0) {
        int distPerTrip = x[i] * -2;
        int nFullTrips = t[i] / k;
        total += distPerTrip * nFullTrips;
        t[i] %= k;
        if (t[i] > 0) {
          total += distPerTrip;
          int leftover = k - t[i];
          t[i] = 0;
          for (int j = i + 1; j < n && leftover > 0 && x[j] < 0; j++) {
            int amountWhichCanBeUsed = Math.min(leftover, t[j]);
            leftover -= amountWhichCanBeUsed;
            t[j] -= amountWhichCanBeUsed;
          }
        }
      }
    }

    // Take care of positive values
    for (int i = n - 1; i >= 0 && x[i] > 0; i--) {
      if (t[i] > 0) {
        int distPerTrip = x[i] * 2;
        int nFullTrips = t[i] / k;
        total += distPerTrip * nFullTrips;
        t[i] %= k;
        if (t[i] > 0) {
          total += distPerTrip;
          int leftover = k - t[i];
          t[i] = 0;
          for (int j = i - 1; j >= 0 && leftover > 0 && x[j] > 0; j--) {
            int amountWhichCanBeUsed = Math.min(leftover, t[j]);
            leftover -= amountWhichCanBeUsed;
            t[j] -= amountWhichCanBeUsed;
          }
        }
      }
    }

    // Output answer
    System.out.println(total);
  
  }

}