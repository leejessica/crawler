package mo.umac.crawler.offline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import mo.umac.metadata.APOI;
import mo.umac.metadata.AQuery;
import mo.umac.metadata.ResultSet;
import mo.umac.metadata.VQP1;
import mo.umac.paint.PaintShapes;
import mo.umac.spatial.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class BinaryCrawler extends OfflineStrategy {

	public static int countquery = 0;
	public static int NEED_POINTS_NUM = 30;
	public static int countpoint = 0;
	public static Set<APOI> queryset = new HashSet<APOI>();// record all points
															// queried
	public static Set<APOI> eligibleset = new HashSet<APOI>();// record all eligible points
	public static Coordinate startPoint1 = new Coordinate();// record the start point of every level query
	public static double inRadius = 0;// using it to keep track of the radius of the mixmum inscribe circle
	public static double CP = 4; // using to adjust the binary search
	public static int onequerycount = 0;// record the number of call the procedure onequery

	public BinaryCrawler() {
		super();
		logger.info("--------------Binary_search--------------");
	}

	@Override
	public void crawl(String state, int category, String query,
			Envelope evenlopeState) {

		if (logger.isDebugEnabled()) {
			logger.info("-----------crawling---------------");
			logger.info(evenlopeState.toString());
		}
		// finished crawling
		if (evenlopeState == null) {
			return;
		}
		Coordinate startPoint = new Coordinate();
		startPoint.x = (evenlopeState.getMinX() + evenlopeState.getMaxX()) / 2;
		startPoint.y = (evenlopeState.getMinY() + evenlopeState.getMaxY()) / 2;
	}

	public void startQuery(Coordinate startPoint, String state, int category, String query,Envelope evenlopeState,
    		LinkedList<Coordinate>visitedQ){
    	//issue the first query
    	AQuery Firstquery = new AQuery(startPoint, state, category, query,
				MAX_TOTAL_RESULTS_RETURNED); 
		ResultSet resultSetStart = query(Firstquery);
		countquery++;
		visitedQ.addLast(startPoint);
		queryset.addAll(resultSetStart.getPOIs());
		eligibleset.addAll(queryset);
		countpoint=eligibleset.size();
		int size=resultSetStart.getPOIs().size();
		double radius=startPoint.distance(resultSetStart.getPOIs().get(size-1).getCoordinate());
		inRadius=radius;
		System.out.println("startquery inRadius===="+inRadius+"======");
		
		
		
		Circle aCircle=new Circle(startPoint, radius);
		if(logger.isDebugEnabled()&&PaintShapes.painting){
			PaintShapes.paint.color=PaintShapes.paint.redTranslucence;
			PaintShapes.paint.addCircle(aCircle);
			PaintShapes.paint.myRepaint();
		}
		
		//find the refCoordinate to determine the longest radius of the circumcircle of the MBR
		Coordinate minCoordinate=new Coordinate(evenlopeState.getMinX(),evenlopeState.getMinY());
		Coordinate maxCoordinate=new Coordinate(evenlopeState.getMaxX(),evenlopeState.getMaxY());
		Coordinate refCoordinate=new Coordinate();
		if(startPoint.distance(minCoordinate)>startPoint.distance(maxCoordinate))
			refCoordinate=minCoordinate;
		else refCoordinate=maxCoordinate;

		while(countpoint<NEED_POINTS_NUM){
		     //TODO call a binary search procedure
			//TODO calculate the eligible points
		}
    }
	
	public void binaryQuery(){
		
	}
	
	/*circle: the circle centered at startPoint with radius
	 *line: startPoint and p are on the line
	 */
	public Coordinate[] line_circle_intersect(Coordinate startPoint, double radius,Coordinate p){
		 Coordinate[] a =new Coordinate[2];
		 a[0]=new Coordinate();
		 a[1]=new Coordinate();
		 //the slope of the line:k=infinite
		 if(p.x==startPoint.x){
			a[0].x=startPoint.x;
			a[0].y=startPoint.y+radius;
			a[1].x=startPoint.x;
			a[1].y=startPoint.y-radius;
		 }
		 //k=0
		 else if(p.y==startPoint.y){
			 a[0].x=startPoint.x+radius;
			 a[0].y=startPoint.y;
			 a[1].x=startPoint.x-radius;
			 a[1].y=startPoint.y;
		 }
		 else{
			 double k=(p.y-startPoint.y)/(p.x-startPoint.x);
			 double A=Math.sqrt((radius*radius)/(1+k*k));
			 a[0].x=startPoint.x+A;
			 a[0].y=startPoint.y+k*A;
			 a[1].x=startPoint.x-A;
			 a[1].y=startPoint.y-k*A;
		 }
		 return a;
	}
}
