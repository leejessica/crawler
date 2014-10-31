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

public class Binary_search extends OfflineStrategy {
	
	public static int countquery=0;
    public static int NEED_POINTS_NUM=30;
    public static int countpoint=0;
    public static Set<APOI>queryset=new HashSet<APOI>();//record all points queried
    public static Set<APOI>eligibleset=new HashSet<APOI>();//record all eligible points
    public static Coordinate startPoint1=new Coordinate();// record the start point of every level query
    public static double inRadius=0;//using it to keep track of the radius of the mixmum inscribe circle
    public static double CP=4; //using to adjust the binary search
    public static int onequerycount=0;// record the number of call the procedure onequery
    
	public Binary_search(){
		super();
		logger.info("--------------Binary_search--------------");
	}
	
    @Override
	public void crawl(String state, int category, String query, Envelope evenlopeState){
    	
    	if(logger.isDebugEnabled()){
    		logger.info("-----------crawling---------------");
    		logger.info(evenlopeState.toString());
    	}
    	//finished crawling
    	if(evenlopeState==null){
    		return;
    	}
    	Coordinate startPoint=new Coordinate();
    	startPoint.x=(evenlopeState.getMinX()+evenlopeState.getMaxX())/2;
    	startPoint.y=(evenlopeState.getMinY()+evenlopeState.getMaxY())/2;   	
    }
    
    public void startQuery(Coordinate startPoint, String state, int category, String query,
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
		
		//the eligible points is less than the needed points, continue the query procedure
		double min_X=1e38;
		double max_X=1e38;
		double min_Y=1e38;
		double max_Y=1e38;
		double bound=1e38;
		while(countpoint<NEED_POINTS_NUM){
			
		}
    }
}
