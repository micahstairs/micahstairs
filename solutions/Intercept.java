/**
 * Problem Link: https://open.kattis.com/problems/intercept
 * Author: Micah Stairs
 * Solved On: January 15, 2017
 *
 * To solve this problem, we first run Dijksta's Shortest Path algorithm, and
 * then construct a list of the edges used in all shortest paths from the start
 * to the end. We then build an undirected graph from the edges in this list.
 * We then find the "articulation points" (or cut vertices) of this new graph.
 * Our answer is the list of these nodes (as well as the starting and ending
 * node).
 **/

import java.util.*;
import java.io.*;

public class Intercept {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static int n;

  public static void main(String[] args) throws IOException {

    // Read first line of input
    String[] line = br.readLine().split(" ");
    n = Integer.parseInt(line[0]);
    int m = Integer.parseInt(line[1]);

    // Create empty nodes
    Node[] nodes = new Node[n];
    for (int i = 0; i < n; i++) {
      nodes[i] = new Node(i);
    }

    // Read in the edges
    for (int i = 0; i < m; i++) {
      line = br.readLine().split(" ");
      int u = Integer.parseInt(line[0]);
      int v = Integer.parseInt(line[1]);
      if (u == v) continue;
      double w = Integer.parseInt(line[2]);
      nodes[u].adj.add(new Edge(u, v, w));
    }

    // Read start
    line = br.readLine().split(" ");
    int start = Integer.parseInt(line[0]);
    int end = Integer.parseInt(line[1]);

    // Compute answer
    modifiedDijkstras(nodes, start, end);

    // Output answer
    System.out.println(sb);
  
  }

  static void modifiedDijkstras(Node[] nodes, int start, int end) {

    // Setup
    PriorityQueue<QNode> q = new PriorityQueue<>();
    boolean[] visited = new boolean[n];
    double[] min = new double[n];
    Arrays.fill(min, Double.POSITIVE_INFINITY);

    // NOTE: We will need to trace back from the end (using DFS) to figure out which ones were actually
    // used in the shortest path
    List<Set<Edge>> sets = new ArrayList<>();
    for (int i = 0; i < n; i++) sets.add(new HashSet<Edge>());

    // Add starting node to the search
    q.add(new QNode(start, 0));
    min[start] = 0;

    // Run Dijkstra's shortest path algorithm
    while (q.size() > 0) {
      QNode qNode = q.remove();
      if (visited[qNode.index]) continue;
      visited[qNode.index] = true;
      int u = qNode.index;
      for (Edge e : nodes[u].adj) {
        int v = e.v;
        double dist = min[u] + e.w;
        if (dist < min[v]) {
          min[v] = dist;
          q.add(new QNode(v, dist));
          sets.get(v).clear();
          sets.get(v).add(e);
        } else if (dist == min[v]) {
          sets.get(v).add(e);
        }
      }
    }

    // Find edges which are actually used in the shortest path
    Set<Edge> edgesUsed = new HashSet<>();
    dfs(sets, edgesUsed, end);
    
    // Prune the original graph to only include these edges (and make it undirected)
    for (int i = 0; i < n; i++) {
      nodes[i].adj.clear();
    }
    for (Edge edge : edgesUsed) {
      nodes[edge.u].adj.add(edge);
      nodes[edge.v].adj.add(new Edge(edge.v, edge.u, edge.w));
    }

    // Find articulation points
    AP ap = new AP(nodes);

    // Build answer
    boolean first = true;
    for (int i = 0; i < n; i++) {
      if (ap.ap[i] || i == start || i == end) {
        if (!first) sb.append(" ");
        first = false;
        sb.append(i);
      }
    }

  }

  // Traverse back over the edges, storing them
  static void dfs(List<Set<Edge>> sets, Set<Edge> edgesUsed, int v) {
    for (Edge edge : sets.get(v)) {
      if (!edgesUsed.contains(edge)) {
        edgesUsed.add(edge);
        dfs(sets, edgesUsed, edge.u);
      }
    }
  }

  // Used by Priority Queue
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

// Adjacency List graph represention.
class Node {
  int index;
  List<Edge> adj = new ArrayList<>();
  public Node(int index) {
    this.index = index;
  }
}
class Edge {
  int u, v;
  double w;
  public Edge(int u, int v, double w) {
    this.u = u;
    this.v = v;
    this.w = w;
  }
}

// Used to compute Articulation Points of a graph in O(V+E) time
class AP { 
  boolean[] ap;
  int n;
  Node[] nodes;
  boolean[] visited;
  int[] discovered, low, parent;
  int time = 0;
  public AP(Node[] nodes) {
    this.nodes = nodes; n = nodes.length;
    ap = new boolean[n]; visited = new boolean[n];
    discovered = new int[n]; low = new int[n]; parent = new int[n];
    Arrays.fill(low, Integer.MAX_VALUE); Arrays.fill(low, -1);
    for (int i = 0; i < n; i++) if (!visited[i]) dfs(i);
  }
  private void dfs(int u) {
    visited[u] = true;
    discovered[u] = low[u] = ++time;
    int nChildren = 0;
    for (Edge edge : nodes[u].adj) {
      int v = edge.v;
      if (!visited[v]) {
        nChildren++;
        parent[v] = u;
        dfs(v);
        low[u] = Math.min(low[u], low[v]);
        if (parent[u] == -1 && nChildren > 1) ap[u] = true;
        if (parent[u] != -1 && low[v] >= discovered[u]) ap[u] = true;
      } else if (parent[u] != v) low[u] = Math.min(low[u], discovered[v]);
    }
  }
}