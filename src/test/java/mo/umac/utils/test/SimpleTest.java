package mo.umac.utils.test;

public class SimpleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleTest st = new SimpleTest();
		st.testInt();

	}
	
	/**
	 * Test whether a integer variable can be assign the value "null"
	 */
	private void testInt(){
		int a;
		a = (Integer) null;
		if( (Integer)a == null){
			System.out.println("(Integer)a is null");
		}else{
			System.out.println("(Integer)a is not null");
		}
	}

}
