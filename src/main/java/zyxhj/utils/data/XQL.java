package zyxhj.utils.data;

public class XQL {

	private static final Byte TYPE_SELECT = 0;
	private static final Byte TYPE_INSERT = 1;
	private static final Byte TYPE_UPDATE = 2;
	private static final Byte TYPE_DELETE = 3;

	private Byte type;

	private Integer count;
	private Integer offset;

	private String[] selections;
	private EXP set;
	private EXP where;

	public static XQL queryList(String table, EXP where, Integer count, Integer offset, String... selections) {
		XQL ret = new XQL();
		ret.type = TYPE_SELECT;
		ret.where = where;
		ret.selections = selections;
		ret.count = count;
		ret.offset = offset;
		return ret;
	}

	public static XQL query(String table, EXP exp, String... selections) {
		return queryList(table, exp, 1, 0, selections);
	}

}
