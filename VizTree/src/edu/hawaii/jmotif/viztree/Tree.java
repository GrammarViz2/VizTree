package edu.hawaii.jmotif.viztree;

import java.util.ArrayList;

public class Tree {

  private byte branch;
  private int depth;
  private int[] max_weight;

  // private int max_weight;

  private Node root;

  public Node getRoot() {
    return root;
  }

  public byte getBranch() {
    return branch;
  }

  public void setBranch(byte value) {
    this.branch = value;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int value) {
    this.depth = value;
  }

  public int[] getMaxWeight() {
    return max_weight;
  }

  public Tree(byte b, int d) {
    branch = b;
    depth = d;
    max_weight = new int[depth];
    for (int i = 0; i < depth; i++) {
      max_weight[i] = 0;
    }
  }

  public void Insert(byte[] data, int offset) {
    // Insert the root
    if (root == null)
      root = new Node(branch, (byte) 0, (byte) 0);

    byte cur = 0;

    Node prev = root;
    // prev = root;

    for (byte i = 0; i < depth; i++) {
      cur = data[i];

      // Insert the node if it doesn't exist
      if (prev.children[cur - 1] == null) {
        Node temp = new Node(branch, cur, (byte) (i + 1));
        prev.children[cur - 1] = temp;
        prev.children[cur - 1].offset.add(offset);
        prev.children[cur - 1].weight = 1;
        prev.children[cur - 1].expected = 1;
        prev.children[cur - 1].parent = prev;

        // Console.Write("{0}({1}), ", Convert.ToString(cur),
        // Convert.ToString(prev.children[cur-1].weight));

        prev = prev.children[cur - 1];

        if (max_weight[i] < 1)
          max_weight[i] = 1;
      }

      // if the node exists, then just update the weight and the offset
      else {
        prev.children[cur - 1].weight++;
        prev.children[cur - 1].expected++;

        if (max_weight[i] < prev.children[cur - 1].weight)
          max_weight[i] = prev.children[cur - 1].weight;

        prev.children[cur - 1].offset.add(offset);

        // Console.Write("{0}({1}), ", Convert.ToString(cur),
        // Convert.ToString(prev.children[cur-1].weight));

        prev = prev.children[cur - 1];

      }
    }

    // Console.WriteLine("");
  }

  public void InsertDiff(byte[] data, int offset) {
    // Insert the root
    if (root == null)
      root = new Node(branch, (byte) 0, (byte) 0);

    byte cur = 0;

    Node prev = root;
    // prev = root;

    for (byte i = 0; i < depth; i++) {
      cur = data[i];

      // prev.children[cur-1].weight = prev.children[cur-1].expected - 1;
      prev.children[cur - 1].weight--;
      // prev.children[cur-1].weight = prev.children[cur-1].expected;
      // if (prev.children[cur-1].weight == 0)
      // {
      // prev.children[cur-1] = null;
      // break;
      // }

      // else
      // {
      prev.children[cur - 1].offset.add(offset);

      // Console.Write("{0}({1}), ", Convert.ToString(cur),
      // Convert.ToString(prev.children[cur-1].weight));

      prev = prev.children[cur - 1];
      // }
      // }

    }

    // Console.WriteLine("");
  }

  // start_pos is zero-based
  public int CountSubstring(int start_pos, int end_pos, byte[] substring) {
    Node curNode;
    ArrayList<Node> alQueue = new ArrayList<Node>();

    boolean STOP = false;
    int total = 0;

    // special case: starting from the root. We don't need to search the tree, just follow the right
    // path.
    if (start_pos == 0) {
      return GetWeight(end_pos, substring);
    }

    alQueue.add(root);

    for (;;) {
      curNode = alQueue.get(0);

      if (curNode.depth >= start_pos)
        break;

      for (int i = 0; i < branch; i++) {
        if (curNode.children[i] != null) {
          alQueue.add(curNode.children[i]);
        }
      }

      alQueue.remove(0);
    }

    boolean EXIST = true;

    while (alQueue.size() > 0) {
      EXIST = true;
      curNode = alQueue.get(0);

      for (int i = start_pos; i <= end_pos; i++) {
        if (curNode.children[substring[i] - 1] != null) {
          curNode = curNode.children[substring[i] - 1];

          // we don't consider the calculated expected values
          if (curNode.expected < 0) {
            EXIST = false;
            break;
          }
        }
        else {
          EXIST = false;
          break;
        }
      }

      if (EXIST)
        total += curNode.weight;

      alQueue.remove(0);

    }

    return total;
  }

