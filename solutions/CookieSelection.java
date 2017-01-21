/**
 * https://open.kattis.com/problems/cookieselection
 * Author: Micah Stairs
 * Solved On: January 20, 2017
 *
 * This is a pretty straight-forward problem, we want to maintain a sorted list of
 * numbers, and be able to remove the median element efficiently. One elegant way
 * to do this is by using both a max-heap and a min-heap.
 *
 * Each heap will hold half of the numbers, with the max-heap holding the smaller of
 * those numbers, and the min-heap holding the larger ones. This means that the median(s)
 * will be found in the roots of the heaps, allowing for O(log(n)) extractions.
 * When adding new numbers, we look at the roots of the heaps and place the new number
 * in the appropriate heap. At this point we need to compare the sizes of the heaps
 * and, if necessary, make them as equal as possible (by removing from one and putting
 * it in the other).
 **/

import java.util.*;
import java.io.*;

public class CookieSelection {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Heaps used to dynamically compute median
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

    // Process each command
    String line;
    while ((line = br.readLine()) != null) {
      
      // Send cookie to packaging
      if (line.charAt(0) == '#') {
        sb.append(minHeap.remove() + "\n");

      // Add cookie to proper heap
      } else {
        int size = Integer.parseInt(line);
        if (maxHeap.size() > 0 && size < maxHeap.peek()) {
          maxHeap.add(size);
        } else {
          minHeap.add(size);
        }
      }

      // Balance (by moving from min heap to max heap)
      if (minHeap.size() > maxHeap.size()) {
        maxHeap.add(minHeap.remove());
      }

      // Balance (by moving from max heap to min heap)
      if (maxHeap.size() > minHeap.size()) {
        minHeap.add(maxHeap.remove());
      }

    }

    // Output answer
    System.out.print(sb);
  
  }

}