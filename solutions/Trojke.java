/**
 * https://open.kattis.com/problems/trojke
 * Author: Micah Stairs
 * Solved On: April 17, 2017
 * 
 * The key insight to solving this problem is realizing that we are only given up to
 * 26 letters, regardless of the the of the grid. For each ordered pair of letters, we
 * can then iterate through the grid and look for possible letters that form a line with
 * the pair. We can do this efficently by looking at the change in x and y positions
 * between the pair and use their GCD to reduce it. This allows us to hop along all of
 * the cells whose centers will fall along the line.
 **/

import java.util.*;
import java.io.*;

public class Trojke {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read and store input
    int n = Integer.parseInt(br.readLine());
    char[][] grid = new char[n][];
    for (int i = 0; i < n; i++) {
      grid[i] = br.readLine().toCharArray();
    }

    // Count number of letters
    int nLetters = 0;
    for (int y = 0; y < n; y++) {
      for (int x = 0; x < n; x++) {
        if (Character.isLetter(grid[y][x])) {
          nLetters++;
        }
      }
    }

    // Store positions of each letter
    int[] letterX = new int[nLetters];
    int[] letterY = new int[nLetters];
    int index = 0;
    for (int y = 0; y < n; y++) {
      for (int x = 0; x < n; x++) {
        if (Character.isLetter(grid[y][x])) {
          letterX[index] = x;
          letterY[index] = y;
          index++;
        }
      }
    }

    // Count each triplet by iterating over ordered pairs and then counting the number of letters which line up with that pair
    int count = 0;
    for (int i = 0; i < nLetters; i++) {
      for (int j = i + 1; j < nLetters; j++) {
        int dx = letterX[j] - letterX[i];
        int dy = letterY[j] - letterY[i];
        int gcd = Math.abs(gcd(dx, dy));
        dx /= gcd;
        dy /= gcd;
        for (int x = letterX[j] + dx, y = letterY[j] + dy; 0 <= x && x < n && 0 <= y && y < n; x += dx, y += dy) {
          if (Character.isLetter(grid[y][x])) {
            count++;
          }
        }
      }
    }

    // Output answer
    System.out.println(count);
  
  }

  // Find the greatest common divisor
  static int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
  }

}