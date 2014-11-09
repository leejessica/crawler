package minitest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import mo.umac.crawler.offline.SortedBydistance;
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

	public static void main(String[] args) {
		Coordinate s=new Coordinate(0, 0);
		Coordinate s1=new Coordinate(1, 1);
		VQP p1=new VQP(s1, 0.5);
		Coordinate s2=new Coordinate(2,2);
		VQP p2=new VQP(s2, 1);
		VQP p3=new VQP(new Coordinate(2,1), 1);
		TreeSet<VQP> set=new TreeSet<VQP>(new SortedBydistance(s));
		set.add(p2);
		set.add(p3);
		
		System.out.println("size="+set.size());
		Iterator<VQP> it=set.iterator();
		while(it.hasNext()){
			VQP p=it.next();
			System.out.println("----"+p.getCoordinate()+",   "+p.getRadius());
		}
		System.out.println("==================");
		VQP p4=set.pollFirst();
		set.add(p1);
		Iterator<VQP> it1=set.iterator();
		while(it1.hasNext()){
			VQP p5=it1.next();
			System.out.println("----"+p5.getCoordinate()+",   "+p5.getRadius());
		}
		
	}   
	
	
}
