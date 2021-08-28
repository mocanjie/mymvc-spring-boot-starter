package io.github.mocanjie.base.mymvc.service;

import io.github.mocanjie.base.mycommon.pager.Pager;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public interface IBaseService {

	<T> Pager queryPageForSql(String sql, Object param, Pager pager, Class<T> clazz);

	<T> List<T> queryListForSql(String sql, Object param, Class<T> clazz);

	<T> T querySingleForSql(String sql, Object param, Class<T> clazz);

	<T> T querySingleByField(String fieldName,String fieldValue, Class<T> clazz);

	<T> Pager queryPageForSql(String sql, Map<String, Object> param, Pager pager, Class<T> clazz);

	<T> List<T> queryListForSql(String sql, Map<String, Object> param, Class<T> clazz);

	<T> T querySingleForSql(String sql, Map<String, Object> param, Class<T> clazz);

	<PO> Serializable insertPO(PO po, boolean autoCreateId);

	<PO> Serializable insertPO(PO po);

	<PO> Serializable batchInsertPO(List<PO> pos, boolean autoCreateId);

	<PO> Serializable batchInsertPO(List<PO> pos);

	<PO> int updatePO(PO po);

	<PO> int updatePO(PO po,boolean ignoreNull);

	<PO> int updatePO(PO po,@Nullable String... forceUpdateProperties);

	<PO> PO queryById(String id, Class<PO> clazz);

	<PO> PO queryById(Long id, Class<PO> clazz);

	<PO> int delPO(PO po);

	<PO> int delByIds(Class<PO> clazz, Object... id);

	<PO> int delById4logic(Class<PO> clazz, Object... id);

}
