/**
 * https://open.kattis.com/problems/palindromicdna
 * Author: Micah Stairs
 * Solved On: January 23, 2017
 *
 * I initially worked on this problem with Finn Lidbetter, but our initial approach
 * was not quite right. Many important ideas came out of that discussion, however.
 *
 * This is actually quite a nice problem. First, we note that each palindromes is
 * just placing a set of equality requirements on pairs of indices. We can use
 * Union Find to find the groups of indices which must then be all equal. Within
 * each group, we can figure out which letters are currently present in that group.
 * 
 * If a group has 4 different letters, then it will be impossible to pick a letter
 * that each index can be switched to. If a group has 3 different letters then there
 * is exactly one letter we can choose. If a group has two different letters, then
 * there's actually two different possibilities. If the letters are adjacent (e.g. 'C'
 * and 'A') then we can either change one of the letters to the other or vice versa.
 * If those letters are opposites (e.g. 'T' and 'A') then everything must be changed
 * to one of those other two letters. If a group only has one unique letter then no
 * changes are needed.
 *
 * We can take this information (along with the restriction that adjacent indices cannot
 * be changed) and build a number of 2-SAT clauses. The variables present in these
 * clauses will represent "the letter in this index will be changed". We can can then
 * take these clauses and solve them efficiently with the help of Tarjan's algorithm.
 *
 * WARNING: In order to make this solution run fast enough, I had to scatter many
 * optimizations throughout my code. The code is therefore quite disgusting and
 * hacky (you should never be forced to represent an adjacency matrix in a BitSet).
 **/

import java.util.*;
import java.io.*;

public class PalindromicDNA {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  // Constants
  static final int N_LETTERS = 4;
  static final int MAX = 10000;
  static final int MAX_CLAUSES = 3 * MAX;

  // Variables in clauses represent "the letter in this index will be changed"
  static int[] a = new int[MAX_CLAUSES];
  static int[] b = new int[MAX_CLAUSES];

  // Space to be re-used for each test case
  static UnionFind uf = new UnionFind(MAX);
  static boolean[][] contrained = new boolean[MAX][N_LETTERS];
  static boolean[][] contrainedFromGroup = new boolean[MAX][N_LETTERS];
  static int[] originalLetter = new int[MAX];
  static int[] nChoicesForGroup = new int[MAX];
  static int[] head = new int[MAX];
  static int[] id = new int[MAX];
  static int[] lastId = new int[MAX];
  static int[] nextId = new int[MAX];

  public static void main(String[] args) throws IOException {

    // Process test cases
    outer: while (true) {
    
      // Read first line of input
      String[] line = br.readLine().split(" ");
      int n = Integer.parseInt(line[0]);
      int t = Integer.parseInt(line[1]);

      // End of input
      if (n == 0 && t == 0) break;

      // Store string as array of characters
      char[] arr = br.readLine().toCharArray();
      for (int i = 0; i < n; i++) {
        Arrays.fill(contrained[i], false);
        if (arr[i] == 'T') {
          originalLetter[i] = 0;
          contrained[i][2] = true;
        } else if (arr[i] == 'C') {
          originalLetter[i] = 1;
          contrained[i][3] = true;
        } else if (arr[i] == 'A') {
          originalLetter[i] = 2;
          contrained[i][0] = true;
        } else if (arr[i] == 'G') { 
          originalLetter[i] = 3;
          contrained[i][1] = true;
        }
      }

      // Parse and store palindromes as a set of constraints
      uf.reset();
      while (t-- > 0) {
        line = br.readLine().split(" ");
        for (int i = 1; i <= line.length / 2; i++) {
          int u = Integer.parseInt(line[i]);
          int v = Integer.parseInt(line[line.length - i]);
          uf.union(u, v);
        }
      }
      br.readLine();

      // Find the head of each group (the lowest index in each group of equalities),
      // and build an array which will allow us to efficiently hop to the next index
      // in that group
      Arrays.fill(nextId, -1);
      for (int i = 0; i < n; i++) {
        head[i] = uf.find(i);
        if (head[i] == i) {
          lastId[i] = i;
        } else {
          nextId[lastId[head[i]]] = i;
          lastId[head[i]] = i;
        }
        id[i] = i + 1;
      }

      // Summarize the constraints of each group
      for (int i = 0; i < n; i++) {
        Arrays.fill(contrainedFromGroup[i], false);
        for (int j = 0; j < N_LETTERS; j++) {
          contrainedFromGroup[head[i]][j] |= contrained[i][j];
        }
      }

      // Count the number of options that can be chosen
      Arrays.fill(nChoicesForGroup, N_LETTERS);
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < N_LETTERS; j++) {
          if (contrainedFromGroup[i][j]) {
            nChoicesForGroup[i]--;
          }
        }
        
        // Impossible if a group has no letter it can choose
        if (nChoicesForGroup[i] == 0) {
          sb.append("NO\n");
          continue outer;
        }

      }

      // Consecutive indices cannot be changed
      int nConstraints = 0;
      for (int i = 0; i < n - 1; i++) {

        // No need to add constraint since one of them doesn't need to change anyway
        if (nChoicesForGroup[head[i]] == 3 || nChoicesForGroup[head[i+1]] == 3) continue;

        a[nConstraints] = -id[i];
        b[nConstraints] = -id[i+1];
        nConstraints++;

      }

