/**
 * https://open.kattis.com/problems/daceydice
 * Author: Micah Stairs
 * Solved On: April 5, 2017
 * 
 * This problem is conceptually simple. We want to see if we can get from the
 * starting state to an ending state. It doesn't matter whether we use a BFS
 * or DFS. The tricky part of this implementation is nicely representing the
 * state of the dice, and handling all of the rotations properly.
 **/

import java.util.*;
import java.io.*;

public class DaceyTheDice {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Process each test case
    int t = Integer.parseInt(br.readLine());
    while (t-- > 0) {
         
      // Store input 
      int n = Integer.parseInt(br.readLine());
      char[][] arr = new char[n][n];
      for (int y = 0; y < n; y++) {
        arr[y] = br.readLine().toCharArray();
      }

      // Find the starting and ending position and the blocked cells
      boolean[][] blocked = new boolean[n][n];
      int startX = -1, startY = -1, endX = -1, endY = -1;
      for (int y = 0; y < n; y++) {
        for (int x = 0; x < n; x++) {
          if (arr[y][x] == 'S') {
            startX = x;
            startY = y;
          } else if (arr[y][x] == 'H') {
            endX = x;
            endY = y;
          } else if (arr[y][x] == '*') {
            blocked[y][x] = true;
          }
        }
      }

      // Check if end is reachable from the start and append answer
      if (dfs(blocked, n, startX, startY, endX, endY)) sb.append("Yes\n");
      else sb.append("No\n");
        
    }    

    // Output answers
    System.out.print(sb);
  
  }

  // Do a DFS from the start to the end
  static boolean dfs(boolean[][] blocked, int n, int startX, int startY, int endX, int endY) {

    // Setup
    Set<State> visited = new HashSet<>();
    Stack<State> stack = new Stack<>();

    // Add initial state
    stack.add(new State(new DiceState(6, 5, 1, 2, 3, 4), startX, startY));

    // Do the DFS
    while (!stack.isEmpty()) {

      State state = stack.pop();

      // Reached end
      if (state.x == endX && state.y == endY && state.diceState.d == 5) return true;

      // Already visited
      if (visited.contains(state)) continue;
      visited.add(state);

      // Go west
      if (state.x > 0 && !blocked[state.y][state.x-1]) {
        stack.push(new State(state.diceState.west(), state.x - 1, state.y));
      }

      // Go east
      if (state.x < n - 1 && !blocked[state.y][state.x+1]) {
        stack.push(new State(state.diceState.east(), state.x + 1, state.y));
      }

      // Go north
      if (state.y > 0 && !blocked[state.y-1][state.x]) {
        stack.push(new State(state.diceState.north(), state.x, state.y - 1));
      }

      // Go south
      if (state.y < n - 1 && !blocked[state.y+1][state.x]) {
        stack.push(new State(state.diceState.south(), state.x, state.y + 1));
      }

    }

    // End could not be reached
    return false;

  }

}

class State {

  DiceState diceState;
  int x, y;

  public State(DiceState diceState, int x, int y) {
    this.diceState = diceState;
    this.x = x;
    this.y = y;
  }

  @Override public boolean equals(Object obj) {
    State state = (State) obj;
    return diceState.equals(state.diceState) && x == state.x && y == state.y;
  }

  @Override public int hashCode() {
    return Objects.hash(diceState, x, y);
  }

}

class DiceState {

  int d, l, u, r, f, b;

  public DiceState(int d, int l, int u, int r, int f, int b) {
    this.d = d;
    this.l = l;
    this.u = u;
    this.r = r;
    this.f = f;
    this.b = b;
  }

  @Override public boolean equals(Object obj) {
    DiceState dice = (DiceState) obj;
    return d == dice.d && l == dice.l && u == dice.u && r == dice.r && f == dice.f && b == dice.b;
  }

  @Override public int hashCode() {
    return d * 31 + l;
  }

  public DiceState north() {
    return new DiceState(b, l, f, r, d, u);
  }

  public DiceState south() {
    return new DiceState(f, l, b, r, u, d);
  }

  public DiceState west() {
    return new DiceState(l, u, r, d, f, b);
  }

  public DiceState east() {
    return new DiceState(r, d, l, u, f, b);
  }

}