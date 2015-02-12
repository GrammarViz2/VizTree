package edu.hawaii.jmotif.viztree;

import java.util.ArrayList;

/**
 * Implements a Tree node abstraction.
 * 
 * @author psenin, jessica lin
 * 
 */
public class Node {

  // the node symbol
  protected char data;

  // children
  protected ArrayList<Node> children;

  // the node's parent
  protected Node parent;

  // some stats on the branch
  protected int expected;
  protected int weight;
  protected int depth;

  // the node positioning relative to the root
  protected double x;
  protected double y;
  protected double x_tmp;
  protected double y_tmp;

  public Node(char value, int depth) {
    this.data = value;
    this.depth = depth;
  }
}
