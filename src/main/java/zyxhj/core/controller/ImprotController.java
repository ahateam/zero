package zyxhj.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alicloud.openservices.tablestore.SyncClient;

import zyxhj.core.domain.ImportTask;
import zyxhj.core.service.ImportTaskService;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;

public class ImprotController extends Controller {

	private static Logger log = LoggerFactory.getLogger(ImprotController.class);

	private SyncClient client;

	private DruidDataSource dds;
	private ImportTaskService importTaskService;

	public ImprotController(String node) {
			super(node);
			try {
				dds = DataSource.getDruidDataSource("rdsDefault.prop");
				client = DataSource.getTableStoreSyncClient("tsDefault.prop");

				importTaskService = Singleton.ins(ImportTaskService.class);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

	@ENUM(des = "导入任务状态")
	public ImportTask.STATUS[] voteStatus = ImportTask.STATUS.values();

	/**
	 * 
	 */
	@POSTAPI(//
			path = "importRecord", //
			des = "导入到临时表", //
			ret = "返回省列表"//
	)
	public APIResponse importRecord(//
			@P(t = "批次编号") Long batchId, //
			@P(t = "用户编号") Long userId, //
			@P(t = "excel文件url") String url, //
			@P(t = "导入任务id") Long importTaskId, //
			@P(t = "第几行开始") Integer skipRowCount, //
			@P(t = "总列数") Integer colCount //

	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			importTaskService.importRecord(client, conn, batchId, userId, url, importTaskId, skipRowCount, colCount);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "deleteImportTask", //
			des = "删除导入", //
			ret = "返回新列表"//
	)
	public APIResponse deleteImportTask(//
			@P(t = "导入任务id") Long importTaskId//
	) throws Exception {
		importTaskService.deleteImportTask(client, importTaskId);
		return APIResponse.getNewSuccessResp();
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getFailImportRecord", //
			des = "获取错误数据", //
			ret = "返回错误数据"//
	)
	public APIResponse getFailImportRecord(//
			@P(t = "导入任务id") Long importTaskId, //
			Integer count, //
			Integer offset //
	) throws Exception {
		return APIResponse
				.getNewSuccessResp(importTaskService.getFailImportRecord(client, importTaskId, count, offset));
	}


	/**
	 * 获取导入列表
	 */
	@POSTAPI(//
			path = "getListImportTemp", //
			des = "获取导入列表", //
			ret = "返回导入列表"//
	)
	public APIResponse getListImportTemp(//
			@P(t = "导入任务id") Long importTaskId, //
			Integer count, //
			Integer offset //
	) throws Exception {

		return APIResponse.getNewSuccessResp(importTaskService.getListImportTemp(client, importTaskId, count, offset));
	}

	/**
	 * 获取导入列表
	 */
	@POSTAPI(//
			path = "createImportTask", //
			des = "创建导入任务", //
			ret = ""//
	)
	public APIResponse createImportTask(//
			@P(t = "导入名称") String title, //
			@P(t = "组织id") Long batchId, //
			@P(t = "用户id") Long userId, //
			@P(t = "导入类型 0为用户导入  1为资产导入") Byte type//
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {
			importTaskService.createImportTask(conn, title, batchId, userId, type);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 获取任务列表
	 */
	@POSTAPI(//
			path = "getListImportTask", //
			des = "获取导入任务", //
			ret = "返回导入任务列表"//
	)
	public APIResponse getListImportTask(//
			@P(t = "组织id") Long orgId, //
			@P(t = "类型", r = false) Byte type, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(importTaskService.getListImportTask(conn, orgId, type, count, offset));
		}
	}

	/**
	 * 获取任务列表
	 */
	@POSTAPI(//
			path = "getImportTask", //
			des = "获取导入任务", //
			ret = "返回导入任务列表"//
	)
	public APIResponse getImportTask(//
			@P(t = "组织id") Long taskId //
	) throws Exception {
		try (DruidPooledConnection conn = dds.getConnection()) {

			return APIResponse.getNewSuccessResp(importTaskService.getImportTask(conn, taskId));
		}
	}
}
