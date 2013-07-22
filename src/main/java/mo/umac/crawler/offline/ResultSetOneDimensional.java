/**
 * 
 */
package mo.umac.crawler.offline;

import java.util.List;

import mo.umac.metadata.APOI;
import mo.umac.metadata.ResultSet;
import mo.umac.spatial.Circle;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

/**
 * @author kate
 * 
 */
public class ResultSetOneDimensional extends ResultSet {

    private LineSegment line;
    private List<APOI> leftPOIs;
    private List<APOI> rightPOIs;
    private List<APOI> onPOIs;

    private int numQueries;
    /**
     * The query circles
     */
    private List<Circle> circles;

    public void addAll(List oneList, List antherList) {
	oneList.addAll(antherList);
    }

    public void addACircle(Circle aCircle) {
	circles.add(aCircle);
    }

    // I don't know why this isn't in Long...
    private static int compare(long a, long b) {
	return a < b ? -1 : a > b ? 1 : 0;
    }

    public int getNumQueries() {
	return numQueries;
    }

    public void setNumQueries(int numQueries) {
	this.numQueries = numQueries;
    }

    public List<Circle> getCircles() {
	return circles;
    }

    public void setCircles(List<Circle> circles) {
	this.circles = circles;
    }

    public List<APOI> getLeftPOIs() {
	return leftPOIs;
    }

    public void setLeftPOIs(List<APOI> leftPOIs) {
	this.leftPOIs = leftPOIs;
    }

    public List<APOI> getRightPOIs() {
	return rightPOIs;
    }

    public void setRightPOIs(List<APOI> rightPOIs) {
	this.rightPOIs = rightPOIs;
    }

    public List<APOI> getOnPOIs() {
	return onPOIs;
    }

    public void setOnPOIs(List<APOI> onLinePOI) {
	this.onPOIs = onLinePOI;
    }

    public LineSegment getLine() {
	return line;
    }

    public void setLine(LineSegment line) {
	this.line = line;
    }

}