  public void Predict(int size_ref, int size_test) {
    Node curNode;
    ArrayList<Node> alQueue = new ArrayList<Node>();

    double scaling_factor = size_test / size_ref;

    alQueue.add(0, root);

    int node_count = 1;

    int freq1 = 0;
    int freq2 = 0;
    int temp_freq = 0;

    double prob = 0.0;

    byte[] path = new byte[depth];

    boolean STOP = false;

    int total = GetTotalCount();

    int order = 0;

    int temp_pos = 0;
    int cur_data;

    Node temp;

    while (alQueue.size() > 0) {
      curNode = alQueue.get(0);

      for (int i = 0; i < branch; i++) {
        STOP = false;

        if (curNode.children[i] == null) {
          Node temp2 = new Node(branch, (byte) (i + 1), (byte) (curNode.depth + 1));
          curNode.children[i] = temp2;
          curNode.children[i].weight = 0;
          curNode.children[i].parent = curNode;
          temp = curNode.children[i];

          temp_pos = curNode.children[i].depth - 1;

          // special case: depth = 1
          if (curNode.children[i].depth == 1) {
            curNode.children[i].expected = 1;
          }

          // special case: depth = 2
          else if (curNode.children[i].depth == 2) {
            // get the data path
            temp = curNode.children[i];

            while (temp != root) {
              path[temp_pos] = temp.data;
              temp = temp.parent;
              temp_pos--;
            }

            freq1 = CountSubstring(1, 1, path);

            curNode.children[i].expected = (int) (curNode.expected * freq1 / total * scaling_factor);
          }

          // any other cases
          else {
            order = curNode.children[i].depth - 2;

            // get the data path
            temp = curNode.children[i];

            while (temp != root) {
              path[temp_pos] = temp.data;
              temp = temp.parent;
              temp_pos--;
            }

            while (!STOP) {
              freq1 = 1;
              freq2 = 1;

              // try the current order
              for (int j = 0; j < curNode.children[i].depth - order; j++) {
                // the CountSubstring function will return 0 if such pattern doesn't exist
                freq1 *= CountSubstring(j, j + order, path);

                // if any of the pattern doesn't exist, then go down one order
                if (freq1 == 0) {
                  order--;

                  // if no HMM of any order can be used, then just use the frequencies of the
                  // individual symbols
                  if (order < 1) {
                    freq1 = 1;

                    for (int w = 0; w < curNode.children[i].depth; w++) {
                      temp_freq = CountSubstring(w, w, path);

                      // if a pattern does not exist, we do not use it (otherwise it'll cause the
                      // whole thing to become
                      // 0.
                      if (temp_freq > 0)
                        freq1 *= temp_freq;
                    }

                    curNode.children[i].expected = (int) (freq1
                        / Math.pow(total, curNode.children[i].depth - 1) * scaling_factor);

                    freq2 = 0;

                    STOP = true;
                  }

                  break;
                }

                else {
                  // compute the denominator at the same time
                  if (j > 0)
                    freq2 *= CountSubstring(j, j + order - 1, path);
                }

              } // end for

              // if we're here, that means we have completed the for-loop and found the right order
              // for HMM. We can stop now.
              STOP = true;
            }

            if (freq2 != 0)
              curNode.children[i].expected = (int) (freq1 / freq2 * scaling_factor);

          }

          // Make sure the value is negative (to denote that it's a computed expected value
          if (curNode.children[i].expected > 0)
            curNode.children[i].expected = 0 - curNode.children[i].expected;
          // else if (curNode.children[i].expected == 0)
          // curNode.children[i].expected = -1;
        }

        if (curNode.children[i].depth < depth) {
          alQueue.add((Node) curNode.children[i]);
          node_count++;
        }
      }

      alQueue.remove(0);
    }
  }

  public int GetMaxWeight() {
    Node curNode = root;

    int max = -9999;

    for (byte i = 0; i < (byte) branch; i++) {
      if (curNode.children[i] != null && Math.abs(curNode.children[i].weight) > max)
        max = curNode.children[i].weight;
    }

    // max_weight = max;
    return max;
  }

