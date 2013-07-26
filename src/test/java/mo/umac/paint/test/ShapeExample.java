package mo.umac.paint.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;
// For JPanel, etc.
// For Graphics, etc.
// For Ellipse2D, etc.

/**
 * An example of drawing/filling shapes with Java2D in Java 1.2.
 * 
 * From tutorial on learning Java2D at
 * http://www.apl.jhu.edu/~hall/java/Java2D-Tutorial.html
 * 
 * 1998 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 */

public class ShapeExample extends JPanel {

    private ArrayList<Shape> myArr = new ArrayList<Shape>();

    private ArrayList<Shape> myArrPoints = new ArrayList<Shape>();

    public void paintComponent(Graphics g) {
//	clear(g);

	for (Shape i : myArr) {
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.draw(i);
	}
	for (Shape i : myArrPoints) {
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.fill(i);
	}

    }

    public void list1() {
	Ellipse2D.Double circle = new Ellipse2D.Double(10, 10, 350, 350);
	Rectangle2D.Double rectangle = new Rectangle2D.Double(10, 10, 50, 350);
	myArr.add(circle);
	myArr.add(rectangle);
    }
    
    public void list2() {
	myArr.clear();
	myArrPoints.clear();
 	Ellipse2D.Double circle = new Ellipse2D.Double(20, 20, 60, 60);
 	myArr.add(circle);
 	Rectangle2D.Double rectangle = new Rectangle2D.Double(70, 30, 60, 5);
 	myArrPoints.add(rectangle);
     }

    // super.paintComponent clears offscreen pixmap,
    // since we're using double buffering by default.

    protected void clear(Graphics g) {
	super.paintComponent(g);
    }

    public static void main(String[] args) {
	ShapeExample example = new ShapeExample();
	example.list1();
	WindowUtilities.openInJFrame(example, 1000, 1000);
	try {
	    Thread.sleep(3000);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	example.list2();
	example.repaint();
    }
}