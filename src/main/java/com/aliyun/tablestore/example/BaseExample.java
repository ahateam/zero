package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.utils.data.DataSource;

public abstract class BaseExample {
	protected SyncClient syncClient;

	public BaseExample() {

		try {
			syncClient = DataSource.getTableStoreSyncClient("tsDefault.prop");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
