package minitest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import mo.umac.metadata.IntersectPoint;
import mo.umac.metadata.VQP;
import mo.umac.metadata.VQP1;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

public class Line_circle_test {

	public Line_circle_test() {
		super();
		System.out.println("this is a test program!!!!!!\n");
	}

	public static boolean isOnLinesegment(Coordinate p, LineSegment l){
		if(Math.abs(l.distance(p)-0)<1e-6)
			return true;
		else return false;
	}

	public static void main(String[] args) {
		Coordinate p=new Coordinate(1, 1);
		Coordinate a1=new Coordinate(0, 2);
		Coordinate a2=new Coordinate(2, 0);
		Coordinate q=new Coordinate(2, 0.00001);
		LineSegment l=new LineSegment(a1, a2);
        boolean flag1=isOnLinesegment(p,l);
        boolean flag2=isOnLinesegment(q,l);
        System.out.println("flag1="+flag1+", flag2= "+flag2);
	}
}
