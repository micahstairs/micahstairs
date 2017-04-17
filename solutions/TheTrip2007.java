/**
 * https://open.kattis.com/problems/trip2007
 * Author: Micah Stairs
 * Solved On: April 17, 2017
 * 
 * The key insight to this problem is realizing that the number of pieces we
 * need is equal to the maximum number of repetitions. So to make that easy to
 * to count we can simply sort the values. After this we can greedily assign the
 * bags in a round-robin fashion since the bags are now ordered by size.
 **/

import java.util.*;
import java.io.*;

public class TheTrip2007 {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    while (true) {
    
      // Get size of input
      int n = Integer.parseInt(br.readLine());
    
      // End of input
      if (n == 0) break;
      
      // Read and sort values
      String[] split = br.readLine().split(" ");
      int[] arr = new int[split.length];
      for (int i = 0; i < arr.length; i++) {
        arr[i] = Integer.parseInt(split[i]);
      }
      Arrays.sort(arr);

      // Find the maximum number of repetitions
      int maxCount = 0;
      int count = 0;
      int prev = -1;
      for (int val : arr) {
        if (val != prev) {
          count = 0;
        }
        count++;
        if (count > maxCount ) {
          maxCount = count;
        }
        prev = val;
      }

      // Create empty lists
      int nPieces = maxCount;
      List<List<Integer>> lists = new ArrayList<>();
      for (int i = 0; i < nPieces; i++) {
        lists.add(new ArrayList<Integer>());
      }

      // Assign bags to pieces in a round-robin fashion
      int nextPiece = 0;
      for (int val : arr) {
        lists.get(nextPiece).add(val);
        nextPiece = (nextPiece + 1) % nPieces;
      }

      // Build answer
      sb.append(nPieces + "\n");
      for (List<Integer> list : lists) {
        for (int i = 0; i < list.size(); i++) {
          if (i > 0) sb.append(" ");
          sb.append(list.get(i));
        }
        sb.append("\n");
      }
        
    }

    // Output answers
    System.out.print(sb);
  
  }

}