/**
 * https://open.kattis.com/problems/factors
 * Author: Micah Stairs
 * Solved On: January 27, 2017
 *
 * The general approach used in this solution was suggested by Liam Keliher,
 * and was further discussed with Finn Lidbetter and William Fiset.
 *
 * We are basically solving this problem backwards than what one might expect.
 * Instead of starting with a query and then finding the associated answer, we
 * are actually enumerating over all possible answers using a backtracking
 * techniaue and keeping only the smallest answers. The only reason that this
 * approach works is because the input and output contraints reduce the set
 * of valid queries to a very manageable size.
 *
 * One trick used in this solution to represent large numbers is to maintain
 * an array with the prime factorization of the number.
 **/

import java.util.*;
import java.io.*;

public class Factors {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  // We can't multiply more than 15 primes together and get a number within the range of a long,
  // but the next 3 primes exist in the prime factorization of 63!, so we need to use those too
  static int MAX_PRIMES = 18;

  // Pre-compute needed primes
  static int[] primes = new int[MAX_PRIMES];
  static {
    int index = 0;
    for (int i = 2; index < MAX_PRIMES; i++) {
      if (isPrime(i)) {
        primes[index++] = i;
      }
    }
  }

  // Pre-compute prime factorizations of factorials up to 63
  static int MAX_FACTORIAL = 63;
  static int[][] fact = new int[MAX_FACTORIAL+1][MAX_PRIMES];
  static {
    for (int i = 2; i <= MAX_FACTORIAL; i++) {
      int tmp = i;
      for (int j = 0; j < MAX_PRIMES; j++) {
        fact[i][j] = fact[i-1][j];
        while (tmp % primes[j] == 0) {
          tmp /= primes[j];
          fact[i][j]++;
        }
      }
    }
  }

  // Used to store answers
  static Map<Long, Long> map = new HashMap<>();

  public static void main(String[] args) throws IOException {

    // Pre-compute the answers to all possible valid inputs
    for (int i = 1; i <= MAX_FACTORIAL; i++) {
      partition(0, i, i, 1, (int[]) fact[i].clone());
    }

    // Process each query
    String line;
    while ((line = br.readLine()) != null) {
      long n = Long.parseLong(line);
      sb.append(n + " " + map.get(n) + "\n");
    }

    // Output answer
    System.out.print(sb);
  
  }

  // Use recursive backtracking to find all valid inputs and the minimum associated answer
  static void partition(int index, int spotsLeft, int max, long currentK, int[] factorizationOfN) {

    // Reached illegal state
    if (spotsLeft < 0) return;

    // Try stopping here
    if (spotsLeft == 0) {
      Long product = multiply(factorizationOfN);
      if (product != null) {
        if (!map.containsKey(product)) {
          map.put(product, currentK);
        } else {
          map.put(product, Math.min(map.get(product), currentK));
        }
      }
    }

    // No more primes to consider
    if (index >= MAX_PRIMES) return;

    // Try each parititon size
    for (int i = 1; i <= max; i++) {
      
      // Detect if overflow will happen
      if (currentK > Long.MAX_VALUE / primes[index]) return;

      // Update k
      currentK *= primes[index];

      // Update n (using copy of array)
      int[] copy = (int[]) factorizationOfN.clone();
      for (int j = 0; j < MAX_PRIMES; j++) {
        copy[j] -= fact[i][j];
      }

      // Make recursive call
      partition(index + 1, spotsLeft - i, i, currentK, copy);

    }

  }

  // Multiplies the prime factorization of a number, returning null if the value
  // exceeds the capacity of a long
  static Long multiply(int[] primeFactorization) {
    long product = 1;
    for (int i = 0; i < MAX_PRIMES; i++) {
      for (int j = 0; j < primeFactorization[i]; j++) {
        if (product > Long.MAX_VALUE / primes[i]) return null;
        product *= primes[i];
      }
    }
    return product;
  }

  // Returns whether the specified number is a prime or not
  static boolean isPrime(long n) {
    if (n < 2) return false;
    if (n == 2 || n == 3) return true;
    if (n % 2 == 0 || n % 3 == 0) return false;
    int limit = (int) Math.sqrt(n);
    for (int i = 5; i <= limit; i += 6)
      if (n % i == 0 || n % (i + 2) == 0)
        return false;
    return true;
  }

}