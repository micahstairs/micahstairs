/**
 * Problem Link: https://open.kattis.com/problems/equivalences
 * Author: Micah Stairs
 * Solved On: January 14, 2017
 *
 * This solution relies on the use of an algorithm which finds Strongly
 * Connected Components. We note that we have shown that all of the statements
 * are equivalent if the associated graph (where nodes represent statements, and
 * the directed edges represent implications) is strongly connected. In this case
 * our answer is 0.
 *
 * Otherwise, we want to find the minimum number of directed
 * edges which need to be added in order to created a strongly connected graph.
 * This is a classic problem, and can be determined by examining the graph induced
 * by compressing each of the strongly connected components into single nodes. In order
 * to be strongly connected, it makes sense that we at least require each node to have
 * both outward and inwards edges. So the number of edges that will need to be added
 * is at least the maximum of the number of nodes with missing outward edges and the
 * number of nodes with missing inward edges. As it turns out, this is a sufficient
 * condition, so our answer is the maximum of these two values.
 **/

import java.util.*;
import java.io.*;

public class ProvingEquivalences {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.parseInt(br.readLine());
    while (t-- > 0) {

      // Read the first line of input      
      String[] line = br.readLine().split(" ");
      int n = Integer.parseInt(line[0]);
      int m = Integer.parseInt(line[1]);

      // Create nodes
      Node[] nodes = new Node[n];
      for (int i = 0; i < n; i++) {
        nodes[i] = new Node(i);
      }

      // Read in edges
      for (int i = 0; i < m; i++) {
        line = br.readLine().split(" ");
        int a = Integer.parseInt(line[0]) - 1;
        int b = Integer.parseInt(line[1]) - 1;
        nodes[a].adj.add(nodes[b]);
      }

      // Run an algorithm to find the strongly connected components
      Tarjan scc = new Tarjan(nodes);

      // If there is only one strongly connected component then no more
      // implications are needed
      if (scc.count == 1) {
        sb.append("0\n");

      // Otherwise we need to add at least 1 implication to form a strongly
      // connected graph
      } else {

        // Consider the graph formed by compressing each strongly connected
        // component into a single node, and figure out which nodes have
        // outwards and inwards edges
        boolean[] isComponent = new boolean[n];
        boolean[] hasIn = new boolean[n];
        boolean[] hasOut = new boolean[n];
        for (int i = 0; i < n; i++) {
          int compU = scc.id[i];
          isComponent[compU] = true;
          for (Node node : nodes[i].adj) {
            int v = node.index;
            int compV = scc.id[v];
            if (compU == compV) continue;
            hasOut[compU] = true;
            hasIn[compV] = true;
          }
        }

        // Use sets to count the unique components which do not have have
        // any incoming edges as well as those which do not have any outgoing
        // edges
        Set<Integer> withoutIn = new HashSet<>();
        Set<Integer> withoutOut = new HashSet<>();
        for (int i = 0; i < n; i++) {
          if (isComponent[i]) {
            if (!hasIn[i]) withoutIn.add(i);
            if (!hasOut[i]) withoutOut.add(i);
          }
        }

        // Append answer for this test case
        sb.append(Math.max(withoutOut.size(), withoutIn.size()) + "\n");

      }

    }

    // Output all of the answers
    System.out.print(sb);
  
  }

}

// Adjacency List graph representation
class Node {
  int index;
  List<Node> adj = new ArrayList<>();
  public Node(int index) {
    this.index = index;
  }
}

// Algorithm to find Strongly Connected Components
class Tarjan {
  Node[] nodes;
  int n, pre, count = 0;
  boolean[] marked;
  int[] id, low;
  Stack<Integer> stack = new Stack<Integer>();
  public Tarjan(Node[] nodes) {
    n = nodes.length;
    this.nodes = nodes;
    marked = new boolean[n];
    id = new int[n];
    low = new int[n];
    for (int u = 0; u < n; u++)
      if (!marked[u])
        dfs(u);
  }
  void dfs(int u) {
    marked[u] = true;
    low[u] = pre++;
    int min = low[u];
    stack.push(u);
    for (Node node : nodes[u].adj) {
      int v = node.index;
      if (!marked[v]) dfs(v);
      if (low[v] < min) min = low[v];
    }
    if (min < low[u]) {
      low[u] = min;
      return;
    }
    int v;
    do {
      v = stack.pop();
      id[v] = count;
      low[v] = n;
    } while (v != u);
    count++;
  }
}