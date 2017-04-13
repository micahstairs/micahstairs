/**
 * https://open.kattis.com/problems/babylonian
 * Author: Micah Stairs
 * Solved On: April 1, 2017
 * 
 * The hardest part of this problem is parsing the input since we can't
 * simply split by commas (otherwise we get strange behaviour when two
 * commas are adjacent). To overcome this problem, I parsed the input
 * character by character instead.
 **/

import java.util.*;
import java.io.*;

public class BabylonianNumbers {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    int t = Integer.parseInt(br.readLine());
    while (t-- > 0) {
      int number = 0;
      long result = 0;
      for (char ch : br.readLine().toCharArray()) {
        if (ch == ',') {
          result *= 60L;
          result += number;
          number = 0;
        } else {
          number *= 10;
          number += (ch - '0');
        }
      }
      result *= 60L;
      result += number;
      System.out.println(result);
    
    }

  }

}