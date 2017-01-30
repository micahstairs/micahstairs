/**
 * https://open.kattis.com/problems/turbo
 * Author: Micah Stairs
 * Solved On: January 30, 2017
 *
 * This is a very straight-forward problem, however an efficient solution is
 * needed. The usage of a Fenwick Tree with range updates and point queries
 * allows us to efficiently alter ranges of indices (this necessary since
 * potentially many numbers are moved to the left or to the right by one
 * position whenever a number is moved it its proper spot).
 **/

import java.util.*;
import java.io.*;

public class Turbo {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read and store input
    int n = Integer.parseInt(br.readLine());
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = Integer.parseInt(br.readLine());
    }

    // Compute 1-based position of each number
    // NOTE: This tells you where to look in the Fenwick tree
    int[] pos = new int[n+1];
    for (int i = 0; i < n; i++) {
      pos[arr[i]] = i + 1;
    }

    // This Fenwick Tree is used to efficiently alter ranges of indices (this
    // necessary since potentially many numbers are moved to the left or to
    // the right by one position whenever a number is moved it its proper spot)
    FenwickTree2 ft = new FenwickTree2(n);
    for (int i = 1; i <= n; i++) {
      ft.add(i, i, i);
    }

    // Process the numbers in the specified order (1, n, 2, n-1, etc.)
    int low = 1;
    int high = n;
    while (low <= high) {

      // Low index
      int index = pos[low];
      int nMoves = ft.get(index) - low;
      sb.append(nMoves + "\n");
      ft.add(1, index, 1);

      // High index
      if (low != high) {
        index = pos[high];
        nMoves = high - ft.get(index);
        sb.append(nMoves + "\n");
        ft.add(index, n, -1);
      }

      // Move to next pair of indices
      low++;
      high--;

    }

    // Output answers
    System.out.print(sb);
  
  }

}

// Point updates, range queries
class FenwickTree {
  int[] arr;
  public FenwickTree(int n) {
    arr = new int[n + 1];
  }
  int intervalSum(int i, int j) { // [i,j]
    return sum(j) - sum(i - 1);
  }
  int sum(int i) { // [1,i]
    int sum = 0;
    while (i > 0) {
      sum += arr[i];
      i -= i & -i;
    }
    return sum;
  }
  void add(int i, int delta) {
    if (i <= 0) return;
    while (i < arr.length) {
      arr[i] += delta;
      i += i & -i;
    }
  }
}

// Range updates, point queries
class FenwickTree2 {
  FenwickTree ft;
  public FenwickTree2(int n) {
    ft = new FenwickTree(n + 1);
  }
  int get(int i) {
    return ft.sum(i);
  }
  void add(int i, int j, int delta) { // Change [i,j] by delta
    ft.add(i, delta);
    ft.add(j + 1, -delta);
  }
}