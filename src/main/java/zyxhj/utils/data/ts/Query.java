package zyxhj.utils.data.ts;

import com.alicloud.openservices.tablestore.model.search.SearchQuery;

public class Query {

	private long offset = 0;
	private long limit = 10;

	public Query() {
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public SearchQuery build() {
		return null;
	}
}
