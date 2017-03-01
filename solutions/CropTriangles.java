/**
 * https://open.kattis.com/problems/cropeasy, https://open.kattis.com/problems/crophard
 * Author: Micah Stairs
 * Solved On: March 1, 2017
 * 
 * I worked on this problem with Finn Lidbetter. The main insight he gave me was
 * that we could take all of the coordinates are mod them by 3, giving only 9
 * distinct groups of coordinates to handle.
 *
 * At this point it becomes a simpler counting problem. We have 3 different cases.
 * We either take all 3 points from the same group, 2 points from the same group
 * or we take each point from a different group. My initial solution had a bug in
 * it so I wrote a brute force implementation and discovered what the bug was in
 * doing this. In the first two cases, I was double-counting (permutations of the
 * same points still make the same triangle). By dividing these cases by 3! and 2!
 * we eliminate this double-counting.
 **/

import java.util.*;
import java.io.*;

public class CropTriangles {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.parseInt(br.readLine());
    for (int testCase = 1; testCase <= t; testCase++) {

      // Read input
      String[] line = br.readLine().split(" ");
      int n = Integer.parseInt(line[0]);
      long a = Long.parseLong(line[1]);
      long b = Long.parseLong(line[2]);
      long c = Long.parseLong(line[3]);
      long d = Long.parseLong(line[4]);
      long x0 = Long.parseLong(line[5]);
      long y0 = Long.parseLong(line[6]);
      long m = Long.parseLong(line[7]);

      // Generate points
      long[] x = new long[n];
      long[] y = new long[n];
      x[0] = x0;
      y[0] = y0;
      for (int i = 1; i < n; i++) {
        x[i] = (a * x[i-1] + b) % m;
        y[i] = (c * y[i-1] + d) % m;
      }

      // Case 1: Group points by modding the coordinates by 3
      long[][] counts = new long[3][3];
      for (int i = 0; i < n; i++) {
        int xRem = (int) (x[i] % 3L);
        int yRem = (int) (y[i] % 3L);
        counts[yRem][xRem]++;
      }

      long answer = 0;

      // Case 2: Take 3 points from the same group
      for (int i = 0; i < 9; i++) {
        int xRem = i % 3;
        int yRem = i / 3;
        if (counts[yRem][xRem] >= 3) {
          answer += (counts[yRem][xRem] * (counts[yRem][xRem] - 1) * (counts[yRem][xRem] - 2)) / 6;
        }
      }

      // Case 3: Take 2 points from the same group
      for (int i = 0; i < 9; i++) {
        int xIndex1 = i % 3;
        int yIndex1 = i / 3;
        if (counts[yIndex1][xIndex1] >= 2) {
          for (int j = i + 1; j < 9; j++) {
            int xIndex2 = j % 3;
            int yIndex2 = j / 3;
            if ((xIndex1 * 2 + xIndex2) % 3 != 0) continue;
            if ((yIndex1 * 2 + yIndex2) % 3 != 0) continue;
            answer += (counts[yIndex1][xIndex1] * (counts[yIndex1][xIndex1] - 1) * counts[yIndex2][xIndex2]) / 2;
          }
        }
      }

      // Take points from separate groups
      for (int i = 0; i < 9; i++) {
        int xIndex1 = i % 3;
        int yIndex1 = i / 3;
        for (int j = i + 1; j < 9; j++) {
          int xIndex2 = j % 3;
          int yIndex2 = j / 3;
          for (int k = j + 1; k < 9; k++) {
            int xIndex3 = k % 3;
            int yIndex3 = k / 3;
            if ((xIndex1 + xIndex2 + xIndex3) % 3 != 0) continue;
            if ((yIndex1 + yIndex2 + yIndex3) % 3 != 0) continue;
            answer += counts[yIndex1][xIndex1] * counts[yIndex2][xIndex2] * counts[yIndex3][xIndex3];
          }
        }
      }

      // Format answer
      sb.append(String.format("Case #%d: %d\n", testCase, answer));

    }

    // Output answers
    System.out.print(sb);
  
  }

}