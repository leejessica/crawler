package mo.umac.crawler.utils.test;

import mo.umac.crawler.utils.UScensusData;

public class UScensusDataTest {

	public static void main(String[] args) {
//		String shpFileName = "./src/main/resources/gz_2010_us_outline_500k/gz_2010_us_outline_500k.shp";
		String shpFileName = "./src/main/resources/UScensus/tl_2012_us_state/tl_2012_us_state.shp";
		UScensusData operator = new UScensusData();
		operator.MBR(shpFileName);
	}

}
