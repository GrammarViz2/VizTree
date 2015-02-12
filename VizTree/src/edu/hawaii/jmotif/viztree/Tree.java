package edu.hawaii.jmotif.viztree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.imageio.ImageIO;

/**
 * Implements the grammarviz tree.
 * 
 * @author seninp, jessica lin
 * 
 */
public class Tree {

  private static final String SPACE = " ";

  private static final int IMAGE_WIDTH = 900;
  private static final int IMAGE_HEIGHT = 600;

  // the tree root
  private Node root;

  // the tree depth
  private int treeDepth;

  private HashMap<Integer, Integer> levelWidth;

  private int treeWidth;

  public Tree() {
    super();
    this.root = new Node();
  }

  // the string consists of terminals separated by spaces
  public void put(String w) {
    String[] split = w.split(SPACE);
    Node cNode = root;
    for (String s : split) {
      Node tmp = cNode.getChild(s);
      if (null == tmp) {
        tmp = new Node(s);
        cNode.addChild(tmp);
      }
      else {
        tmp.depth++;
      }
      cNode = tmp;
    }
  }

  public void put(String key, Integer value) {
    String[] split = key.split(SPACE);
    Node cNode = root;
    for (String s : split) {
      Node tmp = cNode.getChild(s);
      if (null == tmp) {
        tmp = new Node(s, value);
        cNode.addChild(tmp);
      }
      else {
        tmp.depth = tmp.depth + value;
      }
      cNode = tmp;
    }
  }

  public void printBreadthFirst() {
    Queue<Node> q = new LinkedBlockingQueue<Node>();
    q.add(this.root);
    int cLevel = 0;
    while (q.size() > 0) {
      Node n = q.remove();
      if (n.getLevel() > cLevel) {
        System.out.print("\n" + n.getLevel() + ", " + this.levelWidth.get(n.getLevel()) + ": ");
        cLevel++;
      }
      System.out.print(n.data + ":" + n.depth + "  ");
      for (Node nd : n.children.values()) {
        q.add(nd);
      }
    }
  }

  private void assignWidths() {

    this.levelWidth = new HashMap<Integer, Integer>();
    int cLevel = 0;
    int cWidth = 1;

    levelWidth.put(cLevel, cWidth);

    cLevel = 1;
    Queue<Node> q = new LinkedBlockingQueue<Node>();
    for (Node nd : this.root.children.values()) {
      q.add(nd);
    }

    cWidth = 0;
    while (q.size() > 0) {

      Node n = q.remove();

      // if we are get to the next level, save the width and init new counter
      if (n.getLevel() > cLevel) {
        this.levelWidth.put(cLevel, cWidth);
        cLevel++;
        cWidth = 0;
      }
      cWidth++;

      for (Node nd : n.children.values()) {
        q.add(nd);
      }
    }

    this.treeWidth = -1;
    for (Entry<Integer, Integer> e : this.levelWidth.entrySet()) {
      if (e.getValue() > this.treeWidth) {
        this.treeWidth = e.getValue();
      }
    }
    this.levelWidth.put(cLevel, cWidth);

  }

  public void assignLevels() {

    // init the tree dimension values
    int currentLevel = 0;

    // enQueue the root
    Queue<Node> q = new LinkedBlockingQueue<Node>();
    this.root.setLevel(currentLevel);
    q.add(this.root);

    while (q.size() > 0) {

      // get the node from queue
      Node n = q.remove();
      currentLevel = n.getLevel();

      // all children of this node shall have a level node+1
      for (Node nd : n.children.values()) {
        nd.setLevel(currentLevel + 1);
        q.add(nd);
      }

    }

    // set the tree depth property
    this.treeDepth = currentLevel;
    this.assignWidths();
  }

  public void draw() {
    // String fileName = new SimpleDateFormat("yyyyMMddhhmmssSS'.png'").format(new Date());
    try {

      BufferedImage bi = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

      double gridStepX = Integer.valueOf(IMAGE_WIDTH).doubleValue() / (this.treeDepth + 2.0d);

      Graphics2D g = bi.createGraphics();
      g.setPaint(Color.WHITE);
      g.fillRect(0, 0, bi.getWidth(), bi.getHeight());

      // drawing part
      //

      // draw a root
      //
      double cX = gridStepX;
      double cY = IMAGE_HEIGHT / 2.0;
      this.root.setCoordinates(cX, cY);

      g.setColor(Color.RED);
      Ellipse2D.Double circle = new Ellipse2D.Double(gridStepX - 3, IMAGE_HEIGHT / 2.0 - 3.0, 6, 6);
      g.fill(circle);

      // place first level in the queue
      Queue<Node> q = new LinkedBlockingQueue<Node>();
      for (Node nd : this.root.children.values()) {
        q.add(nd);
      }
      int cLevel = 1;
      int wCounter = 0;
      double gridStepY = Integer.valueOf(IMAGE_HEIGHT).doubleValue()
          / (this.levelWidth.get(1) + 1.0d);

      while (q.size() > 0) {

        Node n = q.remove();
        if (n.getLevel() > cLevel) {
          cLevel++;
          gridStepY = Integer.valueOf(IMAGE_HEIGHT).doubleValue()
              / (this.levelWidth.get(cLevel) + 1.0d);
          wCounter = 0;
        }

        // drawing part
        n.setCoordinates(gridStepX + n.getLevel() * gridStepX, gridStepY + wCounter * gridStepY);
        g.setColor(Color.RED);

        System.out.println((int) n.parent.x + ", " + (int) n.parent.y + ", " + (int) n.x + ", "
            + (int) n.y);
        g.setStroke(new BasicStroke(n.depth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine((int) n.parent.x, (int) n.parent.y, (int) n.x, (int) n.y);

        circle = new Ellipse2D.Double((int) n.x - 2.5, (int) n.y - 2.5, 5, 5);
        g.fill(circle);

        g.setColor(Color.CYAN);
        g.setFont(g.getFont().deriveFont(20f));
        g.drawString(n.data, (int) n.x + 2, (int) n.y + 23);

        wCounter++;

        for (Node nd : n.children.values()) {
          q.add(nd);
        }

      }

      File f = new File("MyFile.png");
      ImageIO.write(bi, "PNG", f);

    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }
}
