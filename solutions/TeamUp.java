/**
 * https://open.kattis.com/problems/teamup
 * Author: Micah Stairs
 * Solved On: February 25, 2017
 * 
 * I spent considerable time working with Finn Lidbetter to come up with a solution to this problem.
 *
 * The restrictions placed on the sets of skills that each character class has is crucial (they
 * are either subsets of one another or completely disjoint). We can think of these classes being
 * arranged in a tree, where a node is an ancestor of another if its set of skills is a superset
 * of the other. We also have the property that the skill sets of siblings are completely disjoint.
 * We can also make the observation that it is optimal to try placing a given node on a team before
 * trying to place any of its descendants.
 * 
 * We can apply a greedy algorithm when placing people on teams due the the aforementioned properties.
 **/

import java.util.*;
import java.io.*;

public class TeamUp {

  static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  static StringBuilder sb = new StringBuilder();

  public static void main(String[] args) throws IOException {

    // Store first line of the input
    String[] line = br.readLine().split(" ");
    int n = Integer.parseInt(line[0]);
    int m = Integer.parseInt(line[1]);
    int p = Integer.parseInt(line[2]);

    // Read in the characters classes
    CharacterClass[] characterClasses = new CharacterClass[m];
    for (int i = 0; i < m; i++) {
      line = br.readLine().split(" ");
      int nSkills = Integer.parseInt(line[0]);
      int[] skills = new int[nSkills];
      for (int j = 0; j < nSkills; j++) {
        skills[j] = Integer.parseInt(line[j+1]);
      }
      characterClasses[i] = new CharacterClass(skills);
    }

    // Add counts
    line = br.readLine().split(" ");
    for (int i = 0; i < p; i++) {
      int characterClass = Integer.parseInt(line[i]) - 1;
      characterClasses[characterClass].peopleIDs.push(i+1);
    }

    // Build tree in order to traverse the nodes efficiently
    Arrays.sort(characterClasses);
    Node root = new Node(null);
    for (int i = 0; i < m; i++) {
      CharacterClass characterClass = characterClasses[i];
      if (characterClass.peopleIDs.size() > 0) {
        Node node = new Node(characterClass);
        root.insert(node);
        for (int skill : characterClass.skills) {
          node.skillsCovered.add(skill);
        }
      }
    }

    // Start with all nodes of the root
    List<Node> activeNodes = new ArrayList<>();
    for (Node node : root.children) {
      activeNodes.add(node);
    }

    // Try building teams greedily
    int nTeams = 0;
    List<Node> newNodes = new ArrayList<>();
    while (!activeNodes.isEmpty()) {

      // Get members for next team
      int nSkillsLeft = n;
      List<Integer> indices = new ArrayList<>();
      for (Node node : activeNodes) {

        indices.add(node.character.peopleIDs.pop());
        nSkillsLeft -= node.skillsCovered.size();

        // Still more left
        if (node.character.peopleIDs.size() > 0) {newNodes.add(node);

        // Add all children
        else for (Node child : node.children) newNodes.add(child);
      }

      // Not all of the skills were able to be covered
      if (nSkillsLeft != 0) break;

      // Store this line of the solution
      sb.append(indices.size());
      for (int index : indices) sb.append(" " + index);
      sb.append("\n");
      nTeams++;

      // Swap lists
      activeNodes.clear();
      activeNodes.addAll(newNodes);
      newNodes.clear();

    }

    // Output solution
    System.out.println(nTeams);
    System.out.print(sb);
  
  }

}

// Used to represent a node in our tree
class Node {

  List<Node> children = new ArrayList<>();
  Set<Integer> skillsCovered = new HashSet<>();
  Map<Integer, Node> skillToNode = new HashMap<>();
  CharacterClass character;

  public Node(CharacterClass character) {
    this.character = character;
  }

  // Insert node into the grpah
  public void insert(Node node) {

    // Check to see if it is a subset of an existing child
    int randomSkillOfChild = node.character.skills[0];
    if (skillToNode.containsKey(randomSkillOfChild)) {
      skillToNode.get(randomSkillOfChild).insert(node);
      return;
    }

    // Otherwise add it as a child
    children.add(node);
    for (int skill : node.character.skills) {
      skillToNode.put(skill, node);
      skillsCovered.add(skill);
    }

  }

}

// Represents a character class
class CharacterClass implements Comparable<CharacterClass> {

  Stack<Integer> peopleIDs = new Stack<>();
  int[] skills;

  public CharacterClass(int[] skills) {
    this.skills = skills;
  }

  @Override public int compareTo(CharacterClass other) {
    return other.skills.length - skills.length;
  }

}