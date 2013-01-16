package mo.umac.utils.test;

import java.math.BigDecimal;

public class SimpleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleTest st = new SimpleTest();
		// st.testInt();
		st.testString();

	}

	private void testString() {
		double radius = 0.0007100657497539307;
		StringBuffer sb = new StringBuffer();
		String r2 = new BigDecimal(radius).toPlainString();
		sb.append("&radius=");
		sb.append(r2);
		System.out.println(sb.toString());
	}

	/**
	 * Test whether a integer variable can be assign the value "null"
	 */
	private void testInt() {
		int a;
		a = (Integer) null;
		if ((Integer) a == null) {
			System.out.println("(Integer)a is null");
		} else {
			System.out.println("(Integer)a is not null");
		}
	}

}