  public int GetTotalCount() {
    Node curNode = root;
    int total = 0;

    for (byte i = 0; i < (byte) branch; i++) {
      if (curNode.children[i] != null)
        total += curNode.children[i].expected;
    }

    return total;
  }

  public int GetMaxOffsetCount() {
    Node curNode = root;

    int max = 0;

    for (byte i = 0; i < (byte) branch; i++) {
      if (curNode.children[i] != null && curNode.children[i].offset.size() > max)
        max = curNode.children[i].offset.size();
    }

    // max_weight = max;
    return max;
  }

  public Node GetNode(byte[] data) {
    Node curNode = root;
    int max_dep = data.length;

    byte cur_val = 0;

    if (max_dep > depth)
      max_dep = depth;

    for (byte i = 0; i < (byte) max_dep; i++) {
      if (curNode != null) {
        cur_val = data[i];

        if (cur_val > 0 && curNode.children[cur_val - 1] != null)
          curNode = curNode.children[cur_val - 1];
        else
          return new Node((byte) 0, (byte) 0, (byte) 0);
      }

      else {
        return new Node((byte) 0, (byte) 0, (byte) 0);
      }
    }

    return curNode;
  }

  // Assuming starting from root
  public int GetWeight(int end_pos, byte[] data) {

    Node curNode = root;
    int total = 0;

    for (int i = 0; i <= end_pos; i++) {
      if (curNode.children[data[i] - 1] != null)
        curNode = curNode.children[data[i] - 1];
      else
        return 0;
    }

    return curNode.weight;

  }

  public int GetWeight(byte[] data) {
    return GetWeight(data.length - 1, data);

    /*
     * int w = 0; byte cur_val = 0; Node curNode = root;
     * 
     * for (byte i = 0; i < data.GetLength(0); i++) { if (curNode != null) { cur_val = data[i];
     * 
     * if (curNode.children[cur_val-1] != null) curNode = curNode.children[cur_val-1];
     * 
     * if (i == data.GetLength(0) - 1) w = curNode.weight; }
     * 
     * else { break; } }
     * 
     * return w;
     */
  }

  public int GetNumLeaves() {
    return (int) (Math.pow(branch, depth));
  }

  public void PrintDepthFirst() {
    Node curNode = root;

    ArrayList<Node> alStack = new ArrayList<Node>();

    int curPos = 0;

    int node_count = 1;

    boolean DONE = false;

    do {
      for (int i = branch - 1; i >= 0; i--) {
        if (curNode.children[i] != null) {
          alStack.add(curPos, curNode.children[i]);
          node_count++;
          curPos++;
        }
      }

      if (alStack.size() == 0)
        break;

      curNode = alStack.get(curPos - 1);

      // pop the head of stack
      alStack.remove(curPos - 1);

      curPos--;

      // print the node
      // Console.WriteLine(Convert.ToString(curNode.data));
      // Console.WriteLine("{0}, {1}", Convert.ToString(curNode.data),
      // Convert.ToString(curNode.depth));
    }
    while (!DONE);

    // Console.WriteLine("Number of nodes in tree: {0}", Convert.ToString(node_count));

    // Console.WriteLine("Number of leave nodes: {0}", Convert.ToString(GetNumLeaves()));
    // Console.Read();

  }

  public void PrintBreadthFirst() {
    Node curNode;
    ArrayList<Node> alQueue = new ArrayList<Node>();

    alQueue.add(0, root);

    int node_count = 1;

    while (alQueue.size() > 0) {
      curNode = alQueue.get(0);

      for (int i = 0; i < branch; i++) {
        if (curNode.children[i] != null) {
          alQueue.add(alQueue.size(), (Node) curNode.children[i]);
          node_count++;
          // Console.WriteLine("{0}, {1}", Convert.ToString(curNode.children[i].data),
          // Convert.ToString(curNode.children[i].depth));
        }
      }
      // Console.ReadLine();

      alQueue.remove(0);

      // node_count--;

    }

    // Console.WriteLine(Convert.ToString(node_count));
    // Print(root);

    // Console.WriteLine("Number of nodes in tree: {0}", Convert.ToString(node_count));
    // Console.WriteLine("Number of leave nodes: {0}", Convert.ToString(GetNumLeaves()));
    // Console.Read();i
  }

}
