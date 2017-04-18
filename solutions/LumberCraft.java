/**
 * https://open.kattis.com/problems/lumbercraft
 * Author: Micah Stairs
 * Solved On: April 17, 2017
 * 
 * This is a fairly straight-forward problem. Using priority queues for each player
 * we can sort the trees with respect to their positions, paying careful attention to
 * how we break ties.
 **/

import java.util.*;
import java.io.*;

public class LumberCraft {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    while (true) {
    
      // Read first line of input
      String[] line = br.readLine().split(" ");
      int n = Integer.parseInt(line[0]);
      int h = Integer.parseInt(line[1]);
      int w = Integer.parseInt(line[2]);
    
      // End of input
      if (n == 0 && w == 0 && h == 0) break;
      
      // Store grid of characters
      char[][] grid = new char[h][];
      for (int i = 0; i < h; i++) {
        grid[i] = br.readLine().toCharArray();
      }

      // Count players
      int nPlayers = 0;
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          if (Character.isLetter(grid[y][x])) {
            nPlayers++;
          }
        }
      }

      // Get locations and letters of players
      int[] playerX = new int[nPlayers];
      int[] playerY = new int[nPlayers];
      char[] playerLetter = new char[nPlayers];
      int index = 0;
      Map<Character, Integer> letterToIndexMap = new HashMap<>();
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          if (Character.isLetter(grid[y][x])) {
            playerX[index] = x;
            playerY[index] = y;
            playerLetter[index] = grid[y][x];
            letterToIndexMap.put(playerLetter[index], index);
            index++;
          }
        }
      }

      // Add all of the trees to each player's queue
      List<Queue<Point>> queues = new ArrayList<>();
      for (int i = 0; i < nPlayers; i++) {
        queues.add(new PriorityQueue<Point>(new DistanceComparator(playerX[i], playerY[i])));
        for (int y = 0; y < h; y++) {
          for (int x = 0; x < w; x++) {
            if (grid[y][x] == '!') {
              queues.get(i).add(new Point(x, y));
            }
          }
        }
      }

      // Determine how much lumber each player will get get
      int[][] count = new int[h][w];
      int[] lastX = new int[nPlayers];
      int[] lastY = new int[nPlayers];
      double[] total = new double[nPlayers];
      for (int t = 0; t < n; t++) {

        // Find closest tree to each player
        for (int i = 0; i < nPlayers; i++) {
          lastX[i] = -1;
          lastY[i] = -1;
          while (!queues.get(i).isEmpty()) {
            Point pt = queues.get(i).remove();
            if (grid[pt.y][pt.x] == '!') {
              count[pt.y][pt.x]++;
              lastX[i] = pt.x;
              lastY[i] = pt.y;
              break;
            }
          }
        }

        // Compute the lumber of each player
        for (int i = 0; i < nPlayers; i++) {
          int x = lastX[i];
          int y = lastY[i];
          if (x != -1 && y != -1) {
            total[i] += 1.0 / count[y][x];
          }
        }

        // Cutdown the trees
        for (int i = 0; i < nPlayers; i++) {
          int x = lastX[i];
          int y = lastY[i];
          if (x != -1 && y != -1) {
            count[y][x] = 0;
            grid[y][x] = '.';
          }
        }

      }

      // Build answer
      for (int y = 0; y < h; y++) {
        for (int x = 0; x < w; x++) {
          sb.append(grid[y][x]);
        }
        sb.append("\n");
      }
      for (char ch = 'A'; ch <= 'Z'; ch++) {
        if (letterToIndexMap.containsKey(ch)) {
          int i = letterToIndexMap.get(ch);
          sb.append(playerLetter[i] + " " + total[i] + "\n");
        }
      }

    }

    // Output answer
    System.out.print(sb);
  
  }

}

// Used to represent a tree's location
class Point {
  int x, y;
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

// Used to sort trees in relation to the location of the player
class DistanceComparator implements Comparator<Point> {
  int x, y;
  public DistanceComparator(int x, int y) {
    this.x = x;
    this.y = y;
  }
  @Override public int compare(Point pt1, Point pt2) {
    double dist1 = dist(pt1);
    double dist2 = dist(pt2);
    if (Math.abs(dist1 - dist2) < 0.000001) {
      int cmp = Integer.compare(pt2.x, pt1.x);
      if (cmp != 0) return cmp;
      return Integer.compare(pt2.y, pt1.y);
    } else
    return Double.compare(dist1, dist2);
  }
  double dist(Point pt) {
    int dx = pt.x - x;
    int dy = pt.y - y;
    return Math.sqrt(dx * dx + dy * dy);
  }
}