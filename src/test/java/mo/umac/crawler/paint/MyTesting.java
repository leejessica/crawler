package mo.umac.crawler.paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyTesting extends JComponent {

    private Ellipse2D.Double circle = new Ellipse2D.Double(50, 50, 100, 100);
    private Rectangle2D.Double square = new Rectangle2D.Double(10, 10, 30, 30);
    private Line2D line = new Line2D.Double(2, 2, 50, 70);
//    private Point2D.Double point = new Point2D.Double(30, 40);
    
    private Rectangle2D.Double rectangle = new Rectangle2D.Double(15, 20, 30, 30);

    public void paintComponent(Graphics g) {
	clear(g);
	Graphics2D g2 = (Graphics2D) g;
	Point2D.Double point = new Point2D.Double(30, 40);
	//
	g2.fillRect(2, 2, 1, 1);
	g2.draw(circle);
//	g2.fill(circle);
//	g2.draw(square);
//	g2.draw(line);
//	g2.draw(rectangle);

    }

    protected void clear(Graphics g) {
	super.paintComponent(g);
    }

    public static void main(String[] args) {
	JFrame frame = new JFrame();
	frame.setSize(100, 100);
	frame.pack();
	frame.setTitle("My Map");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	MyTesting component = new MyTesting();
	frame.add(component);
	frame.setVisible(true);
    }

}
