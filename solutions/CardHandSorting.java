/**
 * https://open.kattis.com/problems/cardhand
 * Author: Micah Stairs
 * Solved On: March 6, 2017
 * 
 * The solution to this problem was discussed with Finn Lidbetter and Will Fiset,
 * and then I went ahead and implemented it.
 *
 * The basic idea is that we are trying all permutations of suits and all possible
 * combinations of ascending/descending for each of the suits. For each of these, we
 * map the cards to an integer which represents its position in this defined order.
 * We can then find the longest increasing subsequence in this new list of integers,
 * which tells us how many cards do not need to be moved. We will be taking the best
 * answer from all of these results.
 **/

import java.util.*;
import java.io.*;

public class CardHandSorting {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

  public static void main(String[] args) throws IOException {

    // Read in cards
    int n = Integer.parseInt(br.readLine());
    String[] line = br.readLine().split(" ");
    Card[] cardHand = new Card[n];
    List<Card> cardList = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      cardHand[i] = new Card(line[i].charAt(0), line[i].charAt(1));
      cardList.add(cardHand[i]);
    }
    
    // Try each ordering of suits and ascending/descending combinations
    int min = Integer.MAX_VALUE;
    Character[] suitOrder = {'c', 'd', 'h', 's'};
    do {
      for (int i = 0; i < 2*2*2*2; i++) {
        List<Integer> cardOrder = new ArrayList<>();
        for (int j = 0; j < n; j++) {
          cardOrder.add(cardHand[j].getOrderIndex(suitOrder, isSet(i, 0), isSet(i, 1), isSet(i, 2), isSet(i, 3)));
        }
        int sequenceLength = longestIncreasingSubsequence(cardOrder).size();
        min = Math.min(min, n - sequenceLength);
      }
    } while (nextPermutation(suitOrder));

    // Output minimal answer
    System.out.println(min);

  }

  static boolean isSet(int set, int i) {
    return (set & (1 << i)) != 0;
  }

  // Next permutation snippet
  static <T extends Comparable<? super T>> boolean nextPermutation(T[] c) {
    int first = getFirst(c);
    if (first == -1) return false;
    int toSwap = c.length - 1;
    while (c[first].compareTo(c[toSwap]) >= 0) --toSwap;
    swap(c, first++, toSwap); toSwap = c.length - 1;
    while (first < toSwap) swap(c, first++, toSwap--);
    return true;
  }
  static <T extends Comparable<? super T>> int getFirst(T[] c) {
    for (int i = c.length - 2; i >= 0; --i)
      if (c[i].compareTo(c[i + 1]) < 0) return i;
    return -1;
  }
  static <T extends Comparable<? super T>> void swap(T[] c, int i, int j) {
    T tmp = c[i]; c[i] = c[j]; c[j] = tmp;
  }

  // Longest increasing subsequence snippet
  static <T extends Comparable<? super T>> List <Integer> longestIncreasingSubsequence(List<T> seq) {
    if (seq.size() == 0) return new ArrayList<Integer>();
    List<Integer> subseqLen = new ArrayList<Integer>(), parent = new ArrayList<Integer>();
    for (int i = 0; i < seq.size(); i++) parent.add(null);
    for (int i = 0; i < seq.size(); i++) {
      if (subseqLen.size() == 0 || seq.get(i).compareTo(seq.get(subseqLen.get(subseqLen.size() - 1))) > 0) {
        if (subseqLen.size() > 0) parent.set(i, subseqLen.get(subseqLen.size() - 1));
        subseqLen.add(i);
      } else {
        int index = findNext(seq, subseqLen, i);
        subseqLen.set(index, i);
        if (index != 0) parent.set(i, subseqLen.get(index - 1)); } }
    List<Integer>result = new ArrayList<Integer>();
    Integer curParent = subseqLen.get(subseqLen.size() - 1);
    while (curParent != null) {
      result.add(curParent);
      curParent = parent.get(curParent); }
    Collections.reverse(result);
    return result; }
  static<T extends Comparable <? super T>> int findNext(List <T> seq, List<Integer> subSeq, int elem) {
    int low = 0, high = subSeq.size() - 1;
    while (high > low) {
      int mid = (high + low) / 2;
      if (seq.get(subSeq.get(mid)).compareTo(seq.get(elem)) < 0) low = mid + 1;
      else high = mid; }
    return high; }

}

class Card {

  int value;
  char suit;

  public Card(char value, char suit) {
    this.value = getValue(value);
    this.suit = suit;
  }

  // Given an ordering of suits and their ascending/descending properties, get the position of this card
  public int getOrderIndex(Character[] suitOrder, boolean cIncreasing, boolean dIncreasing, boolean hIncreasing, boolean sIncreasing) {
    int suitIndex = getSuitIndex(suitOrder, suit);
    switch (suit) {
      case 'c': return suitIndex * 13 + (cIncreasing ? value : 12 - value);
      case 'd': return suitIndex * 13 + (dIncreasing ? value : 12 - value);
      case 'h': return suitIndex * 13 + (hIncreasing ? value : 12 - value);
      case 's': return suitIndex * 13 + (sIncreasing ? value : 12 - value);
    }
    throw new IllegalStateException();
  }

  // Map the value of the card to an integer in the range [0,12]
  static int getValue(char value) {
    switch (value) {
      case 'T': return 8;
      case 'J': return 9;
      case 'Q': return 10;
      case 'K': return 11;
      case 'A': return 12;
      default: return (int) (value - '2');
    }
  }

  // Get the index of the suit
  static int getSuitIndex(Character[] suitOrder, char suit) {
    for (int i = 0; i < suitOrder.length; i++) {
      if (suitOrder[i] == suit) {
        return i;
      }
    }
    throw new IllegalStateException();
  }

}