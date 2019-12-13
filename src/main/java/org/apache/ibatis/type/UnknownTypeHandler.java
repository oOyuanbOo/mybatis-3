/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;

/**
 * @author Clinton Begin
 * 不知道是个什么类型该怎么映射？
 */
public class UnknownTypeHandler extends BaseTypeHandler<Object> {

  private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();

  /**
   * 初始化配置的一块内容，类型控制注册器出现了
   */
  private TypeHandlerRegistry typeHandlerRegistry;

  public UnknownTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
    this.typeHandlerRegistry = typeHandlerRegistry;
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
      throws SQLException {
    // 先到注册器里去找找有没有这个javaType对应的jdbcType
    TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
    handler.setParameter(ps, i, parameter, jdbcType);
  }

  @Override
  public Object getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    // 同样的返回的jdbcType也需要到注册器里找下映射
    TypeHandler<?> handler = resolveTypeHandler(rs, columnName);
    return handler.getResult(rs, columnName);
  }

  @Override
  public Object getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    TypeHandler<?> handler = resolveTypeHandler(rs.getMetaData(), columnIndex);
    if (handler == null || handler instanceof UnknownTypeHandler) {
      handler = OBJECT_TYPE_HANDLER;
    }
    return handler.getResult(rs, columnIndex);
  }

  @Override
  public Object getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return cs.getObject(columnIndex);
  }

  private TypeHandler<?> resolveTypeHandler(Object parameter, JdbcType jdbcType) {
    TypeHandler<?> handler;
    // null对应的是Object
    if (parameter == null) {
      handler = OBJECT_TYPE_HANDLER;
    } else {
      // 不为空就到注册器里去找
      handler = typeHandlerRegistry.getTypeHandler(parameter.getClass(), jdbcType);
      // check if handler is null (issue #270)
      if (handler == null || handler instanceof UnknownTypeHandler) {
        handler = OBJECT_TYPE_HANDLER;
      }
    }
    return handler;
  }

  private TypeHandler<?> resolveTypeHandler(ResultSet rs, String column) {
    try {
      Map<String,Integer> columnIndexLookup;
      columnIndexLookup = new HashMap<>();
      // metaData相当于是以Reflection为基础的增强类，更为便捷的使用反射
      ResultSetMetaData rsmd = rs.getMetaData();
      int count = rsmd.getColumnCount();
      for (int i = 1; i <= count; i++) {
        String name = rsmd.getColumnName(i);
        // 把返回值和返回值位置作映射存储到查询map里
        columnIndexLookup.put(name,i);
      }
      Integer columnIndex = columnIndexLookup.get(column);
      TypeHandler<?> handler = null;
      if (columnIndex != null) {
        handler = resolveTypeHandler(rsmd, columnIndex);
      }
      if (handler == null || handler instanceof UnknownTypeHandler) {
        handler = OBJECT_TYPE_HANDLER;
      }
      return handler;
    } catch (SQLException e) {
      throw new TypeException("Error determining JDBC type for column " + column + ".  Cause: " + e, e);
    }
  }

  private TypeHandler<?> resolveTypeHandler(ResultSetMetaData rsmd, Integer columnIndex) {
    TypeHandler<?> handler = null;
    // 知道位置了，就去获取jdbcType
    JdbcType jdbcType = safeGetJdbcTypeForColumn(rsmd, columnIndex);
    Class<?> javaType = safeGetClassForColumn(rsmd, columnIndex);
    if (javaType != null && jdbcType != null) {
      handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
    } else if (javaType != null) {
      handler = typeHandlerRegistry.getTypeHandler(javaType);
    } else if (jdbcType != null) {
      handler = typeHandlerRegistry.getTypeHandler(jdbcType);
    }
    return handler;
  }

  private JdbcType safeGetJdbcTypeForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
    try {
      return JdbcType.forCode(rsmd.getColumnType(columnIndex));
    } catch (Exception e) {
      return null;
    }
  }

  private Class<?> safeGetClassForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
    try {
      return Resources.classForName(rsmd.getColumnClassName(columnIndex));
    } catch (Exception e) {
      return null;
    }
  }
}
