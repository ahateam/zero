package zyxhj.flow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.flow.domain.Department;
import zyxhj.flow.repository.DepartmentRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.DataSource;

public class DepartmentService {

	private static Logger log = LoggerFactory.getLogger(FlowService.class);

	private DruidDataSource ds;
	
	private DepartmentRepository departmentRepository;
	
	
	public DepartmentService() {
		try {
			ds = DataSource.getDruidDataSource("rdsDefault.prop");
			departmentRepository = Singleton.ins(DepartmentRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	

	public void createDepartment(DruidPooledConnection conn, String name, String remark) {
		
		Department d = new Department();
		d.id = IDUtils.getSimpleId();
		d.name = name ;
		d.remark = remark;
		try {
			departmentRepository.insert(conn, d);
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	
	
}
