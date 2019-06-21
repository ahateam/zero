package zyxhj.utils.data.ts;

import java.util.ArrayList;
import java.util.List;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.Condition;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.RowChange;
import com.alicloud.openservices.tablestore.model.RowDeleteChange;
import com.alicloud.openservices.tablestore.model.RowExistenceExpectation;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.RowUpdateChange;

public class RowChangeBuilder {

	private List<RowChange> rowChanges;

	public RowChangeBuilder() {
		rowChanges = new ArrayList<RowChange>();
	}

	public void add(RowChange rowChange) {
		rowChanges.add(rowChange);
	}

	public void put(String tableName, PrimaryKey pk, List<Column> columns, boolean cover) {
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
		rowChanges.add(putChange);
	}

	public void update(String tableName, PrimaryKey pk, List<Column> columns) {
		RowUpdateChange updateChange = new RowUpdateChange(tableName, pk);
		for (Column col : columns) {
			if (null == col) {
				// 如果为空，跳过，不更新
			} else {
				updateChange.put(col);
			}
		}
		rowChanges.add(updateChange);
	}

	public void delete(String tableName, PrimaryKey pk) {
		RowDeleteChange deleteChange = new RowDeleteChange(tableName, pk);
		deleteChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_EXIST));
		rowChanges.add(deleteChange);
	}

	public List<RowChange> build() {
		return rowChanges;
	}
}
