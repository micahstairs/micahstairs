/**
 * https://open.kattis.com/problems/jumpingmonkey
 * Author: Micah Stairs
 * Solved On: January 24, 2017
 * 
 * I worked on this problem with Finn Lidbetter. We tried a wide variety of
 * approaches before finally finding the correct one. We started with a
 * greedy approach (since we thought there were only a small amount of cases
 * that were actually possible), then we came to a dynamic programming solution.
 * But the problem with this was finding the shortest solution, so I had to switch
 * to a BFS. After finally finding this solution, it seemed quite intuitive. The
 * small input size should have led me to this solution earlier.
 **/

import java.util.*;
import java.io.*;

public class JumpingMonkey {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static int n;
  static boolean[][] adj;

  // Used to re-build the sequence afters
  static Map<Integer, Integer> prevState = new HashMap<>();
  static Map<Integer, Integer> prevIndex = new HashMap<>();

  public static void main(String[] args) throws IOException {

    // Process each test case
    outer: while (true) {

      // Reset memoNextState
      prevState.clear();
      prevIndex.clear();
    
      // First line of input
      String[] line = br.readLine().split(" ");
      n = Integer.parseInt(line[0]);
      int m = Integer.parseInt(line[1]);

      // End of input
      if (n == 0 && m == 0) break;

      // Read in the edges
      adj = new boolean[n][n];
      for (int i = 0; i < m; i++) {
        line = br.readLine().split(" ");
        int a = Integer.parseInt(line[0]);
        int b = Integer.parseInt(line[1]);
        adj[a][b] = adj[b][a] = true;
      }
      br.readLine();

      // Define important states (a 1 in position 'i' means that the monkey could be on that tree)
      int startingState = (1 << n) - 1;
      int endingState = 0;

      // Run BFS to find shortest solution
      boolean possible = bfs(startingState, endingState);

      // No solution exists
      if (!possible) {
        sb.append("Impossible\n");

      //
      } else {

        // Retrace path from the end to the start
        int cur = endingState;
        List<Integer> indices = new ArrayList<>();
        while (cur != startingState) {
          int index = prevIndex.get(cur);
          indices.add(index);
          cur = prevState.get(cur);
        }

        // Reverse path and build output
        Collections.reverse(indices);
        sb.append(indices.size() + ":");
        for (int index : indices) {
          sb.append(" " + index);
        }
        sb.append("\n");

      }

    }

    // Output all of the answers
    System.out.print(sb);
  
  }

  // Returns true if a solution exists
  static boolean bfs(int start, int end) {

    // Setup
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> q = new LinkedList<>();
    q.add(start);

    // Do BFS
    while (q.size() > 0) {

      int state = q.remove();

      // Reached end
      if (state == end) return true;

      // Determine the new set of trees that the monkey might be in
      int newState = 0;
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          if (adj[i][j] && isSet(state, i)) {
            newState |= (1 << j);
          }
        }
      }

      // Try shooting each tree
      for (int i = 0; i < n; i++) {
        int nextState = clearBit(newState, i);
        if (visited.contains(nextState)) continue;
        visited.add(nextState);
        prevIndex.put(nextState, i);
        prevState.put(nextState, state);
        q.add(nextState);
      }

    }

    // Ending state could not be reached
    return false;

  }

  // Sets a bit
  static boolean isSet(int set, int i) {
    return (set & (1 << i)) != 0L;
  }

  // Clears a bit
  static int clearBit(int set, int i)  {
    return set & ~(1 << i);
  }

}