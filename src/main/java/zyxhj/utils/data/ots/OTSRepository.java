package zyxhj.utils.data.ots;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.BatchGetRowRequest;
import com.alicloud.openservices.tablestore.model.BatchGetRowResponse;
import com.alicloud.openservices.tablestore.model.BatchGetRowResponse.RowResult;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.Condition;
import com.alicloud.openservices.tablestore.model.DeleteRowRequest;
import com.alicloud.openservices.tablestore.model.GetRangeRequest;
import com.alicloud.openservices.tablestore.model.GetRangeResponse;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.MultiRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.RangeRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowDeleteChange;
import com.alicloud.openservices.tablestore.model.RowExistenceExpectation;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;
import com.alicloud.openservices.tablestore.model.UpdateRowRequest;

import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public abstract class OTSRepository<T> {

	protected OTSObjectMapper<T> mapper;

	protected OTSRepository(Class<T> clazz) {
		this.mapper = new OTSObjectMapper<T>(clazz);
	}

	/**
	 * 原生接口，插入一行数据
	 * 
	 * @param tableName
	 *            表名
	 * @param pk
	 *            主键
	 * @param columns
	 *            要存储的数据列表
	 * @param cover
	 *            如果已经有值，是否覆盖
	 */
	public static void nativeInsert(SyncClient client, String tableName, PrimaryKey pk, List<Column> columns,
			boolean cover) throws ServerException {
		try {
			RowPutChange putChange = new RowPutChange(tableName, pk);

			if (!cover) {
				// 不覆盖
				// 预期不存在，如果存在则异常
				putChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));
			}

			for (Column col : columns) {
				if (null == col) {
					// 如果为空，则跳过，不写入也不更新
				} else {
					putChange.addColumn(col);
				}
			}

			// 开始写入
			client.putRow(new PutRowRequest(putChange));
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_PUT_ERROR, e.getMessage());
		}
	}

	/**
	 * 原生接口，更新一行数据，空数据会自动跳过
	 * 
	 * @param tableName
	 *            表名
	 * @param pk
	 *            主键
	 * @param columns
	 *            要存储的数据列表
	 */
	public static void nativeUpdate(SyncClient client, String tableName, PrimaryKey pk, List<Column> columns)
			throws ServerException {
		try {
			RowUpdateChange updateChange = new RowUpdateChange(tableName, pk);

			for (Column col : columns) {
				if (null == col) {
					// 如果为空，跳过，不更新
				} else {
					updateChange.put(col);
				}
			}

			// 开始写入
			client.updateRow(new UpdateRowRequest(updateChange));
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_UPDATE_ERROR, e.getMessage());
		}
	}

	/**
	 * 原生接口，查询一行数据
	 * 
	 * @param tableName
	 *            表名
	 * @param pk
	 *            主键
	 * @param selections
	 *            可选参数，要查询的列名，不填则查询所有列
	 * @return 查询到的记录，JSONObject格式
	 */
	public static JSONObject nativeGet(SyncClient client, String tableName, PrimaryKey pk, String... selections)
			throws ServerException {
		Row row = _get(client, tableName, pk, selections);
		if (row.isEmpty()) {
			return null;
		} else {
			return OTSObjectMapper.deserialize2JSONObject(row);
		}
	}

	private static Row _get(SyncClient client, String tableName, PrimaryKey pk, String... selections)
			throws ServerException {
		try {
			// 读一行
			SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(tableName, pk);
			// 设置读取最新版本
			criteria.setMaxVersions(1);
			if (selections != null && selections.length > 0) {
				criteria.addColumnsToGet(selections);
			}
			GetRowResponse getRowResponse = client.getRow(new GetRowRequest(criteria));
			Row row = getRowResponse.getRow();
			if (row == null || row.isEmpty()) {
				return null;
			} else {
				return row;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_GET_ERROR, e.getMessage());
		}
	}

	/**
	 * 原生接口，删除一行数据
	 * 
	 * @param tableName
	 *            表名
	 * @param pk
	 *            主键
	 */
	public static void nativeDel(SyncClient client, String tableName, PrimaryKey pk) throws ServerException {
		try {
			RowDeleteChange rowChange = new RowDeleteChange(tableName, pk);
			rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_EXIST));

			DeleteRowRequest request = new DeleteRowRequest();
			request.setRowChange(rowChange);
			client.deleteRow(request);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_DELETE_ERROR);
		}
	}

	/**
	 * 原生接口，按主键范围查询</br>
	 * 因TableStore特性，遍历策略是读出来后，在客户端进行过滤</br>
	 * 因此，offset跳过的数据，也会产生读取操作并消耗资源</br>
	 * 总之必须小心使用本方法，避免滥用，offset + count的总数应该避免过大。
	 * 
	 * @param tableName
	 *            表名
	 * @param pkStart
	 *            开始主键
	 * @param pkEnd
	 *            结束主键
	 * @param count
	 *            查询数量，数量不宜过大
	 * @param offset
	 *            起始偏移位置，数量不宜过大
	 * @param selections
	 *            可选参数，要查询的列名，不填则查询所有列
	 * @return 查询到的记录，JSONArray格式
	 */
	public static JSONArray nativeGetRange(SyncClient client, String tableName, PrimaryKey pkStart, PrimaryKey pkEnd,
			Integer count, Integer offset, String... selections) throws ServerException {

		int limit = count;
		int skip = offset;

		List<Row> rows = new ArrayList<Row>(limit);
		PrimaryKey nextStart = pkStart;
		while (limit > 0 && nextStart != null) {
			// 构造GetRange的查询参数。
			// 注意：startPrimaryKey需要设置为上一次读到的位点，从上一次未读完的地方继续往下读，实现流式的范围查询。
			RangeRowQueryCriteria criteria = new RangeRowQueryCriteria(tableName);
			criteria.setInclusiveStartPrimaryKey(nextStart);
			criteria.setExclusiveEndPrimaryKey(pkEnd);
			// 需要设置正确的limit，这里期望读出的数据行数最多为完整的一页数据以及需要过滤(offset)的数据
			criteria.setLimit(skip + limit);

			criteria.setMaxVersions(1);
			if (selections != null && selections.length > 0) {
				criteria.addColumnsToGet(selections);
			}

			GetRangeRequest request = new GetRangeRequest();
			request.setRangeRowQueryCriteria(criteria);
			GetRangeResponse response = client.getRange(request);
			for (Row row : response.getRows()) {
				if (skip > 0) {
					skip--; // 对于offset之前的数据，需要过滤掉，采用的策略是读出来后在客户端进行过滤。
				} else {
					rows.add(row);
					limit--;
				}
			}
			// 设置下一次查询的起始位点
			nextStart = response.getNextStartPrimaryKey();
		}

		JSONArray ret = new JSONArray();
		for (Row row : rows) {
			ret.add(OTSObjectMapper.deserialize2JSONObject(row));
		}
		return ret;
	}

	/**
	 * 原生接口，按主键列表批量查询</br>
	 * 
	 * @param tableName
	 *            表名
	 * @param pks
	 *            主键列表
	 * @param selections
	 *            可选参数，要查询的列名，不填则查询所有列
	 * @return 查询到的记录，JSONArray格式
	 */
	public static JSONArray nativeBatchGet(SyncClient client, String tableName, List<PrimaryKey> pks,
			String... selections) throws ServerException {
		List<Row> rows = _batchGet(client, tableName, pks, selections);
		JSONArray ret = new JSONArray();
		for (Row row : rows) {
			if (row == null || row.isEmpty()) {
				// 异常，插入一个空数据，数组对齐，方便后续处理
				ret.add(new JSONObject(true));
			} else {
				ret.add(OTSObjectMapper.deserialize2JSONObject(row));
			}
		}
		return ret;
	}

	private static List<Row> _batchGet(SyncClient client, String tableName, List<PrimaryKey> pks, String... selections)
			throws ServerException {
		MultiRowQueryCriteria criteria = new MultiRowQueryCriteria(tableName);
		for (PrimaryKey pk : pks) {
			criteria.addRow(pk);
		}

		criteria.setMaxVersions(1);
		if (selections != null && selections.length > 0) {
			criteria.addColumnsToGet(selections);
		}

		BatchGetRowRequest batchGetRowRequest = new BatchGetRowRequest();
		batchGetRowRequest.addMultiRowQueryCriteria(criteria);

		List<Row> ret = new ArrayList<>();

		BatchGetRowResponse response = client.batchGetRow(batchGetRowRequest);
		Iterator<RowResult> it = response.getBatchGetRowResult(tableName).iterator();
		while (it.hasNext()) {
			RowResult rr = it.next();
			Row row = rr.getRow();
			if (row == null || row.isEmpty()) {
				// 异常，插入一个空数据，数组对齐，方便后续处理
				ret.add(null);
			} else {
				ret.add(row);
			}
		}

		return ret;
	}

	/**
	 * 插入对象，检查是否存在，如果不存在，插入；否则插入失败
	 * 
	 */
	public void insert(OTSAutoCloseableClient client, T t, boolean cover) throws ServerException {
		nativeInsert(client, mapper.getTableName(), mapper.getPrimaryKeyFromObject(t),
				mapper.getColumnListFromObject(t), cover);
	}

	/**
	 * 根据主键获取对象
	 */
	public T get(OTSAutoCloseableClient client, PrimaryKey pk, String... selections) throws Exception {
		Row row = _get(client, mapper.getTableName(), pk, selections);
		if (row == null || row.isEmpty()) {
			return null;
		} else {
			return mapper.deserialize(row);
		}
	}

	/**
	 * 根据主键删除对象
	 */
	public void delete(OTSAutoCloseableClient client, PrimaryKey pk) throws ServerException {
		nativeDel(client, mapper.getTableName(), pk);
	}

	/**
	 * 根据主键列表批量获取对象列表
	 */
	public List<T> batchGet(SyncClient client, List<PrimaryKey> pks, String... selections) throws Exception {
		List<Row> rows = _batchGet(client, mapper.getTableName(), pks, selections);
		List<T> ret = new ArrayList<>();
		for (Row row : rows) {
			if (row == null || row.isEmpty()) {
				ret.add(null);
			} else {
				ret.add(mapper.deserialize(row));
			}
		}
		return ret;
	}

}
