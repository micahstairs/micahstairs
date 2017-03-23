/**
 * https://open.kattis.com/problems/baza
 * Author: Micah Stairs
 * Solved On: March 22, 2017
 * 
 * I worked on this problem with Lucas Wood. We initially came up with an approach
 * which involved getting substrings of every possible prefix, which proved to be too
 * slow. Lucas suggested we use a Trie to more compactly represent the substrings and
 * this was fast enough.
 *
 * Our general approach is to read in the input, storing mappings from each word in the
 * database to the index that it appears. We also insert each of these words into a trie.
 * Each node in our trie has been augmented with a list containing the indicies of the
 * words in the database who have a prefix ending at this node. These indicies are in
 * sorted order.
 *
 * As we process the queries, we check to see if the word exists. Otherwise we know that
 * we have to look through the entire list. We update our count appropriately. We traverse
 * the trie as we process each character in this query word. At each step, we examine the
 * list of indicies in this node and do a binary search to see how many elements in this
 * list fall before or on the number of entries we need to look through the list for. This
 * gives us a number to add to our count.
 **/

import java.util.*;
import java.io.*;

public class Baza {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read and store input
    int n = Integer.parseInt(br.readLine());
    Map<String, Integer> wordToIndex = new HashMap<>();
    char[][] database = new char[n][];
    for (int i = 0; i < n; i++) {
      String word = br.readLine();
      wordToIndex.put(word, i);
      database[i] = word.toCharArray();
    }

    // Build trie
    Node root = new Node();
    for (int i = 0; i < n; i++) {
      root.insert(database[i], 0, i);
    }

    // Process queries
    int q = Integer.parseInt(br.readLine());
    while (q-- > 0) {
      String word = br.readLine();
      int indexOfWord = wordToIndex.containsKey(word) ? wordToIndex.get(word) : n;
      int count = wordToIndex.containsKey(word) ? wordToIndex.get(word) + 1 : n;
      Node node = root;
      for (int j = 0; j < word.length(); j++) {
        node = node.getChild(word.charAt(j));
        int result = Collections.binarySearch(node.indicies, indexOfWord);
        if (result >= 0) count += result + 1;
        else count += -result - 1;
      }
      sb.append(count + "\n");
    }
    
    // Output answers
    System.out.print(sb);
    
  }

}

// Trie node
class Node {

  List<Integer> indicies = new ArrayList<>();
  Node[] children = new Node[26];

  public void insert(char[] arr, int nextIndex, int associatedIndex) {
    if (nextIndex > 0) indicies.add(associatedIndex);
    if (nextIndex < arr.length) {
      getChild(arr[nextIndex]).insert(arr, nextIndex + 1, associatedIndex);
    }
  }

  public Node getChild(char ch) {
    int index = getIndex(ch);
    if (children[index] == null) children[index] = new Node();
    return children[index];
  }

  private static int getIndex(char ch) {
    return ch - 'a';
  }

}