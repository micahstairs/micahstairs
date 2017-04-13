/**
 * https://open.kattis.com/problems/thermostat
 * Author: Micah Stairs
 * Solved On: April 1, 2017
 * 
 * I solved this problem by working out the formulas by hand, mapping a unit system
 * to another using algebra. With these formulas in hand, I just needed to create
 * a class which could represent a fraction and perform the needed operations
 * (including reducing the fraction). When I first submitted this problem I got
 * the wrong answer since I was making a mistake when handling the sign of the fraction.
 **/

import java.util.*;
import java.io.*;

public class ThermostatTrouble {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Read first line of input
    String[] line = br.readLine().split(" ");
    int n = Integer.parseInt(line[0]);
    int q = Integer.parseInt(line[1]);

    // Store unit systems
    long[] l = new long[n];
    long[] h = new long[n];
    for (int i = 0; i < n; i++) {
      line = br.readLine().split(" ");
      l[i] = Long.parseLong(line[0]);
      h[i] = Long.parseLong(line[1]);
    }

    // Process queries
    for (int i = 0; i < q; i++) {
      line = br.readLine().split(" ");
      int a = Integer.parseInt(line[0]) - 1;
      int b = Integer.parseInt(line[1]) - 1;
      long x = Long.parseLong(line[2]);
      Fraction fraction1 = new Fraction(x - l[a], h[a] - l[a]);
      Fraction fraction2 = new Fraction(h[b] - l[b], 1);
      Fraction fraction3 = new Fraction(l[b], 1);
      Fraction answer = Fraction.add(Fraction.multiply(fraction1, fraction2), fraction3);
      sb.append(answer + "\n");
    }

    // Output answers
    System.out.print(sb);
  
  }

}

// Class used to represent a fraction and perform some common operations
class Fraction {

  long n, d;

  public Fraction(long n, long d) {
    this.n = n;
    this.d = d;
  }

  static Fraction multiply(Fraction a, Fraction b) {
    return reduce(new Fraction(a.n * b.n, a.d * b.d));
  }

  static Fraction add(Fraction a, Fraction b) {
    return reduce(new Fraction(a.n * b.d + b.n * a.d, a.d * b.d));
  }

  static Fraction reduce(Fraction f) {
    long div = gcd(f.n, f.d);
    return new Fraction(f.n / div, f.d / div);
  }

  static long gcd(long a, long b) {
    return b == 0 ? a : gcd(b, a % b);
  }

  @Override public String toString() {
    if (d < 0) {
      n = -n;
      d = -d;
    }
    return n + "/" + d;
  }

}