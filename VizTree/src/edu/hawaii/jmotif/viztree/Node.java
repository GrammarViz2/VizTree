package edu.hawaii.jmotif.viztree;

import java.util.ArrayList;

public class Node {
  public byte data;
  public Node[] children;
  public Node parent;

  // public static byte branch;
  public int expected;
  public int weight;
  public int depth;
  public double x;
  public double y;
  public double x_tmp;
  public double y_tmp;
  public ArrayList<Integer> offset;

  public Node(byte b, byte d, byte depth) {
    // branch = b;
    data = d;
    expected = 0;
    weight = 0;
    this.depth = depth;
    children = new Node[b];
    x = 0.0;
    y = 0.0;
    x_tmp = 0.0;
    y_tmp = 0.0;

    offset = new ArrayList<Integer>();

    for (byte i = 0; i < b; i++)
      children[i] = null;
  }
}
