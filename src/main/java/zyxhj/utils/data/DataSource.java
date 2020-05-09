package zyxhj.utils.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.utils.Utils;

/**
 */
public class DataSource {

	private static Logger log = LoggerFactory.getLogger(DataSource.class);

	private static Map<String, SyncClient> tsSyncClientMap = new HashMap<>();

	public static SyncClient getTableStoreSyncClient(String configFileName) throws Exception {
		SyncClient syncClient = tsSyncClientMap.get(configFileName);
		if (null == syncClient) {
			Properties p = Utils.readProperties(StringUtils.join("configs/", configFileName));

			String endPoint = p.getProperty("endPoint");
			String accessKeyId = p.getProperty("accessKeyId");
			String accessKeySecret = p.getProperty("accessKeySecret");
			String instanceName = p.getProperty("instanceName");

			syncClient = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName);

			tsSyncClientMap.put(configFileName, syncClient);
		}
		return syncClient;
	}

	private static Map<String, AsyncClient> tsAsyncClientMap = new HashMap<>();

	public static AsyncClient getTableStoreAsyncClient(String configFileName) throws Exception {
		AsyncClient asyncClient = tsAsyncClientMap.get(configFileName);
		if (null == asyncClient) {

			Properties p = Utils.readProperties(StringUtils.join("configs/", configFileName));

			String endPoint = p.getProperty("endPoint");
			String accessKeyId = p.getProperty("accessKeyId");
			String accessKeySecret = p.getProperty("accessKeySecret");
			String instanceName = p.getProperty("instanceName");

			asyncClient = new AsyncClient(endPoint, accessKeyId, accessKeySecret, instanceName);

			tsAsyncClientMap.put(configFileName, asyncClient);
		}
		return asyncClient;
	}

	private static Map<String, DruidDataSource> rdsDruidDataSourceMap = new HashMap<>();

	public static DruidDataSource getDruidDataSource(String configFileName) throws Exception {
		DruidDataSource dds = rdsDruidDataSourceMap.get(configFileName);
		if (null == dds) {

			Properties p = Utils.readProperties(StringUtils.join("configs/", configFileName));

			dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(p);

			rdsDruidDataSourceMap.put(configFileName, dds);
		}
		return dds;
	}

	public static <X> X list2Obj(List<X> list) {
		if (list == null || list.size() <= 0) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
