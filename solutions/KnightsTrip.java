/**
 * https://open.kattis.com/problems/knightstrip
 * Author: Micah Stairs
 * Solved On: February 22, 2017
 * 
 * I spent a lot of time discussing this solution with Finn Lidbetter but we did
 * not quite come to the correct solution. Afterwards, I discussed it with Liam
 * Keliher and we found the flaw to my original solution and came up with a way
 * to correct it.
 *
 * The general approach of this solution is to use a subset of the allowed moves
 * to quickly move close to our destination and then to use a pre-computed table to
 * finish the remaining moves.
 **/

import java.util.*;
import java.io.*;

public class KnightsTrip {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static final int INF = 987654321;

  // Knight movement
  static int N_DIRECTIONS = 8;
  static int[] dx = {-2, -1, 1, 2,  2,  1, -1, -2};
  static int[] dy = { 1,  2, 2, 1, -1, -2, -2, -1};

  // Pre-compute answers to area around destination
  static final int DIAMETER = 20;
  static final int SIZE = 1 + DIAMETER * 2;
  static int[][] distance = new int[SIZE][SIZE];
  static {
    for (int[] arr : distance) Arrays.fill(arr, INF);
    distance[DIAMETER][DIAMETER] = 0;
    Queue<Integer> qx = new LinkedList<>();
    Queue<Integer> qy = new LinkedList<>();
    qx.add(DIAMETER);
    qy.add(DIAMETER);
    while (qx.size() > 0) {
      int x = qx.remove();
      int y = qy.remove();
      for (int i = 0; i < N_DIRECTIONS; i++) {
        int newX = x + dx[i];
        int newY = y + dy[i];
        if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
          if (distance[newY][newX] > distance[y][x] + 1) {
            distance[newY][newX] = distance[y][x] + 1;
            qx.add(newX);
            qy.add(newY);
          }
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {

    // Process each query
    String line;
    while (!(line = br.readLine()).equals("END")) {
      String[] split = line.split(" ");
      int x = Math.abs(Integer.parseInt(split[0]));
      int y = Math.abs(Integer.parseInt(split[1]));
      sb.append(findMinimum(x, y) + "\n");
    }

    // Output answers
    System.out.print(sb);
  
  }

  // Finds the minimum number of moves 
  static int findMinimum(int x, int y) {
    int min = INF;
    int HALF = DIAMETER / 2;
    for (int i = HALF; i < SIZE - HALF; i++) {
      for (int j = HALF; j < SIZE - HALF; j++) {
        int targetX = x + (i - DIAMETER);
        int targetY = y + (j - DIAMETER);
        if (targetX < 0 || targetY < 0) continue;
        int nMoves = distance[j][i] + countMoves(targetX, targetY);
        if (nMoves < min) {
          min = nMoves;
        }
        
      }
    }
    return min;
  }

  // Computes the number of moves required when only considering a handful of moves which
  // allow you to most efficiently move towards the destination
  static int countMoves(int x, int y) {

    // Above top slope
    if (x * 2 <= y) {

      int nMoves = x;
      y -= 2 * x;
      if (y % 2 == 0) {
        nMoves += y / 2;
        if (y % 4 == 2) nMoves += 2;
        return nMoves;
      }

    // Below bottom slope
    } else if (y * 2 <= x) {

      int nMoves = y;
      x -= 2 * y;
      if (x % 2 == 0) {
        nMoves += x / 2;
        if (x % 4 == 2) nMoves += 2;
        return nMoves;
      }

    // Between slopes
    } else {
      int a = 2 * x - y;
      int b = 2 * y - x;
      if (a % 3 == 0 && b % 3 == 0) return a / 3 + b / 3;
    }

    // Impossible to reach destination using the considered moves
    return INF;

  }

}