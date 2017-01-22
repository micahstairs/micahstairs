/**
 * https://open.kattis.com/problems/artwork
 * Author: Micah Stairs
 * Solved On: January 22, 2017
 *
 * The key insight to this problem is that we should process the queries backwards.
 * This means we can use Union Find to keep track of how many connected regions
 * there are after each query. The only thing is that we need to ignore all of the
 * black cells.
 *
 * Before we process the queries backwards, we need to go through them forwards and
 * keep track of the time when each cell first went black. Then we can create the
 * Union Find structure and union adjacent white cells together. When we are going
 * through the queries backwards and we turn a black cell into a white one, then
 * all we need to do is union it with adjacent white cells.
 **/

import java.util.*;
import java.io.*;

public class Artwork {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  static int w, h;

  static int[] dx = {-1, 1, 0, 0};
  static int[] dy = {0, 0, -1, 1};

  public static void main(String[] args) throws IOException {

    // Read first line of input
    String[] line = br.readLine().split(" ");
    w = Integer.parseInt(line[0]);
    h = Integer.parseInt(line[1]);
    int q = Integer.parseInt(line[2]);

    // Read and store queries
    int[] x1 = new int[q];
    int[] y1 = new int[q];
    int[] x2 = new int[q];
    int[] y2 = new int[q];
    for (int i = 0; i < q; i++) {
      line = br.readLine().split(" ");
      x1[i] = Integer.parseInt(line[0]) - 1;
      y1[i] = Integer.parseInt(line[1]) - 1;
      x2[i] = Integer.parseInt(line[2]) - 1;
      y2[i] = Integer.parseInt(line[3]) - 1;
    }

    // Figure out what the board will look like at the end and store the
    // when that cell first got coloured
    boolean[][] black = new boolean[h][w];
    int[][] time = new int[h][w];
    for (int i = 0; i < h; i++) Arrays.fill(time[i], -1);
    for (int i = 0; i < q; i++) {
      int x = x1[i];
      int y = y1[i];
      boolean horizontal = x1[i] != x2[i];
      while (x <= x2[i] && y <= y2[i]) {
        black[y][x] = true;
        if (time[y][x] == -1) time[y][x] = i;
        if (horizontal) x++;
        else y++;
      }
    }

    // Setup union find to represent the ending configuration
    int nCells = w * h;
    int nBlackCells = 0;
    UnionFind uf = new UnionFind(nCells);
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        if (black[y][x]) {
          nBlackCells++;
        } else {
          if (x + 1 < w && !black[y][x + 1]) uf.union(getIndex(x, y), getIndex(x + 1, y));
          if (y + 1 < h && !black[y + 1][x]) uf.union(getIndex(x, y), getIndex(x, y + 1));
        }
      }
    }

    // Process queries backwards
    List<Integer> answers = new ArrayList<>();
    for (int i = q - 1; i >= 0; i--) {
      answers.add(uf.nSets - nBlackCells);
      int x = x1[i];
      int y = y1[i];
      boolean horizontal = x1[i] != x2[i];
      while (x <= x2[i] && y <= y2[i]) {
        if (time[y][x] == i) {
          nBlackCells--;
          for (int j = 0; j < 4; j++) {
            int newX = x + dx[j];
            int newY = y + dy[j];
            if (newX >= 0 && newX < w && newY >= 0 && newY < h && !black[newY][newX]) {
              uf.union(getIndex(x, y), getIndex(newX, newY));
            }
          }
          black[y][x] = false;
        }
        if (horizontal) x++;
        else y++;
      }
    }

    // Reverse the order of the answer and output them
    Collections.reverse(answers);
    for (int answer : answers) {
      sb.append(answer + "\n");
    }
    System.out.print(sb);
  
  }

  // Return the index associated with a particular cell
  static int getIndex(int x, int y) {
    return y * w + x;
  }

}

// Union Find
class UnionFind {
  int[] id, sz;
  int nSets;
  public UnionFind(int n) {
    nSets = n;
    id = new int[n]; sz = new int[n];
    for (int i = 0; i < n; i++) { id[i] = i; sz[i] = 1; }
  }
  int find(int p) {
    int rt = p;
    while (rt != id[rt]) rt = id[rt];
    while (p != rt) { int next = id[p]; id[p] = rt; p = next; }
    return rt;
  }
  boolean connected(int p, int q) {
    return find(p) == find(q);
  }
  int getSize(int p) {
    return sz[find(p)];
  }
  void union(int p, int q) {
    int r1 = find(p), r2 = find(q);
    if (r1 == r2) return;
    nSets--;
    if (sz[r1] < sz[r2]) { sz[r2] += sz[r1]; id[r1] = r2; }
    else { sz[r1] += sz[r2]; id[r2] = r1; }
  }
}