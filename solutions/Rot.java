/**
 * https://open.kattis.com/problems/rot
 * Author: Micah Stairs
 * Solved On: April 14, 2017
 * 
 * This problem is fairly straight-forward, however the 45 degree rotation
 * takes a bit of thought to properly implement.
 **/

import java.util.*;
import java.io.*;

public class Rot {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read and store input
    String[] line = br.readLine().split(" ");
    int r = Integer.parseInt(line[0]);
    int c = Integer.parseInt(line[1]);
    char[][] arr = new char[r][];
    for (int i = 0; i < r; i++) {
      arr[i] = br.readLine().toCharArray();
    }
    int deg = Integer.parseInt(br.readLine());

    // Rotate by all needed multiples of 90 degrees
    while (deg >= 90) {
      arr = rotateClockwise(arr);
      deg -= 90;
    }

    // Rotate by 45 degrees if needed
    if (deg == 45) {
      int n = arr.length + arr[0].length - 1;
      char[][] rotated = new char[n][n];
      for (int i = 0; i < n; i++) Arrays.fill(rotated[i], ' ');
      for (int i = 0; i < arr.length; i++) {
        for (int j = 0; j < arr[0].length; j++) {
          int x = j + (arr.length - i - 1);
          int y = j + i;
          rotated[y][x] = arr[i][j];
        }
      }
      arr = rotated;
    }

    // Output answer
    for (int i = 0; i < arr.length; i++) {
      String str = "!";
      for (int j = 0; j < arr[i].length; j++) {
        str += arr[i][j];
      }
      sb.append(str.trim().substring(1) + "\n");
    }
    System.out.print(sb);
  
  }

  // Rotate the matrix clockwise
  static char[][] rotateClockwise(char[][] arr) {
    int n = arr.length;
    int m = arr[0].length;
    char[][] rotated = new char[m][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        rotated[j][n-i-1] = arr[i][j];
      }
    }
    return rotated;
  }

}