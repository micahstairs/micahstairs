/**
 * https://open.kattis.com/problems/ograda
 * Author: Micah Stairs
 * Solved On: May 8, 2017
 * 
 * This problem is pretty straight-forward except I needed to take advantage of
 * segment trees and binary searches in order to reduce the complexity of my
 * solution to O(nlog(n)). It seems plausible to me that this solution could be
 * simplified in order to eliminate the binary searches, however the complexity
 * would remain unchanged.
 **/

import java.util.*;
import java.io.*;

public class Ograda {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read first line of input
    String[] line = br.readLine().split(" ");
    int n = Integer.parseInt(line[0]);
    int x = Integer.parseInt(line[1]);

    // Read in heights of planks and compute total area
    long totalArea = 0;
    line = br.readLine().split(" ");
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = Integer.parseInt(line[i]);
      totalArea += arr[i];
    }

    // Store heights in segment tree
    MinSegmentTree st1 = new MinSegmentTree(n);
    for (int i = 0; i < n; i++) {
      st1.modify(i, arr[i]);
    }

    // Compute the heights of valid strokes
    int w = n - x + 1;
    MinSegmentTree st2 = new MinSegmentTree(w);
    for (int i = 0; i < w; i++) {
      st2.modify(i, st1.min(i, i + x)); 
    }

    // Pick out the needed strokes
    int lastHeight = 0;
    int paintEnd = 0;
    List<Integer> startingPoints = new ArrayList<>();
    List<Integer> endingPoints = new ArrayList<>();
    List<Integer> heights = new ArrayList<>();
    for (int i = 0; i < w; i++) {
      int height =  st2.min(i, i + 1);
      int nextHeight = i == w - 1 ? -1 : st2.min(i + 1, i + 2);
      if (lastHeight < height || height > nextHeight || i >= paintEnd || i == w - 1) {
        startingPoints.add(i);
        endingPoints.add(i + x - 1);
        heights.add(height);
        paintEnd = i + x;
        lastHeight = height;
      }
    }

    // Compute painted area
    int nStrokes = startingPoints.size();
    MaxSegmentTree st3 = new MaxSegmentTree(nStrokes);
    for (int i = 0; i < nStrokes; i++) {
      st3.modify(i, heights.get(i));
    }
    long areaPainted = 0;
    for (int i = 0; i < n; i++) {
      int first = Collections.binarySearch(endingPoints, i);
      if (first < 0) first = (-(first + 1));
      int last = Collections.binarySearch(startingPoints, i);
      if (last < 0) last = (-(last + 1)) - 1;
      areaPainted += st3.max(first, last + 1);
    }

    // Output answers
    System.out.println(totalArea - areaPainted);
    System.out.println(nStrokes);
    
  }
}

// Segment Tree with min operation
class MinSegmentTree {

  private int[] min;
  private int n;

  public MinSegmentTree(int n) {
    min = new int[2 * n];
    this.n = n;
  }

  // Modify point
  public void modify(int p, int value) {
    for (min[p += n] += value; p > 1; p >>= 1) {
      min[p>>1] = Math.min(min[p], min[p^1]);
    }
  }
  
  // Get min of interval [l, r)
  public int min(int l, int r) {
    int res = Integer.MAX_VALUE;
    for (l += n, r += n; l < r; l >>= 1, r >>= 1) {
      if ((l&1) != 0) {
        if (min[l] < res) res = min[l];
        l++;
      }
      if ((r&1) != 0) {
        --r;
        if (min[r] < res) res = min[r];
      }
    }
    return res;
  }

}

// Segment Tree with max operation
class MaxSegmentTree {

  private int[] max;
  private int n;

  public MaxSegmentTree(int n) {
    max = new int[2 * n];
    this.n = n;
  }

  // Modify point
  public void modify(int p, int value) {
    for (max[p += n] += value; p > 1; p >>= 1) {
      max[p>>1] = Math.max(max[p], max[p^1]);
    }
  } 
  
  // Get max of interval [l, r)
  public int max(int l, int r) {
    int res = Integer.MIN_VALUE;
    for (l += n, r += n; l < r; l >>= 1, r >>= 1) {
      if ((l&1) != 0) {
        if (max[l] > res) res = max[l];
        l++;
      }
      if ((r&1) != 0) {
        --r;
        if (max[r] > res) res = max[r];
      }
    }
    return res;
  }

}