package mo.umac.rtree;

import gnu.trove.TIntProcedure;
import gnu.trove.TIntStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Properties;

import mo.umac.metadata.APOI;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.Node;
import com.infomatiq.jsi.rtree.RTree;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class MyRTree extends RTree {

    public MyRTree() {
	Properties props = new Properties();
	props.setProperty("MaxNodeEntries", "4");
	props.setProperty("MinNodeEntries", "2");
	this.init(props);
    }

    /**
     * @param points
     * 
     * @deprecated
     */
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

    public MyRTree(HashMap<Integer, APOI> pois) {
	this();

	Rectangle tmpRect = null;
	float[] values = new float[2];

	for (Iterator iterator = pois.entrySet().iterator(); iterator.hasNext();) {
	    Entry entry = (Entry) iterator.next();
	    int id = (Integer) entry.getKey();
	    APOI poi = (APOI) entry.getValue();

	    values[0] = (float) poi.getCoordinate().x;
	    values[1] = (float) poi.getCoordinate().y;
	    tmpRect = new Rectangle(values[0], values[1], values[0], values[1]);
	    this.add(tmpRect, id);
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

    /**
     * @param rectangleId
     * @param envelope
     */
    public void addRectangle(int rectangleId, Envelope envelope) {
	Rectangle tmpRect = null;
	tmpRect = new Rectangle((float) envelope.getMinX(),
		(float) envelope.getMinY(), (float) envelope.getMaxX(),
		(float) envelope.getMaxY());
	this.add(tmpRect, rectangleId);
    }

    /**
     * whether this envelope has been contained by any other envelope indexed
     * before
     * 
     * @param envelope
     * @return
     */
    public boolean contains(Envelope envelope) {
	Rectangle r = new Rectangle((float) envelope.getMinX(),
		(float) envelope.getMinY(), (float) envelope.getMaxX(),
		(float) envelope.getMaxY());
	//
	AddToListProcedure v = new AddToListProcedure();
	myContains(r, v);
	List<Integer> list = v.getList();
	System.out.println(list.size());
	if (list != null && list.size() > 0) {
	    return true;
	}

	return false;
    }

    public void myContains(Rectangle r, TIntProcedure v) {
	// stacks used to store nodeId and entry index of each node
	// from the root down to the leaf. Enables fast lookup
	// of nodes when a split is propagated up the tree.
	TIntStack parents = new TIntStack();
	TIntStack parentsEntry = new TIntStack();

	// find a rectangle in the tree that contains the passed
	// rectangle
	// written to be non-recursive (should model other searches on this?)

	int rootNodeId = this.getRootNodeId();

	parents.reset();
	parents.push(rootNodeId);

	parentsEntry.reset();
	parentsEntry.push(-1);

	// TODO: possible shortcut here - could test for intersection with the
	// MBR of the root node. If no intersection, return immediately.

	while (parents.size() > 0) {
	    Node n = getNode(parents.peek());
	    int startIndex = parentsEntry.peek() + 1;

	    if (!n.isLeaf()) {
		// go through every entry in the index node to check
		// if it intersects the passed rectangle. If so, it
		// could contain entries that are contained.
		boolean intersects = false;
		for (int i = startIndex; i < n.entryCount; i++) {

		    Rectangle rN = new Rectangle(n.entriesMinX[i],
			    n.entriesMinY[i], n.entriesMaxX[i],
			    n.entriesMaxY[i]);

		    // System.out.println(rN.toString());

		    // if (rN.contains(r)) {
		    if (Rectangle.intersects(r.minX, r.minY, r.maxX, r.maxY,
			    n.entriesMinX[i], n.entriesMinY[i],
			    n.entriesMaxX[i], n.entriesMaxY[i])) {
			// System.out.println("this contains");
			parents.push(n.ids[i]);
			parentsEntry.pop();
			parentsEntry.push(i); // this becomes the start index
					      // when the child has been
					      // searched
			parentsEntry.push(-1);
			intersects = true;
			break; // ie go to next iteration of while()
		    }
		}
		if (intersects) {
		    continue;
		}
	    } else {
		// go through every entry in the leaf to check if
		// it is contained by the passed rectangle
		for (int i = 0; i < n.entryCount; i++) {
		    Rectangle rN = new Rectangle(n.entriesMinX[i],
			    n.entriesMinY[i], n.entriesMaxX[i],
			    n.entriesMaxY[i]);
		    // System.out.println(rN.toString());

		    // if (rN.contains(r)) {
		    if (Rectangle.intersects(r.minX, r.minY, r.maxX, r.maxY,
			    n.entriesMinX[i], n.entriesMinY[i],
			    n.entriesMaxX[i], n.entriesMaxY[i])) {
			// System.out.println("this contains");
			if (!v.execute(n.ids[i])) {
			    return;
			}
		    }
		}
	    }
	    parents.pop();
	    parentsEntry.pop();
	}
    }

    public static Point coordinateToPoint(Coordinate v) {
	return new Point((float) v.x, (float) v.y);
    }

    public static void main(String[] args) {
	MyRTree rtree = new MyRTree();
	Envelope e1 = new Envelope(1, 10, 1, 10);
	rtree.addRectangle(0, e1);
	Random random = new Random(System.currentTimeMillis());
	for (int i = 1; i < 7; i++) {
	    e1 = new Envelope(random.nextInt(100), random.nextInt(100),
		    random.nextInt(100), random.nextInt(100));
	    System.out.println(i + ": ");
	    System.out.println(e1.toString());
	    rtree.addRectangle(0, e1);
	}

	Envelope e2 = new Envelope(2, 3, 2, 3);
	boolean b = rtree.contains(e2);
	System.out.println(b);
    }
}
