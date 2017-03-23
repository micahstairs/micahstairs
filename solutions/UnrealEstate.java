/**
 * https://open.kattis.com/problems/unrealestate
 * Author: Micah Stairs
 * Solved On: March 22, 2017
 * 
 * I spent a bit of time discussing this problem with William Fiset and Finn Lidbetter.
 * I implemented an algorithm which I initially believed to be O(n^2) but it may have been
 * O(n^2logn). This involved a line sweeping algorithm, but avoided the need of using a 
 * segment tree. This approach was too slow.
 *
 * So I had to improve it to O(nlogn) by using the segment tree. Since we are
 * working with real values, it was necessary to use coordinate compression.
 * Lazy propagation is, of course, required in this situation since we are doing
 * both range updates and queries. This particular segment tree keeps track of the
 * minimum value in each interval and a length indicating how much the minimum value
 * occurs.
 **/

import java.util.*;
import java.io.*;

public class UnrealEstate {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read in the number of rectangles
    int n = Integer.parseInt(br.readLine());

    // Read in the rectangles
    Rect[] rects = new Rect[n];
    for (int i = 0; i < n; i++) {
      String[] line = br.readLine().split(" ");
      double x1 = Double.parseDouble(line[0]);
      double y1 = Double.parseDouble(line[1]);
      double x2 = Double.parseDouble(line[2]);
      double y2 = Double.parseDouble(line[3]);
      rects[i] = new Rect(x1, y1, x2, y2);
    }
  
    // Compute and output the area of the union of these rectangles
    System.out.printf("%.2f\n", getAreaOfUnion(rects));    
  
  }

  static double getAreaOfUnion(Rect[] rects) {

    // Setup
    int n = rects.length;
    Set<Double> ySet = new HashSet<>();
    Queue<Rect> qLeftX = new PriorityQueue<>(new XSortLeft());
    Queue<Rect> qRightX = new PriorityQueue<>(new XSortRight());
    for (int i = 0; i < n; i++) {
      qLeftX.add(rects[i]);
      qRightX.add(rects[i]);
      ySet.add(rects[i].y1);
      ySet.add(rects[i].y2);
    }
    List<Double> yList = new ArrayList<>(ySet);
    Collections.sort(yList);
    Node root = new Node(yList, 0, yList.size() - 1);
    double range = root.maxPos - root.minPos;
    Set<Rect> activeSet = new HashSet<>();
    Double previousX = null;
    double area = 0;
    
    // Line sweep
    while (!qRightX.isEmpty()) {

      double minXLeft = qLeftX.isEmpty() ? Double.POSITIVE_INFINITY : qLeftX.peek().x1;
      double minXRight = qRightX.peek().x2;
      double minX = Math.min(minXLeft, minXRight);

      // Add area
      if (previousX == null) {
        previousX = minX;
      } else {
        double heightUnion = root.min == 0 ? range - root.length : range;
        area += (minX - previousX) * heightUnion;
      }

      // Remove from active set
      while (!qRightX.isEmpty() && qRightX.peek().x2 == minX) {
        Rect rect = qRightX.remove();
        root.update(rect.y1, rect.y2, -1);
        activeSet.remove(rect);
      }
      
      // Add to active set
      while (!qLeftX.isEmpty() && qLeftX.peek().x1 == minX) {
        Rect rect = qLeftX.remove();
        root.update(rect.y1, rect.y2, +1);
        activeSet.add(rect);
      }

      previousX = minX;

    }

    return area;

  }

}

class Rect {
  double x1, y1, x2, y2;
  public Rect(double x1, double y1, double x2, double y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }
}

class XSortLeft implements Comparator<Rect> {
  @Override public int compare(Rect a, Rect b) {
    return Double.compare(a.x1, b.x1);
  }
}

class XSortRight implements Comparator<Rect> {
  @Override public int compare(Rect a, Rect b) {
    return Double.compare(a.x2, b.x2);
  }
}

class Node {
  double minPos, maxPos, min = 0, length;
  int lazy = 0;
  Node left, right;
  public Node(List<Double> vals, int start, int end) {
    minPos = vals.get(start);
    maxPos = vals.get(end);
    length = maxPos - minPos;
    if (start + 1 == end) left = right = null;
    else {
      int range = end - start, mid = start + range / 2;
      left = new Node(vals, start, mid);
      right = new Node(vals, mid, end);
    }
  }
  public void update(double l, double r, int change) {
    lazyUpdates();
    if (l <= minPos && maxPos <= r) {
      min += change;
      if (left  != null) left.lazy += change;
      if (right != null) right.lazy += change;
    } else if (!(r <= minPos || l >= maxPos)) {
      left.update(l, r, change);
      right.update(l, r, change);
      min = Math.min(left.min, right.min);
      length = 0;
      if (left.min == min) length += left.length;
      if (right.min == min) length += right.length;
    }
  }
  private void lazyUpdates() {
    min += lazy;
    if (left != null) left.lazy += lazy;
    if (right != null) right.lazy += lazy;
    lazy = 0;
  }

}