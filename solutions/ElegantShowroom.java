/**
 * https://open.kattis.com/problems/showroom
 * Author: Micah Stairs
 * Solved On: March 13, 2017
 * 
 * The key insight to this problem is realizing that we want to do a breadth-first
 * search from all of the exterior doors (doors on the edge of the grid). We will
 * want to treat all cars with a cost of 1, but interior doors (doors found anywhere
 * on the inside on the grid) shouldn't have a cost.
 *
 * This solution uses a cool trick to take care of costs of 0 without adding to the
 * time complexity of the solution. Instead of using a queue, we use a deque. Since
 * we have a seperator element in the deque, which is used to indicate when we've moved
 * to the next depth, the end of the deque that we place a new element on depends on
 * the cost we want to add. When we put something at the back of the deque, it adds a
 * cost of 1. And when we put something at the front of the deque, it doesn't add to
 * the cost at all.
 **/

import java.util.*;
import java.io.*;

public class ElegantShowroom {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  static int w, h;
  static char[][] grid;

  public static void main(String[] args) throws IOException {

    // Read input
    String[] line = br.readLine().split(" ");
    h = Integer.parseInt(line[0]);
    w = Integer.parseInt(line[1]);

    // Read in the grid
    grid = new char[h][w];
    for (int i = 0; i < h; i++) {
      grid[i] = br.readLine().toCharArray();
    }

    // Read in the coordinates of the car
    line = br.readLine().split(" ");
    int targetY = Integer.parseInt(line[0]) - 1;
    int targetX = Integer.parseInt(line[1]) - 1;

    // Compute and output answer
    System.out.println(bfs(targetX, targetY));

  }

  // Do a Breadth-First Search from the exterior doors to the car, treating the edge
  // weight of interior doors as 0 and cars as 1.
  static int bfs(int targetX, int targetY) {

    final int SEPARATOR = -1;
    Deque<Integer> dequeX = new ArrayDeque<>();
    Deque<Integer> dequeY = new ArrayDeque<>();
    boolean[][] visited = new boolean[h][w];

    // Left and right sides
    for (int y = 1; y < h - 1; y++) {
      if (grid[y][0] == 'D') {
        dequeX.addFirst(0);
        dequeY.addFirst(y);
        visited[y][0] = true;
      }
      if (grid[y][w-1] == 'D') {
        dequeX.addFirst(w-1);
        dequeY.addFirst(y);
        visited[y][w-1] = true;
      }
    }

    // Top and bottom sides
    for (int x = 1; x < w - 1; x++) {
      if (grid[0][x] == 'D') {
        dequeX.addFirst(x);
        dequeY.addFirst(0);
        visited[0][x] = true;
      }
      if (grid[h-1][x] == 'D') {
        dequeX.addFirst(x);
        dequeY.addFirst(w-1);
        visited[h-1][x] = true;
      }
    }

    dequeX.addLast(SEPARATOR);
    int[] dx = {-1, 1, 0, 0};
    int[] dy = {0, 0, -1, 1};
    int depth = 0;

    // Run BFS
    while (dequeY.size() > 0) {

      // Move to next depth
      if (dequeX.peekFirst() == SEPARATOR) {
        dequeX.addLast(dequeX.removeFirst());
        depth++;
      }

      // Get next cell
      int x = dequeX.removeFirst();
      int y = dequeY.removeFirst();

      // Reached car
      if (x == targetX && y == targetY) return depth;

      // Try moving in all 4 directions
      for (int i = 0; i < dx.length; i++) {
        int newX = x + dx[i];
        int newY = y + dy[i];
        if (newX >= 0 && newX < w && newY >= 0 && newY < h) {

          // Car, treat as cost 1
          if (grid[newY][newX] == 'c') {
            if (!visited[newY][newX]) {
              visited[newY][newX] = true;
              dequeX.addLast(newX);
              dequeY.addLast(newY);
            }

          // Door, treat as cost 0 (by placing at the front of the deque)
          } else if (grid[newY][newX] == 'D') {
            if (!visited[newY][newX]) {
              visited[newY][newX] = true;
              dequeX.addFirst(newX);
              dequeY.addFirst(newY);
            }
          }
        }
      }

    }

    // We will never reach this
    throw new IllegalStateException();

  }

}