package zyxhj.utils.data.ts;

import java.util.Properties;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class TSDataSource implements DataSource {

	private String endPoint;
	private String accessKeyId;
	private String accessKeySecret;
	private String instanceName;

	private TSAutoCloseableClient OTSClient;

	public TSDataSource(Properties props) {
		String endPoint = props.getProperty("endPoint");
		String accessKeyId = props.getProperty("accessKeyId");
		String accessKeySecret = props.getProperty("accessKeySecret");
		String instanceName = props.getProperty("instanceName");

		this.endPoint = endPoint;
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;
		this.instanceName = instanceName;

		OTSClient = new TSAutoCloseableClient(endPoint, accessKeyId, accessKeySecret, instanceName);
	}

	public String getEndPoint() {
		return endPoint;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public String getInstanceName() {
		return instanceName;
	}

	@Override
	public Object openConnection() throws ServerException {
		return OTSClient;
	}

	@Override
	public void closeConnection(Object conn) throws ServerException {
		try {
			((TSAutoCloseableClient) conn).close();
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_CONNECTION_ERROR, e.getMessage());
		}
	}

}