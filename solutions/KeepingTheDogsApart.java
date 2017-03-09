/**
 * https://open.kattis.com/problems/dogs
 * Author: Micah Stairs
 * Solved On: March 8, 2017
 * 
 * The solution to this problem was discussed with Finn Lidbetter and Will Fiset,
 * and then I went ahead and implemented it.
 *
 * A key insight was to add additional split points in the paths so that segments
 * could be paired together in such a way that the lines are the same length (since
 * both dogs travel at the same speed). Then we can iterate over each pair of lines
 * and do a ternary search on them in order to find the shortest distance between 
 * the dogs at a point in time.
 **/

import java.util.*;
import java.io.*;

public class KeepingTheDogsApart {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read in the first path
    int n = Integer.parseInt(br.readLine());
    Point[] pts1 = new Point[n];
    for (int i = 0; i < n; i++) {
      String[] line = br.readLine().split(" ");
      double x = Integer.parseInt(line[0]);
      double y = Integer.parseInt(line[1]);
      pts1[i] = new Point(x, y);
    }

    // Read in the second path
    int m = Integer.parseInt(br.readLine());
    Point[] pts2 = new Point[m];
    for (int i = 0; i < m; i++) {
      String[] line = br.readLine().split(" ");
      double x = Integer.parseInt(line[0]);
      double y = Integer.parseInt(line[1]);
      pts2[i] = new Point(x, y);
    }

    // Add split points to the paths so that the lines can be paired with equal distances
    List<Point> splitPts1 = new ArrayList<>();
    List<Point> splitPts2 = new ArrayList<>();
    splitPath(pts1, pts2, splitPts1);
    splitPath(pts2, pts1, splitPts2);
    int nPoints = splitPts1.size();

    // Find the answer by doing a ternary search on each pair of segments
    double min = Double.POSITIVE_INFINITY;
    for (int i = 0; i < nPoints - 1; i++) {
      double result = ternarySearch(splitPts1.get(i), splitPts1.get(i+1), splitPts2.get(i), splitPts2.get(i+1));
      min = Math.min(min, result);
    }

    // Output answer
    System.out.println(min);

  }

  // Add split points to the paths so that the lines can be paired with equal distances
  static void splitPath(Point[] pts1, Point[] pts2, List<Point> splitPts) {

    // Setup
    int n = pts1.length;
    int m = pts2.length;
    double[] lengths1 = getLengths(pts1);
    double[] lengths2 = getLengths(pts2);

    // Add first point
    splitPts.add(pts1[0]);

    // Move across paths
    int index1 = 0, index2 = 0;
    Point currentPt1 = pts1[0];
    while (index1 < n - 1 && index2 < m - 1) {

      // Lines are the same length
      if (roughlyEquals(lengths1[index1], lengths2[index2])) {

        // Add point
        splitPts.add(pts1[index1+1]);

        // Update positions
        index1++;
        index2++;
        currentPt1 = pts1[index1];

      // Other line is longer
      } else if (lengths1[index1] < lengths2[index2]) {
      
        // Add point
        splitPts.add(pts1[index1+1]);

        // Update position
        lengths2[index2] -= lengths1[index1];
        index1++;
        currentPt1 = pts1[index1];

      // This line is longer
      } else {

        // Compute split point
        double percentage = lengths2[index2] / lengths1[index1];
        double dx = pts1[index1+1].x - currentPt1.x;
        double dy = pts1[index1+1].y - currentPt1.y;
        Point newPoint = new Point(currentPt1.x + (dx * percentage), currentPt1.y + (dy * percentage));
        currentPt1 = newPoint;
        splitPts.add(newPoint);

        // Update position
        lengths1[index1] -= lengths2[index2];
        index2++;

      }

    }

  }

  // Check to see if the two values are roughly equal
  static boolean roughlyEquals(double a, double b) {
    return Math.abs(a - b) < 0.00000000001;
  }

  // Get the lengh
  static double[] getLengths(Point[] pts) {
    int n = pts.length;
    double[] lengths = new double[n - 1];
    for (int i = 0; i < n - 1; i++) {
      lengths[i] = dist(pts[i], pts[i+1]);
    }
    return lengths;
  }

  // Get the distance between two points
  static double dist(Point a, Point b) {
    double dx = a.x - b.x;
    double dy = a.y - b.y;
    return Math.sqrt(dx * dx + dy * dy);
  }

  // Use a ternary search to find the closest two points will get as they travel along the lines at a constant speed
  static double ternarySearch(Point a1, Point a2, Point b1, Point b2) {
    double len = dist(a1, a2);
    double low = 0, high = len;
    Double best = null;
    while (true) {
      double mid1 = (2 * low + high) / 3, mid2 = (low + 2 * high) / 3;
      double res1 = f(a1, a2, b1, b2, mid1 / len), res2 = f(a1, a2, b1, b2, mid2 / len);
      if (res1 > res2) low = mid1;
      else high = mid2;
      if (best != null && Math.abs(best - mid1) < 0.00001) break;
      best = mid1;
    }
    return f(a1, a2, b1, b2, best / len);
  }

  // Compute the distance between the two lines at a given time (expressed as a percentage along the lines)
  static double f(Point a1, Point a2, Point b1, Point b2, double percentage) {
    Point a = getPointAlongLine(a1, a2, percentage);
    Point b = getPointAlongLine(b1, b2, percentage);
    return dist(a, b);
  }

  // Get a point along a line using vectors
  static Point getPointAlongLine(Point a, Point b, double percentage) {
    double dx = b.x - a.x;
    double dy = b.y - a.y;
    return new Point(a.x + dx * percentage, a.y + dy * percentage);
  }

}

// Simple class used to represent a 2D point (Point2D.Double could have been used)
class Point {

  double x, y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

}