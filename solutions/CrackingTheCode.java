/**
 * https://open.kattis.com/problems/crackingthecode
 * Author: Micah Stairs
 * Solved On: January 19, 2017
 *
 * I discussed this solution with Olivier Bourgeois and Finn Lidbetter, before
 * actually going ahead and implementing it. The important trick is to turn each
 * of the strings into a canonical form (except the message we need to decode).
 * This form will assign each letter a unique number, where the numbers get assigned
 * based on when that character is first seen relative to the other letters. For
 * example, we would turn "abccdeb" into [0,1,2,2,3,4,5]. Looking at other strings
 * from the 2nd sample input these would also turn into the same representation.
 * 
 * If a encoded message has a different canonical form than the decoded message, then
 * that encoded message cannot possibly match. If none of them match, then we output
 * "IMPOSSIBLE". Otherwise, we need to go through the matches and find spots where
 * the characters are all the same (which gives us a mapping from an encoded character
 * to a decoded character). At the end, if we have found 25 mappings, we can
 * automatically infer the last mapping (since we are only using 26 letters). We can
 * finally decode the message now.
 **/

import java.util.*;
import java.io.*;

public class CrackingTheCode {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.parseInt(br.readLine());
    while (t-- > 0) {
      
      // Store encoded messages, and generate canonical forms
      int n = Integer.parseInt(br.readLine());
      char[][] encoded = new char[n][];
      int[][] encodedCanonical = new int[n][];
      for (int i = 0; i < n; i++) {
        encoded[i] = br.readLine().toCharArray();
        encodedCanonical[i] = toCanonical(encoded[i]);
      }

      // Store decoded message, and generate canonical form
      char[] decoded = br.readLine().toCharArray();
      int[] decodedCanonical = toCanonical(decoded);

      // Store message to decode
      char[] message = br.readLine().toCharArray();

      // Find the indicies of the possible matches
      List<Integer> matches = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        if (Arrays.equals(encodedCanonical[i], decodedCanonical)) {
          matches.add(i);
        }
      }

      // No matches, so it is impossible
      if (matches.size() == 0) {
        sb.append("IMPOSSIBLE\n");

      // Possible to solve at least some of the characters
      } else {

        // Generate known mappings
        Map<Character, Character> map = new HashMap<>();
        int m = decoded.length;
        boolean[] seenInEncoded = new boolean[26];
        boolean[] seenInDecoded = new boolean[26];
        outer: for (int i = 0; i < m; i++) {
          char ch = encoded[matches.get(0)][i];
          for (int j : matches) {
            if (encoded[j][i] != ch) {
              continue outer;
            }
          }
          map.put(ch, decoded[i]);
          seenInEncoded[ch - 'a'] = true;
          seenInDecoded[decoded[i] - 'a'] = true;
        }

        // If we have mappings for 25 characters then we can infer the last mapping
        if (map.size() == 25) {
          char missingEncoded = 0;
          char missingDecoded = 0;
          for (int i = 0; i < 26; i++) {
            if (!seenInDecoded[i]) missingDecoded = (char) ('a' + i);
            if (!seenInEncoded[i]) missingEncoded = (char) ('a' + i);
          }
          map.put(missingEncoded, missingDecoded);
        }

        // Decode message
        for (int i = 0; i < message.length; i++) {
          if (map.containsKey(message[i])) {
            sb.append(map.get(message[i]));
          } else {
            sb.append("?");
          }
        }
        sb.append("\n");
        
      }
    
    }

    // Output answer
    System.out.print(sb);
  
  }

  // Convert the sequence of characters to a canonical format
  // Ex: [a,b,d,d,t,c,a] -> [0,1,2,2,3,4,0]
  static int[] toCanonical(char[] chars) {
    int n = chars.length;
    int[] canonical = new int[n];
    int nextNumber = 0;
    Map<Character, Integer> map = new HashMap<>();
    for (int i = 0; i < n; i++) {
      if (!map.containsKey(chars[i])) {
        map.put(chars[i], nextNumber++);
      }
      canonical[i] = map.get(chars[i]);
    }
    return canonical;
  }

}