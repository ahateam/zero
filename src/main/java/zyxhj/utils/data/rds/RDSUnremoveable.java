package zyxhj.utils.data.rds;

public class RDSUnremoveable {

	/**
	 * 是否可用
	 */
	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean alive = true;

	/**
	 * 是否可用标记
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Long aliveTimestamp;

}
