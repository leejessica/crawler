package mo.umac.rtree;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;
import com.vividsolutions.jts.geom.Coordinate;

public class MyRTree extends RTree {

    public MyRTree() {
	Properties props = new Properties();
	props.setProperty("MaxNodeEntries", "50");
	props.setProperty("MinNodeEntries", "20");
	this.init(props);
    }

    public MyRTree(List<Coordinate> points) {
	this();

	Rectangle tmpRect = null;
	float[] values = new float[2];

	for (int i = 0; i < points.size(); i++) {
	    values[0] = (float) points.get(i).x;
	    values[1] = (float) points.get(i).y;
	    tmpRect = new Rectangle(values[0], values[1], values[0], values[1]);
	    this.add(tmpRect, i);
	}
    }

    public List<Integer> rangeSearch(Coordinate point, double range) {
	Point query = coordinateToPoint(point);
	AddToListProcedure v = new AddToListProcedure();

	this.nearestN(query, v, Integer.MAX_VALUE, (float) range);

	return v.getList();
    }

    public List<Integer> searchNN(Coordinate point, int N, double maxDistance) {
	Point query = coordinateToPoint(point);
	AddToListProcedure v = new AddToListProcedure();

	this.nearestN(query, v, N, (float) maxDistance);

	return v.getList();
    }

    public List<Integer> searchNN(Coordinate point, int N) {

	return this.searchNN(point, N, Float.MAX_VALUE);
    }

    public void addPoint(int pointId, Coordinate point) {
	Rectangle tmpRect = null;
	float[] values = new float[2];
	values[0] = (float) point.x;
	values[1] = (float) point.y;
	tmpRect = new Rectangle(values[0], values[1], values[0], values[1]);
	this.add(tmpRect, pointId);
    }

    public static Point coordinateToPoint(Coordinate v) {
	return new Point((float) v.x, (float) v.y);
    }
}
