/**
 * https://open.kattis.com/problems/bilateral
 * Author: Micah Stairs
 * Solved On: February 17, 2017
 * 
 * This problem can be recognized as a maximum vertex covering problem in a bipartitie graph.
 * Fortunately, this can be transformed into a maximum bipartite matching problem, which can
 * be solved using a max flow algorithm.
 *
 * Due to the input size, we need to be careful about our choice of a graph representation.
 *
 * Surprisingly the constraint that we needed to include our friend, if selecting him did
 * not make the vertex cover larger, does not need to be explicitly enforced using my
 * particular implementation. I had a plan to account for this constraint, which involved
 * running the algorithm twice (once with the friend chosen and once without it chosen),
 * but this was surprisingly too slow.
 **/

import java.util.*;
import java.io.*;

public class BilateralProjects {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  // Constants (offset is used since IDs of 0-999 are never used)
  static final int OFFSET = 1000;
  static final int MAX = 3000 - OFFSET;

  public static void main(String[] args) throws IOException {

    // Create adjacency matrix
    boolean[][] adj = new boolean[MAX][MAX];

    // Read input size
    int m = Integer.parseInt(br.readLine());
    
    // Read input
    for (int i = 0; i < m; i++) {
      String[] split = br.readLine().split(" ");
      int a = Integer.parseInt(split[0]) - OFFSET;
      int b = Integer.parseInt(split[1]) - OFFSET;
      adj[a][b] = adj[b][a] = true;
    }

    // Find minimum vertex cover
    Set<Integer> vertexCover = bipartiteMinimumVertexCover(adj, 1000, 1000);

    // Output answer
    for (int id : vertexCover) {
      sb.append((id + OFFSET) + "\n");
    }
    System.out.println(vertexCover.size());
    System.out.print(sb);

  }

  // Returns a minimum vertex cover of a bipartite graph
  // NOTE: The first 'n' indices are for one side of bipartite graph, remaining 'm' indicies are for other half
  static Set<Integer> bipartiteMinimumVertexCover(boolean[][] adj, int n, int m) {
    int nNodes = n + m + 2, source = nNodes - 2, sink = nNodes - 1;
    List<Edge>[] graph = createGraph(nNodes);
    Set<Integer> hasEdge = new HashSet<>();
    for (int i = 0; i < n; i++) addEdge(graph, source, i, 1);
    for (int i = 0; i < m; i++) addEdge(graph, n + i, sink, 1);
    for (int i = 0; i < n; i++) for (int j = n; j < n + m; j++) {
      if (adj[i][j]) {
        hasEdge.add(i);
        hasEdge.add(j);
        addEdge(graph, i, j, 1);
      }
    }
    maxFlow(graph, source, sink);
    int[][] matched = new int[n+m][n+m];
    Set<Integer> set1 = new HashSet<>(), set2 = new HashSet<>(), set3 = new HashSet<>();
    for (int i = 0; i < n; i++) {
      boolean unmatched = true;
      for (int j = n; j < n + m; j++) {
        boolean used = false;
        for (Edge edge : graph[i]) if (edge.v == j && edge.f > 0) used = true;
        if (adj[i][j] && used) { unmatched = false; matched[i][j] = matched[j][i] = 1; }
      }
      if (unmatched && hasEdge.contains(i)) set1.add(i);
    }
    boolean[][] visited = new boolean[2][n+m];
    for (int u : set1) { dfs(adj, matched, visited, set2, u, 0); dfs(adj, matched, visited, set2, u, 1); }
    for (int i = 0; i < n; i++) if (!set2.contains(i) && hasEdge.contains(i)) set3.add(i);
    for (int i = n; i < n + m; i++) if (set2.contains(i) && hasEdge.contains(i)) set3.add(i);
    return set3;
  }
  static void dfs(boolean[][] adj, int[][] matched, boolean[][] visited, Set<Integer> set, int u, int needMatch) {
    set.add(u);
    if (visited[needMatch][u]) return;
    visited[needMatch][u] = true;
    for (int v = 0; v < matched.length; v++)
      if (adj[u][v] && matched[u][v] == needMatch)
        dfs(adj, matched, visited, set, v, 1 - needMatch);
  }

  // Ford-fulkerson implementation using adjacency list as a graph representation
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
    for (Edge e : gr[u]) {
      if (!vis[e.v] && e.f < e.cap) {
        int df = findPath(gr, vis, e.v, t, Math.min(f, e.cap - e.f));
        if (df > 0) {
          e.f += df; gr[e.v].get(e.rev).f -= df; return df;
        }
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