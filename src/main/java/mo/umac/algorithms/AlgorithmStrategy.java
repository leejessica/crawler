package mo.umac.algorithms;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public abstract class AlgorithmStrategy {
	
	protected HttpClient createHttpClient() {
		// ThreadSafeClientConnManager manager = new
		// ThreadSafeClientConnManager();
		// TODO check
		PoolingClientConnectionManager manager = new PoolingClientConnectionManager();

		HttpParams params = new BasicHttpParams();
		int timeout = 1000 * 10;

		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpClient httpClient = new DefaultHttpClient(manager, params);
		return httpClient;
	}
}
