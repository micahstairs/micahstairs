/**
 * Problem Link: https://open.kattis.com/problems/speedyescape
 * Author: Micah Stairs
 * Solved On: January 15, 2017
 *
 * The solution outlined here is actually faster than the official solution
 * (which uses a binary search on top of Dijkstra's Shortest Path algorithm).
 * First we we compute the smallest amount of time needed for the police to
 * reach each node. Then we do the same for the brothers, except we also prevent
 * the node where the police to be visited (since no matter how fast they drive,
 * they will not be able to beat the police there).
 *
 * Next we use this information when performing a modified version of Dijkstra's.
 * The only requirement of the algorithm is that we use a monotonically increasing
 * function. We will be considering the ratio between the time it takes for the
 * brothers to get there and the time it takes for the police to get there. Our
 * function will not be adding the ratios, but it will be taking the maximum of
 * the minimum ratio needed to reach our current node and the ratio of the next
 * node.
 *
 * Finally, we use these minimum ratios, and considering only the nodes which are
 * exits, our answer will the minimum of these values. If the minimum is INF then
 * it is impossible.
 **/

import java.util.*;
import java.io.*;

public class SpeedyEscape {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Get first line of input
    String[] line = br.readLine().split(" ");
    int n = Integer.parseInt(line[0]);
    int m = Integer.parseInt(line[1]);
    int e = Integer.parseInt(line[2]);

    // Initialize adjacency matrix
    double[][] adj = new double[n][n];
    for (int i = 0; i < n; i++) {
      Arrays.fill(adj[i], Double.POSITIVE_INFINITY);
      adj[i][i] = 0;
    }

    // Read in the edges
    for (int i = 0; i < m; i++) {
      line = br.readLine().split(" ");
      int u = Integer.parseInt(line[0]) - 1;
      int v = Integer.parseInt(line[1]) - 1;
      int w = Integer.parseInt(line[2]);
      adj[u][v] = adj[v][u] = w;
    }

    // Read in the exits
    boolean[] exit = new boolean[n];
    line = br.readLine().split(" ");
    for (int i = 0; i < e; i++) {
      int index = Integer.valueOf(line[i]) - 1;
      exit[index] = true;
    }

    // Read in the starting locations
    line = br.readLine().split(" ");
    int b = Integer.parseInt(line[0]) - 1;
    int p = Integer.parseInt(line[1]) - 1;

    // Find the shortest amount of time that it takes for the police to get to each node
    double[] policeDist = dijkstra(adj, p, null);

    // Find the shortest amount of time that it takes for the brothers to get to each node
    // without visiting the spot where the police start out
    double[] brothersDist = dijkstra(adj, b, p);

    // Compute the minimum ratio needed for the brothers to reach each node
    double[] ratios = modifiedDijkstra(adj, brothersDist, policeDist, b, p);
    
    // From the ratios of the exits, compute the minimum speed needed to escape
    double min = Double.POSITIVE_INFINITY;
    for (int i = 0; i < n; i++) {
      if (exit[i]) {
        min = Math.min(min, ratios[i] * 160.0);
      }
    }

    // Output answer
    if (min == Double.POSITIVE_INFINITY) System.out.println("IMPOSSIBLE");
    else System.out.println(min);

  }

  // Use a modified version of Dijkstra's Shortest Path algorithm to 
  static double[] modifiedDijkstra(double[][] adj, double[] brothersDist, double[] policeDist, int start, Integer avoid) {
    
    // Setup
    int n = adj.length;
    boolean[] visited = new boolean[n];
    double[] minRatio = new double[n];
    Arrays.fill(minRatio, Double.POSITIVE_INFINITY);
    Queue<QNode> q = new PriorityQueue<>();
    q.add(new QNode(start, 0));
    minRatio[start] = 0;

    // BFS
    while (!q.isEmpty()) {
      QNode qNode = q.remove();
      int u = qNode.index;
      if (visited[u]) continue;
      visited[u] = true;
      for (int v = 0; v < n; v++) {
        if (avoid != null && v == avoid) continue;
        if (adj[u][v] == Double.POSITIVE_INFINITY) continue;
        double newRatio = Math.max(minRatio[u], brothersDist[v]/policeDist[v]);
        if (newRatio < minRatio[v]) {
          minRatio[v] = newRatio;
          q.add(new QNode(v, newRatio));
        }
      }
    }

    return minRatio;
  }

  static double[] dijkstra(double[][] adj, int start, Integer avoid) {

    // Setup
    int n = adj.length;
    boolean[] visited = new boolean[n];
    double[] min = new double[n];
    Arrays.fill(min, Double.POSITIVE_INFINITY);
    Queue<QNode> q = new PriorityQueue<>();
    q.add(new QNode(start, 0));
    min[start] = 0;

    // BFS
    while (!q.isEmpty()) {
      QNode qNode = q.remove();
      int u = qNode.index;
      if (visited[u]) continue;
      visited[u] = true;
      for (int v = 0; v < n; v++) {
        if (avoid != null && avoid == v) continue;
        double newCost = min[u] + adj[u][v];
        if (newCost < min[v]) {
          min[v] = newCost;
          q.add(new QNode(v, newCost));
        }
      }
    }

    return min;
  }

  // Used by Priority Queue for BFS
  static class QNode implements Comparable<QNode> {
    int index;
    double cost;
    public QNode(int index, double cost) {
      this.index = index;
      this.cost = cost;
    }
    @Override public int compareTo(QNode other) {
      return Double.compare(cost, other.cost);
    }
  }

}