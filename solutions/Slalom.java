/**
 * https://open.kattis.com/problems/slalom
 * Author: Micah Stairs
 * Solved On: January 31, 2017
 * 
 * I intially discussed how to approach this problem with William Fiset and Finn
 * Lidbetter.
 *
 * Although tedius to implement, the general solution is relatively straight-forward.
 * We want to take the problem and reduce it to a set of interesting points, find
 * the connections between those nodes, and then find the shortest path.
 *
 * The interesting points clearly include the starting position as well as each
 * gate endpoint. But we may also need to add extra points to the finish line
 * (since we will want to go vertically through the finish line, if possible,
 * since this minimizes the distance).
 *
 * When actually computing which points are reachable from a given point, we use
 * slopes to determine if we can pass through all of the gates leading up to the
 * current one and still reach a particular endpoint on the current gate.
 *
 * To eliminate the need to worry about vertical slopes, we actually rotate the input
 * (and mirror it) so that we imagine going from left to right instead of top to
 * bottom.
 *
 * Although it has a complexity of O(n^2), this solution was initially too slow
 * so I had to spend a long time trying to optimize it. As it turns out,
 * Math.hypot() is very slow for the purposes of finding the distance between
 * two points. It is about 3x faster to use Math.sqrt() and do the multiplications
 * yourself. The reason for this is because Math.hypot() does some extra work to
 * avoid the possibility of overflow.
 **/

import java.util.*;
import java.io.*;

public class Slalom {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static final int MAX = 1000;
  static final int MAX_NODES = MAX * 4;

  // Re-use data structures for each test case
  static double[] xPos, yPos1, yPos2, finishLineY;
  static Set<Double> yCoordindates = new HashSet<>();
  static Map<Double, Integer> yCoordinateToIndex = new HashMap<>();
  static double[][] adj = new double[MAX_NODES][MAX_NODES];

  public static void main(String[] args) throws IOException {

    // Process each test case
    while (true) {
    
      // Read first line of input
      int n = Integer.parseInt(br.readLine());
    
      // End of input
      if (n == 0) break;
      
      // Store starting position
      String[] line = br.readLine().split(" ");
      double startY = Double.parseDouble(line[0]);
      double startX = -Double.parseDouble(line[1]);

      // Store positions 
      xPos = new double[n];
      yPos1 = new double[n];
      yPos2 = new double[n];
      for (int i = 0; i < n; i++) {
        line = br.readLine().split(" ");
        xPos[i] = -Double.parseDouble(line[0]);
        yPos1[i] = Double.parseDouble(line[1]);
        yPos2[i] = Double.parseDouble(line[2]);
      }

      // Read empty line
      br.readLine();

      // Create set of y-coordinates which lie in the range of the finish line
      // which are present in the input
      yCoordindates.clear();
      yCoordindates.add(yPos1[n-1]);
      yCoordindates.add(yPos2[n-1]);
      if (yPos1[n-1] <= startY && startY <= yPos2[n-1]) {
        yCoordindates.add(startY);
      }
      for (int i = 0; i < n - 1; i++) {
        if (yPos1[n-1] <= yPos1[i] && yPos1[i] <= yPos2[n-1]) {
          yCoordindates.add(yPos1[i]);
        }
        if (yPos1[n-1] <= yPos2[i] && yPos2[i] <= yPos2[n-1]) {
          yCoordindates.add(yPos2[i]);
        }
      }

      // Move these coordinates from the set into a more accessible array
      finishLineY = new double[yCoordindates.size()];
      int nextIndex = 0;
      yCoordinateToIndex.clear();
      for (double y : yCoordindates) {
        yCoordinateToIndex.put(y, nextIndex);
        finishLineY[nextIndex++] = y;
      }

      // Set up the graph
      int nNodes = 2 + (n - 1) * 2 + finishLineY.length;
      for (int i = 0; i < nNodes; i++) Arrays.fill(adj[i], Double.POSITIVE_INFINITY);
      final int START = 0;
      final int END = nNodes - 1;

      // Try going from the start
      f(0, START, startX, startY);

      // Try going from both endpoints of each gate
      for (int i = 0; i < n - 1; i++) {
        f(i + 1, 1 + i * 2, xPos[i], yPos1[i]);
        f(i + 1, 1 + i * 2 + 1, xPos[i], yPos2[i]);
      }

      // Connect all points on finish line to a single 'END' node
      for (int i = 0; i < finishLineY.length; i++) {
        int index = 1 + (n - 1) * 2 + i;
        adj[index][END] = 0;
      }

      // Take advantage of the DAG structure to compute the shortest path in linear time
      double[] dist = new double[nNodes];
      Arrays.fill(dist, Double.POSITIVE_INFINITY);
      dist[START] = 0;
      for (int u = 0; u < nNodes; u++) {
        if (dist[u] == Double.POSITIVE_INFINITY) continue;
        for (int v = u + 1; v < nNodes; v++) {
          if (adj[u][v] == Double.POSITIVE_INFINITY) continue;
          double newDist = dist[u] + adj[u][v];
          if (newDist < dist[v]) dist[v] = newDist;
        }
      }

      // Append answer
      sb.append(dist[END] + "\n");

    }

    // Output all of the answers
    System.out.print(sb);
  
  }

  // Starting at the given point, connect to all other reachable nodes, computing
  // the distance between them
  static void f(int startFromGate, int u, double x1, double y1) {

    double minSlope = Double.NEGATIVE_INFINITY;
    double maxSlope = Double.POSITIVE_INFINITY;

    // Try connecting to both endpoints on each gate (except for the finish line)
    int n = xPos.length;
    for (int i = startFromGate; i < n - 1; i++) {
      double x2 = xPos[i];

      // End early
      if (minSlope > maxSlope) break;

      // Compute slopes
      double bottomSlope = getSlope(x1, y1, x2, yPos1[i]);
      double topSlope = getSlope(x1, y1, x2, yPos2[i]);

      // Try connecting to lower endpoint
      if (minSlope <= bottomSlope && bottomSlope <= maxSlope) {
        int v = 1 + 2 * i;
        double y2 = yPos1[i];
        adj[u][v] = hypot(x1 - x2, y1 - y2);
      }

      // Try connecting to upper endpoint
      if (minSlope <= topSlope && topSlope <= maxSlope) {
        int v = 1 + 2 * i + 1;
        double y2 = yPos2[i];
        adj[u][v] = hypot(x1 - x2, y1 - y2);
      }

      // Update minimum and maximum slope
      if (bottomSlope > minSlope) minSlope = bottomSlope;
      if (topSlope < maxSlope) maxSlope = topSlope;
      
    }

    // Consider connecting to all of the interesting points on the finish line
    double finishLineX = xPos[n-1];
    for (int i = 0; i < finishLineY.length; i++) {
      double x2 = finishLineX;
      double y2 = finishLineY[i];
      double slope = getSlope(x1, y1, x2, y2);
      if (minSlope <= slope && slope <= maxSlope) {
        int v = 1 + (n - 1) * 2 + i;
        adj[u][v] = hypot(x1 - x2, y1 - y2);
      }      
    }

  }

  // This method is much faster than Math.hypot() for some reason
  static double hypot(double a, double b) {
    return Math.sqrt(a * a + b * b);
  }

  // Assumes that 'x1' does not equal 'x2'
  static double getSlope(double x1, double y1, double x2, double y2) {
    return (y2 - y1) / (x2 - x1);
  }

}