package zyxhj.utils.data.ts;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.CapacityUnit;
import com.alicloud.openservices.tablestore.model.CreateTableRequest;
import com.alicloud.openservices.tablestore.model.DeleteTableRequest;
import com.alicloud.openservices.tablestore.model.PrimaryKeySchema;
import com.alicloud.openservices.tablestore.model.ReservedThroughput;
import com.alicloud.openservices.tablestore.model.TableMeta;
import com.alicloud.openservices.tablestore.model.TableOptions;
import com.alicloud.openservices.tablestore.model.search.CreateSearchIndexRequest;
import com.alicloud.openservices.tablestore.model.search.DeleteSearchIndexRequest;
import com.alicloud.openservices.tablestore.model.search.FieldSchema;
import com.alicloud.openservices.tablestore.model.search.IndexSchema;

import zyxhj.utils.data.ts.TSAnnID.Key;

public class TSUtils {

	public static void createTableByEntity(SyncClient client, Class entityClass) {
		try {
			createTable(client, entityClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void drapTableByEntity(SyncClient client, Class entityClass) {
		try {
			deleteTable(client, entityClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deleteTable(SyncClient client, Class entityClass) throws Exception {
		TSAnnEntity annEntity = (TSAnnEntity) entityClass.getAnnotation(TSAnnEntity.class);
		if (null == annEntity) {
			// 没有注解，建表错误
			System.out.println("类缺少Entity注解");
			return;
		} else {

			// 先删索引
			HashMap<String, ArrayList<Field>> indexColumnsMap = new HashMap<>();
			Field[] classFields = entityClass.getFields();
			for (Field cf : classFields) {
				if (!Modifier.isStatic(cf.getModifiers())) {
					TSAnnIndex annIndex = cf.getAnnotation(TSAnnIndex.class);
					if (annIndex != null) {
						String name = annIndex.name();

						ArrayList<Field> indexFields = indexColumnsMap.get(name);
						if (indexFields != null) {
							indexFields.add(cf);
						} else {
							// 新的一组索引
							indexFields = new ArrayList<>();
							indexFields.add(cf);
							indexColumnsMap.put(name, indexFields);
						}
					}
				}
			}

			Iterator<String> keys = indexColumnsMap.keySet().iterator();
			while (keys.hasNext()) {
				// 按分组名逐组创建多元索引
				String key = keys.next();

				DeleteSearchIndexRequest request = new DeleteSearchIndexRequest();
				request.setTableName(annEntity.alias()); // 设置表名
				request.setIndexName(key); // 设置索引名
				client.deleteSearchIndex(request); // 调用client删除对应的多元索引
			}

			// 最后删除表
			DeleteTableRequest request = new DeleteTableRequest(annEntity.alias());
			client.deleteTable(request);
		}
	}

	private static void createTable(SyncClient client, Class entityClass) throws Exception {

		TSAnnEntity annEntity = (TSAnnEntity) entityClass.getAnnotation(TSAnnEntity.class);

		Field[] pks = new Field[4];
		int pkCount = 0;

		ArrayList<Field> normalColumns = new ArrayList<>();
		HashMap<String, ArrayList<Field>> indexColumnsMap = new HashMap<>();

		if (null == annEntity) {
			// 没有注解，建表错误
			System.out.println("类缺少Entity注解");
			return;
		} else {

			Field[] classFields = entityClass.getFields();
			for (Field cf : classFields) {
				if (!Modifier.isStatic(cf.getModifiers())) {

					TSAnnID annId = cf.getAnnotation(TSAnnID.class);
					TSAnnIndex annIndex = cf.getAnnotation(TSAnnIndex.class);
					TSAnnField annField = cf.getAnnotation(TSAnnField.class);

					if (null != annId) {
						// ID列，按顺序填入数组中
						Key kt = annId.key();
						if (kt == TSAnnID.Key.PK1) {
							pks[0] = cf;
							pkCount = 1;
						} else if (kt == TSAnnID.Key.PK2) {
							pks[1] = cf;
							pkCount = 2;
						} else if (kt == TSAnnID.Key.PK3) {
							pks[2] = cf;
							pkCount = 3;
						} else if (kt == TSAnnID.Key.PK4) {
							pks[3] = cf;
							pkCount = 4;
						}
					} else {
						// 字段列
						if (null != annIndex) {
							// 有索引的字段列
							// 一个表可能有多组多元索引，需要按索引名区分

							String name = annIndex.name();

							ArrayList<Field> indexFields = indexColumnsMap.get(name);
							if (indexFields != null) {
								indexFields.add(cf);
							} else {
								// 新的一组索引
								indexFields = new ArrayList<>();
								indexFields.add(cf);
								indexColumnsMap.put(name, indexFields);
							}
						} else {
							// 普通的字段列
							normalColumns.add(cf);
						}
					}
				}
			}

			// {
			// // 输出测试
			// System.out.println("------------------");
			// for (int i = 0; i < pkCount; i++) {
			// Field pkField = pks[i];
			// String pkName = pkField.getName();
			// System.out.print(StringUtils.join("pk", i, "=", pkName, " "));
			// System.out.println();
			// }
			// System.out.println("------------------");
			//
			// Iterator<String> keys = indexColumnsMap.keySet().iterator();
			// while (keys.hasNext()) {
			// String key = keys.next();
			// System.out.println(StringUtils.join("---index>", key, ">>>"));
			// ArrayList<Field> fields = indexColumnsMap.get(key);
			// for (Field field : fields) {
			// System.out.println(StringUtils.join("------>", field.getName()));
			// }
			// }
			// System.out.println("------------------");
			// System.out.println(StringUtils.join("---normalColumns>>>"));
			// for (Field field : normalColumns) {
			// System.out.println(StringUtils.join("------>", field.getName()));
			// }
			// }

			String tableName = annEntity.alias();
			int timeToLive = annEntity.timeToLive(); // 数据的过期时间，单位秒, -1代表永不过期，例如设置过期时间为一年, 即为 365 * 24 * 3600。
			int maxVersions = annEntity.maxVersions(); // 保存的最大版本数，设置为3即代表每列上最多保存3个最新的版本。

			TableMeta tableMeta = new TableMeta(tableName);
			// 构造主键
			for (int i = 0; i < pkCount; i++) {
				Field pkField = pks[i];
				String pkName = pkField.getName();

				TSAnnID annId = pkField.getAnnotation(TSAnnID.class);

				// 为主表添加主键列
				tableMeta.addPrimaryKeyColumn(new PrimaryKeySchema(pkName, annId.type()));
			}

			// 先构造表
			{
				TableOptions tableOptions = new TableOptions(timeToLive, maxVersions);
				CreateTableRequest request = new CreateTableRequest(tableMeta, tableOptions);
				request.setReservedThroughput(new ReservedThroughput(new CapacityUnit(0, 0))); // 设置读写预留值，容量型实例只能设置为0，高性能实例可以设置为非零值。

				client.createTable(request);
			}

			// 然后开始构造多元索引
			// TableStore无需构造列（目前不支持二级索引，只支持多元索引）
			// 因此跳过没有索引的列

			Iterator<String> keys = indexColumnsMap.keySet().iterator();
			while (keys.hasNext()) {
				// 按分组名逐组创建多元索引
				String key = keys.next();

				CreateSearchIndexRequest request = new CreateSearchIndexRequest();
				request.setTableName(tableName); // 设置表名
				request.setIndexName(key); // 设置索引名

				ArrayList<FieldSchema> fieldSchemas = new ArrayList<>();
				ArrayList<Field> fields = indexColumnsMap.get(key);
				for (Field field : fields) {
					TSAnnIndex annIndex = field.getAnnotation(TSAnnIndex.class);

					FieldSchema fieldSchema = new FieldSchema(field.getName(), annIndex.type());
					fieldSchema.setIndex(annIndex.index());
					fieldSchema.setEnableSortAndAgg(annIndex.enableSortAndAgg());
					fieldSchema.setIsArray(annIndex.isArray());
					fieldSchema.setStore(annIndex.store());

					fieldSchemas.add(fieldSchema);
				}

				IndexSchema indexSchema = new IndexSchema();
				indexSchema.setFieldSchemas(fieldSchemas);
				request.setIndexSchema(indexSchema);
				client.createSearchIndex(request); // 调用client创建SearchIndex
			}

		}

	}

}
