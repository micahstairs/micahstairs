/**
 * https://open.kattis.com/problems/darkness
 * Author: Micah Stairs
 * Solved On: March 15, 2017
 * 
 * I discussed the solution to this problem with Liam Keliher, Finn Lidbetter and
 * William Fiset. The general approach is to first compute the amount of light
 * which hits each of the cells. Then we determine which of the cells are sufficiently
 * lit. Finally we start building a flow graph, connecting adjacent cells with the 
 * proper capacity (either 11 or 43). We connect the source to each of the dark cells
 * with an infinite capacity. And we connect any cell on the each of the grid to the
 * sink with infinite capacity. We get the minimum cut from this graph, and divide the
 * answer by two, since we added two edges for each adjacent pair of cells (one in
 * each direction).
 **/

import java.util.*;
import java.io.*;

public class TheDarkness {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  static final int INF = 1_000_000;

  public static void main(String[] args) throws IOException {

    // Read first few lines of input
    int b = Integer.parseInt(br.readLine());
    int h = Integer.parseInt(br.readLine());
    String[] line = br.readLine().split(" ");
    int r = Integer.parseInt(line[0]);
    int c = Integer.parseInt(line[1]);
    
    // Determine the amount of light which hits each cell
    double[][] light = new double[r][c];
    for (int y1 = 0; y1 < r; y1++) {
      String str = br.readLine();
      for (int x1 = 0; x1 < c; x1++) {
        int s = str.charAt(x1) - '0';
        for (int y2 = 0; y2 < r; y2++) {
          for (int x2 = 0; x2 < c; x2++) {
            int dx = Math.abs(x1 - x2);
            int dy = Math.abs(y1 - y2);
            light[y2][x2] += (double) s / (dx * dx + dy * dy + h * h);
          }  
        }
      }
    }

    // Determine which cells are sufficiently lit
    boolean[][] sufficientlyLit = new boolean[r][c];
    for (int y = 0; y < r; y++) {
      for (int x = 0; x < c; x++) {
        sufficientlyLit[y][x] = light[y][x] >= b;
      }
    }

    // Setup graph
    int nNodes = r * c + 2;
    int source = nNodes - 2;
    int sink = nNodes - 1;
    List<Edge>[] graph = createGraph(nNodes);

    // Add edges to graph
    int[] dx = {-1, 1, 0, 0};
    int[] dy = {0, 0, -1, 1};
    for (int y1 = 0; y1 < r; y1++) {
      for (int x1 = 0; x1 < c; x1++) {
        int index1 = y1 * c + x1;
        boolean isOnBoundary = false;
        for (int i = 0; i < 4; i++) {
          int x2 = x1 + dx[i];
          int y2 = y1 + dy[i];
          if (x2 >= 0 && x2 < c && y2 >= 0 && y2 < r) {
            int index2 = y2 * c + x2;
            int cap = (sufficientlyLit[y1][x1] && sufficientlyLit[y2][x2]) ? 43 : 11;
            addEdge(graph, index1, index2, cap);
            addEdge(graph, index2, index1, cap);
          } else isOnBoundary = true;
        }

        // Connect source to it
        if (!sufficientlyLit[y1][x1]) addEdge(graph, source, index1, INF);

        // Connect it to sink
        if (isOnBoundary) addEdge(graph, index1, sink, INF);
        
      }
    }

    // Output answer (dividing by 2 since we added edges twice)
    System.out.println(maxFlow(graph, source, sink) / 2);

  }

  // Dinic's algorithm used to find the maximum flow (minimum cut)
  static List<Edge>[] createGraph(int nodes) {
    List<Edge>[] graph = new List[nodes];
    for (int i = 0; i < nodes; i++) graph[i] = new ArrayList<>();
    return graph;
  }
  static void addEdge(List<Edge>[] graph, int u, int v, int cap) {
    graph[u].add(new Edge(v, graph[v].size(), cap));
    graph[v].add(new Edge(u, graph[u].size() - 1, 0));
  }
  static int maxFlow(List<Edge>[] graph, int s, int t) {
    boolean[] mincut = new boolean[graph.length];
    for (int flow = 0;;) {
      int df = findPath(graph, mincut, s, t, Integer.MAX_VALUE);
      if (df == 0) return flow;
      Arrays.fill(mincut, false);
      flow += df;
    }
  }
  static int findPath(List<Edge>[] gr, boolean[] vis, int u, int t, int f) {
    if (u == t) return f;
    vis[u] = true;
    for (Edge e : gr[u])
      if (!vis[e.v] && e.f < e.cap) {
        int df = findPath(gr, vis, e.v, t, Math.min(f, e.cap - e.f));
        if (df > 0) { e.f += df; gr[e.v].get(e.rev).f -= df; return df;
      }
    }
    return 0;
  }
  static class Edge {
    int v, rev; int cap, f;
    public Edge(int v, int rev, int cap) {
      this.v = v; this.rev = rev; this.cap = cap;
    }
  }

}