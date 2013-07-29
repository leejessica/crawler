package mo.umac.rtree;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntProcedure;
import gnu.trove.TIntStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import mo.umac.crawler.offline.SliceCrawler;
import mo.umac.metadata.APOI;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.Node;
import com.infomatiq.jsi.rtree.RTree;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class MyRTree extends RTree {

    public static Logger logger = Logger.getLogger(MyRTree.class.getName());

    public static MyRTree rtree = new MyRTree();

    public MyRTree() {
	Properties props = new Properties();
	// FIXME change to 50, 20
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
	boolean contain = myContains(r, v);
	if (!contain) {
	    return contain;
	}
	List<Integer> list = v.getList();
	logger.debug(list.size());
	if (list != null && list.size() == 0) {
	    return true;
	}
	// FIXME judge from these intersected rectangles
	// sorting the list
	for (int i = 0; i < list.size(); i++) {
	    int id = list.get(i);
	    logger.debug("id = " + id);
	    Node node = getNode(id);
	    if (node == null) {
		logger.debug("null node");
	    } else {
		logger.debug(node.mbrMinX + ", " + node.mbrMaxX + ", "
			+ node.mbrMinY + ", " + node.mbrMaxY);
	    }
	}

	return false;
    }

    public boolean myContains(Rectangle r, TIntProcedure v) {
	// stacks used to store nodeId and entry index of each node
	// from the root down to the leaf. Enables fast lookup
	// of nodes when a split is propagated up the tree.
	TIntStack parents = new TIntStack();
	// find a rectangle in the tree that contains the passed
	// rectangle
	// written to be non-recursive (should model other searches on this?)

	int rootNodeId = this.getRootNodeId();

	parents.reset();
	parents.push(rootNodeId);
	boolean contain = false;
	Rectangle rN;
	while (parents.size() > 0) {
	    Node n = getNode(parents.pop());
	    if (logger.isDebugEnabled()) {
		rN = new Rectangle(n.mbrMinX, n.mbrMinY, n.mbrMaxX, n.mbrMaxY);
		System.out.println("");
		System.out.println(rN.toString());
		System.out.println("-------");
	    }
	    if (!n.isLeaf()) {
		// The children of n are not the leaves
		// go through every entry in the index node to check
		// if it intersects the passed rectangle. If so, it
		// could contain entries that are contained.
		contain = false;
		for (int i = 0; i < n.entryCount; i++) {
		    if (logger.isDebugEnabled()) {
			rN = new Rectangle(n.entriesMinX[i], n.entriesMinY[i],
				n.entriesMaxX[i], n.entriesMaxY[i]);
			System.out.println(rN.toString());
		    }

		    if (Rectangle.contains(n.entriesMinX[i], n.entriesMinY[i],
			    n.entriesMaxX[i], n.entriesMaxY[i], r.minX, r.minY,
			    r.maxX, r.maxY)) {
			// the first one contains the second one
			if (logger.isDebugEnabled()) {
			    logger.debug("contained by a non-leaf node");
			}
			parents.push(n.ids[i]);
			contain = true;
			break;
		    }
		}
		if (contain) {
		    continue;
		} else {
		    return false;
		}
	    } else {
		// go through every entry in the leaf to check if
		// it is contained by the passed rectangle
		for (int i = 0; i < n.entryCount; i++) {
		    if (logger.isDebugEnabled()) {
			rN = new Rectangle(n.entriesMinX[i], n.entriesMinY[i],
				n.entriesMaxX[i], n.entriesMaxY[i]);
			System.out.println(rN.toString());
		    }
		    if (Rectangle.contains(n.entriesMinX[i], n.entriesMinY[i],
			    n.entriesMaxX[i], n.entriesMaxY[i], r.minX, r.minY,
			    r.maxX, r.maxY)) {
			// the objective rectangle is contained by a single
			// rectangle
			if (logger.isDebugEnabled()) {
			    logger.debug("contained by a leaf node");
			}
			return true;
		    } else {
			if (Rectangle.intersects(r.minX, r.minY, r.maxX,
				r.maxY, n.entriesMinX[i], n.entriesMinY[i],
				n.entriesMaxX[i], n.entriesMaxY[i])) {
			    if (logger.isDebugEnabled()) {
				logger.debug("intersect");
			    }
			    logger.debug("n.ids[i] = " + n.ids[i]);
			    if (!v.execute(n.ids[i])) {
				// maybe
				logger.debug(!v.execute(n.ids[i]));
			    }
			}
		    }
		}
		return true;
	    }
	}
	return false;
    }

    public static Point coordinateToPoint(Coordinate v) {
	return new Point((float) v.x, (float) v.y);
    }

    /**
     * print the rtree
     */
    public void print() {
	TIntStack parents = new TIntStack();
	int rootNodeId = this.getRootNodeId();

	parents.reset();
	parents.push(rootNodeId);

	while (parents.size() > 0) {
	    Node n = getNode(parents.pop());
	    Rectangle rN = new Rectangle(n.mbrMinX, n.mbrMinY, n.mbrMaxX,
		    n.mbrMaxY);
	    System.out.println("");
	    System.out.println(rN.toString());
	    System.out.println("-------");

	    if (!n.isLeaf()) {// The children of n are not the leaves
		System.out.println("not leaf");
		// go through every entry in the index node to check
		// if it intersects the passed rectangle. If so, it
		// could contain entries that are contained.
		for (int i = 0; i < n.entryCount; i++) {
		    rN = new Rectangle(n.entriesMinX[i], n.entriesMinY[i],
			    n.entriesMaxX[i], n.entriesMaxY[i]);

		    System.out.println(rN.toString());

		    // if (rN.contains(r)) {
		    // System.out.println("this contains");
		    parents.push(n.ids[i]);
		}
	    } else {
		// go through every entry in the leaf to check if
		// it is contained by the passed rectangle
		System.out.println("leaf");
		for (int i = 0; i < n.entryCount; i++) {
		    rN = new Rectangle(n.entriesMinX[i], n.entriesMinY[i],
			    n.entriesMaxX[i], n.entriesMaxY[i]);
		    System.out.println(rN.toString());

		}
	    }
	}
    }

    public static void main(String[] args) {
	// MyRTree rtree = new MyRTree();
	int i = 0;
	Envelope e1 = new Envelope(2, 4, 0, 10);
	rtree.addRectangle(i++, e1);

	e1 = new Envelope(3, 6, 0, 10);
	rtree.addRectangle(i++, e1);

	e1 = new Envelope(7, 9, 3, 5);
	rtree.addRectangle(i++, e1);

	e1 = new Envelope(8, 10, 1, 4);
	rtree.addRectangle(i++, e1);

	e1 = new Envelope(8, 10, 6, 7);
	rtree.addRectangle(i++, e1);

	// 1. whether the first two have been merged?
	// 2. whether the envelope below is covered by the previous rectangle?

	// FIXME print nodeMap
	TIntObjectHashMap<Node> map = rtree.nodeMap;
	// 
	
	e1 = new Envelope(8.1, 8.5, 2, 4.5);

	boolean contain = rtree.contains(e1);
	logger.debug(contain);
	// boolean b = rtree.contains(e1);
	// System.out.println(b);
    }
}