      // Place constraints on groups
      for (int i = 0; i < n; i++) {

        // Only one choice (add one constraint to each element in group)
        if (nChoicesForGroup[i] == 1) {

          // Find the letter which we are forced to use for this group
          int letter = 0;
          while (contrainedFromGroup[i][letter]) letter++;

          // Find all elements in the group
          int j = i;
          do {

            // Shouldn't be changed
            if (originalLetter[j] == letter) {
              a[nConstraints] = -id[j];
              b[nConstraints] = -id[j];
              nConstraints++;

            // Needs to be changed
            } else {
              a[nConstraints] = id[j];
              b[nConstraints] = id[j];
              nConstraints++;

              // Early stopping condition (since we can't change two adjacent letters)
              if (j + 1 == nextId[j] && originalLetter[j+1] == originalLetter[j]) {
                sb.append("NO\n");
                continue outer;
              }

            }
            j = nextId[j];
          } while (j != -1);

        // Two choices
        } else if (nChoicesForGroup[i] == 2) {

          // All elements will need to change their letters
          if (contrainedFromGroup[i][0] == contrainedFromGroup[i][2]) {
            int j = i;
            do {
              a[nConstraints] = id[j];
              b[nConstraints] = id[j];
              nConstraints++;
              j = nextId[j];
            } while (j != -1);

          // Only some of them will need to change their letters to match the other letter
          } else {

            // Add clauses to ensure one part of the group changes and the other does not
            int j = i;
            do {
              
              // XOR clause
              if (originalLetter[j] != originalLetter[i]) {
                a[nConstraints] = id[i];
                b[nConstraints] = id[j];
                nConstraints++;
                a[nConstraints] = -id[i];
                b[nConstraints] = -id[j];
                nConstraints++;

              // Equality clause
              } else {
                a[nConstraints] = id[i];
                b[nConstraints] = -id[j];
                nConstraints++;
                a[nConstraints] = -id[i];
                b[nConstraints] = id[j];
                nConstraints++;
              }
              j = nextId[j];
            } while (j != -1);

          }

        }

      }

      // Use 2-SAT to determine if it is possible to satisfy all of the contraints
      if (nConstraints == 0 || twoSat(a, b, n, nConstraints)) sb.append("YES\n");
      else sb.append("NO\n");
    
    }

    // Output all of the answers
    System.out.print(sb);
  
  }

  // 2-SAT snippet (modified to account for the fact that the arrays are padded,
  // and also uses a BitSet to reduce memory usage)
  static BitSet adj = new BitSet();
  static boolean twoSat(int[] a, int[] b, int n, int nConstraints) {
    int nNodes = n * 2;
    adj.clear();
    for (int i = 0; i < nConstraints; i++) {
      int index1 = index(-a[i],n) * nNodes + index(b[i],n);
      int index2 = index(-b[i],n) * nNodes + index(a[i],n);
      adj.set(index1);
      adj.set(index2);
    }
    Tarjan scc = new Tarjan(adj, nNodes);
    for (int i = 0; i < n; i++)
      if (scc.id[i] == scc.id[i + n])
        return false;
    return true;
  }
  static int index(int var, int n) {
    return var > 0 ? var - 1 : n - 1 - var;
  }

}

// Tarjan's Algorithm (Used to find Strongly Connected Components), modified
// to use a BitSet instead of an adjacency matrix in order to save space and
// includes a number of other optimizations
class Tarjan {
  BitSet adj;
  int n, pre, count = 0, stackIndex = 0;
  static final int MAX = PalindromicDNA.MAX_CLAUSES;
  static boolean[] marked = new boolean[MAX];
  static int[] id = new int[MAX], low = new int[MAX], stack = new int[MAX];
  public Tarjan(BitSet adj, int n) {
    this.n = n;
    this.adj = adj;
    Arrays.fill(marked, false);
    Arrays.fill(id, 0);
    Arrays.fill(low, 0);
    Arrays.fill(stack, 0);
    for (int u = 0; u < n; u++) if (!marked[u]) dfs(u);
  }
  void dfs(int u) {
    marked[u] = true;
    low[u] = pre++;
    int min = low[u];
    stack[stackIndex++] = u;
    int base = u * n;
    for (int v = 0; v < n; v++) {
      if (adj.get(base + v)) {
        if (!marked[v]) dfs(v);
        if (low[v] < min) min = low[v];
      }
    }
    if (min < low[u]) {
      low[u] = min;
      return;
    }
    int v;
    do {
      v = stack[--stackIndex];
      id[v] = count;
      low[v] = n;
    } while (v != u);
    count++;
  }
}

// Modified Union Find which makes it so that the root always has the smallest
// index in each group
class UnionFind {
  int[] id, sz;
  public UnionFind(int n) {
    id = new int[n];
    sz = new int[n];
  }
  void reset() {
    for (int i = 0; i < id.length; i++) { id[i] = i; sz[i] = 1; }
  }
  int find(int p) {
    int rt = p;
    while (rt != id[rt]) rt = id[rt];
    while (p != rt) { int next = id[p]; id[p] = rt; p = next; }
    return rt;
  }
  void union(int p, int q) {
    int r1 = find(p), r2 = find(q);
    if (r1 == r2) return;
    if (r1 > r2) { sz[r2] += sz[r1]; id[r1] = r2; }
    else { sz[r1] += sz[r2]; id[r2] = r1; }
  }
}