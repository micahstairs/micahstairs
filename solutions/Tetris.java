/**
 * https://open.kattis.com/problems/tetris
 * Author: Micah Stairs
 * Solved On: February 15, 2017
 * 
 * The only problem we have to worry about in this problem is ensuring that we don't
 * double-count any of the rotations which are actually identical. One way to easily
 * get around this is by hard-coding each unique rotation (easy to do since there are
 * only 7 different shapes and up to 4 unique rotations).
 *
 * The representation chosen to represent a rotated shapes also affects how easy the
 * solution is to implement. I chose to notate each shape rotation using one number
 * per column, which indicates the position of the lowest cell in that column relative
 * to that of the first column. This representation is easy to generate and is extremely
 * easy to use to check if a shape 'fits' on the board.
 **/

import java.util.*;
import java.io.*;

public class Tetris {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  // The distinct patterns of each shape (notated using one number per column, indicating
  // the position of the lowest cell in that column relative to that of the first column)
  static int[][][] shapePatterns = {
    { {0}, {0, 0, 0, 0} },
    { {0, 0} },
    { {0, 0, 1}, {0, -1} },
    { {0, -1, -1}, {0, 1} },
    { {0, 0, 0}, {0, 1}, {0, -1, 0}, {0, -1} },
    { {0, 0, 0}, {0, 0}, {0, 1, 1}, {0, -2} },
    { {0, 0, 0}, {0, 2}, {0, 0, -1}, {0, 0} }
  };

  public static void main(String[] args) throws IOException {

    // Read first line of input
    String[] line = br.readLine().split(" ");
    int c = Integer.parseInt(line[0]);
    int p = Integer.parseInt(line[1]) - 1;

    // Read heights of columns
    String[] split = br.readLine().split(" ");
    int[] arr = new int[c];
    for (int i = 0; i < c; i++) {
      arr[i] = Integer.parseInt(split[i]);
    }

    // Check each possible placement of each distinct shape rotation
    int nWays = 0;
    for (int[] pattern : shapePatterns[p]) {
      loop: for (int i = 0; i + pattern.length <= c; i++) {
        for (int j = 0; j < pattern.length; j++) {
          if (arr[i+j] != arr[i] + pattern[j]) {
            continue loop;
          }
        }
        nWays++;
      }
    }

    // Output answer
    System.out.println(nWays);
    
  }

}