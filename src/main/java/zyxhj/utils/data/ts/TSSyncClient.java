package zyxhj.utils.data.ts;

import com.alicloud.openservices.tablestore.SyncClient;

public class TSSyncClient extends SyncClient implements AutoCloseable {

	public TSSyncClient(String endpoint, String accessKeyId, String accessKeySecret, String instanceName) {
		super(endpoint, accessKeyId, accessKeySecret, instanceName);
	}

	@Override
	public void close() throws Exception {
		// do nothing
		// TS 公用一个连接，不关闭。
		// 实现AutoCloseable只是为了统一代码风格
	}

}
