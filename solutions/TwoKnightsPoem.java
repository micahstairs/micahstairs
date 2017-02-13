/**
 * https://open.kattis.com/problems/twoknights
 * Author: Micah Stairs
 * Solved On: February 13, 2017
 * 
 * I briefly discussed this problem with Finn Lidbetter and William Fiset.
 * 
 * It is conceptually quite simple, we want to perform a search on the state
 * space to see if we can write the whole poem. With only 40 different keys
 * and a poem length of 100 characters, there are only 40 * 40 * 100 different
 * states. We can easily visit each of these states.
 *
 * This implementation using a DFS to explore the state space, but a BFS would
 * work just as well. I wrote my solution without using recursion but it could
 * have been done using recursion as well. The trickiest part of this problem
 * is representing the keyboard nicely and to control the movement that the
 * pair of knights are allowed to make around the keyboard. One mistake that
 * I made initially was that I missed the detail that both knights are not
 * allowed to be on the same key at the same time.
 **/

import java.util.*;
import java.io.*;

public class TwoKnightsPoem {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  // Keyboard
  static char[][] keyboardLowercase = {
    {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'},
    {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';'},
    {'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/'},
    {'^', '^', ' ', ' ', ' ', ' ', ' ', ' ', '^', '^'}
  };
  static char[][] keyboardUppercase = {
    {'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'},
    {'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', ':'},
    {'Z', 'X', 'C', 'V', 'B', 'N', 'M', '<', '>', '?'},
    {'^', '^', ' ', ' ', ' ', ' ', ' ', ' ', '^', '^'}
  };
  static final char SHIFT = '^';

  // Valid movement of knights
  static int[] dx = {-2, -1,  1,  2, 2, 1, -1, -2};
  static int[] dy = {-1, -2, -2, -1, 1, 2,  2,  1};

  public static void main(String[] args) throws IOException {

    // Process each test case
    String line;
    while (!(line = br.readLine()).equals("*")) {
       char[] poem = line.toCharArray();
       sb.append(String.format("%d\n", canType(poem) ? 1 : 0));
    }

    // Output answers
    System.out.print(sb);
  
  }

  // Check to see if the specified poem can be typed using a DFS
  static boolean canType(char[] poem) {

    // Setup DFS
    int n = poem.length;
    Set<State> visited = new HashSet<>();
    Stack<State> stack = new Stack<>();
    State startingState = new State(0, 3, 9, 3, 0);
    stack.add(startingState);
    visited.add(startingState);

    // Run DFS
    while (!stack.isEmpty()) {

      State state = stack.pop();

      // Try moving first knight
      for (int i = 0; i < dx.length; i++) {
        int newX = state.x1 + dx[i];
        int newY = state.y1 + dy[i];

        // The knights cannot occupy the same key
        if (newX == state.x2 && newY == state.y2) continue;

        // Out of bounds
        if (!inBounds(newX, newY)) continue;

        // No keys are typed if we about to hit a shift key
        if (keyboardLowercase[newY][newX] == SHIFT) {
          State newState = new State(newX, newY, state.x2, state.y2, state.index);
          if (!visited.contains(newState)) {
            stack.add(newState);
            visited.add(newState);
          }

        // Otherwise type a key only if it is needed for the poem
        } else {
          boolean shiftPressed = keyboardLowercase[state.y2][state.x2] == SHIFT;
          char charPressed = shiftPressed ? keyboardUppercase[newY][newX] : keyboardLowercase[newY][newX];
          if (charPressed == poem[state.index]) {
            int nextIndex = state.index + 1;
            if (nextIndex == n) return true;
            State newState = new State(newX, newY, state.x2, state.y2, nextIndex);
            if (!visited.contains(newState)) {
              stack.add(newState);
              visited.add(newState);
            }
          }
        }

      }

      // Instead of trying to move the second knight, we're just going to swap Knight 1 and Knight 2
      // since it requires us to write less code
      State newState = new State(state.x2, state.y2, state.x1, state.y1, state.index);
      if (!visited.contains(newState)) {
        stack.add(newState);
        visited.add(newState);
      }

    }

    return false;

  }

  // Returns true if the specified coordinates are in the bounds of the keyboard
  static boolean inBounds(int x, int y) {
    return x >= 0 && x <= 9 && y >= 0 && y <= 3;
  }

}

// Class used to represent a pair of locations on the keyboard as well as the
// index of the next character which needs to be typed
class State {

  int x1, y1, x2, y2;
  int index;

  public State(int x1, int y1, int x2, int y2, int index) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.index = index;
  }

  @Override public int hashCode() {
    return Objects.hash(x1, y1, x2, y2, index);
  }

  @Override public boolean equals(Object obj) {
    State state = (State) obj;
    return x1 == state.x1 && y1 == state.y1 && x2 == state.x2 && y2 == state.y2 && index == state.index;
  }

}