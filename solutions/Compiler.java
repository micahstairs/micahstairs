/**
 * https://open.kattis.com/problems/compiler
 * Author: Micah Stairs
 * Solved On: March 15, 2017
 * 
 * I worked on this problem extensively with William Fiset. We tried a number of
 * different strategies to generate a list of instructions, but the challenging
 * part was doing it in 40 or less instructions.
 *
 * Initially we thought that representing the number in binary and then using a
 * series of instructions to generate that number would be good enough, but we
 * were just a few instructions over the limit for certain cases.
 *
 * Our approach in the end was to represent the input as a ternary number, using
 * register A as an accumulator, register X to store the constant 1, and register
 * Y to store the constant 2. For each digit, we would multiply the current result
 * in register A by 3 (which is done by pushing A onto the stack three times and
 * then doing 2 adds). And then depending on what the digit was, we would either 
 * add 0, 1, or 2 to the result.
 *
 * We had to add an optimization to handle cases beginning with a 2 differently
 * than cases beginning with a 1 in order to bring the number of instructions
 * under the limit (if the number does not begin with a 2 then we don't need to
 * pre-compute our constant 2, we can do it during the first multiplication of 3).
 **/

import java.util.*;
import java.io.*;

public class Compiler {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read input
    int n = Integer.parseInt(br.readLine());

    // Generate and output list of instructions
    for (String instruction : getInstructions(n)) {
      System.out.println(instruction);
    }

  }

  // Get list of instructions
  static List<String> getInstructions(int n) {

    List<String> list = new ArrayList<>();

    // Convert number to base 3
    char[] base3 = Integer.toString(n, 3).toCharArray();

    // Special case
    if (n == 0) {

      list.add("ZE A");
    
    // Number starts with a 2
    } else if (base3[0] == '2') {

      boolean first = true;
      list.add("ST X");
      list.add("PH X");
      list.add("PH X");
      list.add("AD");
      list.add("PL Y");
      list.add("PH Y");
      list.add("PL A");
      for (char digit :  base3) {
        if (first) {
          first = false;
        } else {
          list.add("PH A");
          list.add("PH A");
          list.add("PH A");
          list.add("AD");
          list.add("AD");
          if (digit == '1') {
            list.add("PH X");
            list.add("AD");
          } else if (digit == '2') {
            list.add("PH Y");
            list.add("AD");
          }
          list.add("PL A");
        }
      }

    // Number starts with a 1
    } else {

      boolean yStored = false;
      boolean first = true;
      list.add("ST X");
      list.add("ST A");
      for (char digit :  base3) {
        if (first) {
          first = false;
        } else {
          list.add("PH A");
          list.add("PH A");
          list.add("PH A");
          list.add("AD");
          if (!yStored) {
            list.add("PL Y");
            list.add("PH Y");
            yStored = true;
          }
          list.add("AD");
          if (digit == '1') {
            list.add("PH X");
            list.add("AD");
          } else if (digit == '2') {
            list.add("PH Y");
            list.add("AD");
          }
          list.add("PL A");
        }
      }

    }    

    list.add("DI A");

    return list;

  }

}