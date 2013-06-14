package mo.umac.db;

public class MainDataSet {

    /**
     * @param args
     */
    public static void main(String[] args) {
	H2DB h2 = new H2DB();
	String folderPath = "../yahoolocal/96926236+Restaurants/NY/";
	String h2Name = "";
	// String folderPath2 = "../yahoolocal/96926236+Restaurants/NY/";
	// String h2Name = ;
//	h2.convertFileDBToH2DB(folderPath, h2Name);
	h2.examData();
    }
    
    

}
