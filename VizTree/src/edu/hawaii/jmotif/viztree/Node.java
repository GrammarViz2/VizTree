package edu.hawaii.jmotif.viztree;

import java.util.TreeMap;

/**
 * Implements a Tree node abstraction.
 * 
 * @author psenin, jessica lin
 * 
 */
public class Node {

  // the node symbol
  protected String data;

  // children
  protected TreeMap<String, Node> children;

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

  private int level;

  /**
   * Constructor.
   * 
   * @param value
   * @param depth
   */
  public Node(String value, int depth) {
    this.data = value;
    this.depth = depth;
    this.children = new TreeMap<String, Node>();
  }

  /**
   * Constructor
   */
  public Node() {
    this.data = null;
    this.depth = 1;
    this.children = new TreeMap<String, Node>();
  }

  public Node(String s) {
    this.data = s;
    this.depth = 1;
    this.children = new TreeMap<String, Node>();
  }

  public Node getChild(String s) {
    return this.children.get(s);
  }

  public void addChild(Node tmp) {
    this.children.put(tmp.getValue(), tmp);
    tmp.parent = this;
  }

  private String getValue() {
    return this.data;
  }

  public void setLevel(int value) {
    this.level = value;
  }

  public int getLevel() {
    return this.level;
  }

  public void setCoordinates(double valueX, double valueY) {
    this.x = valueX;
    this.y = valueY;
  }
}
