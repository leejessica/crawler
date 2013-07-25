package mo.umac.mo.paint;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import mo.umac.spatial.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class PaintShapes extends JPanel {

    public Ellipse2D.Double paintCircle(Circle circle) {
	Coordinate center = circle.getCenter();
	double radius = circle.getRadius();
	double diameter = 2 * radius;
	return new Ellipse2D.Double(center.x, center.y, diameter, diameter);
    }

    public     Rectangle2D.Double paintRectangle(Envelope envelope){
	double width = envelope.getWidth();
	double length = envelope.getHeight();
	// FIXME paint
	envelope.getMinX()
	return new Rectangle2D.Double(15, 20, 30, 30);
    }

    public void paintPoint(Graphics g, Coordinate p) {
	Graphics2D g2 = (Graphics2D) g;
	g2.fillRect(2, 2, 1, 1);
	g2.fillRect(p.x, p.y, 2, 2);
    }

}
