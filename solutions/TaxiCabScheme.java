/**
 * https://open.kattis.com/problems/taxicab
 * Author: Micah Stairs
 * Solved On: March 15, 2017
 * 
 * I worked on this problem with Finn Lidbetter. The general approach is to look
 * at each pair of cab rides and determine if one cab can be re-used for the other
 * ride. This is done by looking at the starting time of the first cab ride, adding
 * the time taken to do that ride, and then adding the time taken to get to the
 * starting location of the second cab ride. If the starting time of the second ride
 * is later than this computed time, then the cab can be re-used. We use maximum
 * flow to compute the number of cabs which can be re-used, using the generated
 * bipartite graph with capacities of 1.
 **/

import java.util.*;
import java.io.*;

public class TaxiCabScheme {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.parseInt(br.readLine());
    while (t-- > 0) {
      
      // Read the number of cabs
      int n = Integer.parseInt(br.readLine());
      
      // Store input and compute the ending time of each cab ride
      int[] startTimes = new int[n];
      int[] endTimes = new int[n];
      int[] startX = new int[n];
      int[] startY = new int[n];
      int[] endX = new int[n];
      int[] endY = new int[n];
      for (int i = 0; i < n; i++) {
        String[] line = br.readLine().split(" ");
        startTimes[i] = parseTime(line[0]);
        startX[i] = Integer.parseInt(line[1]);
        startY[i] = Integer.parseInt(line[2]);
        endX[i] = Integer.parseInt(line[3]);
        endY[i] = Integer.parseInt(line[4]);
        endTimes[i] = startTimes[i] + dist(startX[i], startY[i], endX[i], endY[i]);
      }

      // Setup graph
      int nNodes = n + n + 2;
      int source = nNodes - 2;
      int sink = nNodes - 1;
      List<Edge>[] graph = createGraph(nNodes);

      // Connect to source and sink
      for (int i = 0; i < n; i++) {
        addEdge(graph, source, i, 1);
        addEdge(graph, n + i, sink, 1);
      }

      // Check to see which
      boolean[][] canReuse = new boolean[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          int timeReady = endTimes[i] + dist(endX[i], endY[i], startX[j], startY[j]);
          if (timeReady < startTimes[j]) addEdge(graph, i, n + j, 1);
        }
      }

      // Add answer
      int nCabsSaved = maxFlow(graph, source, sink);
      int nCabsNeeded = n - nCabsSaved;
      sb.append(nCabsNeeded + "\n");

    }

    // Output answers
    System.out.print(sb);
  
  }

  // Compute the Manhatten distance between two points
  static int dist(int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }

  // Parse time and represent it as the total number of minutes
  static int parseTime(String str) {
    int hours = Integer.parseInt(str.substring(0, 2));
    int minutes = Integer.parseInt(str.substring(3, 5));
    return hours * 60 + minutes;
  }

  // Dinic's algorithm used to find the maximum flow
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