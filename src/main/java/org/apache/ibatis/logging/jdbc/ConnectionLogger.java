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
package org.apache.ibatis.logging.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * Connection proxy to add logging.
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 * BaseJdbcLogger看完，现在来看看代理类在忙什么
 * 看名字是关于连接的
 * 要理解这一串的动态代理，首先要知道jdbc是怎么执行数据库操作的
 * 每一步生成不同的对象，是代理套代理，然后执行方法吗，捋一捋，这里面的文章绝对不像芋道源码中说的那么轻描淡写
 * 它相当于是给一套模板写一个增强，举个现实中的例子，比如企业培训规范搞一个多语言版本
 */
public final class ConnectionLogger extends BaseJdbcLogger implements InvocationHandler {

  /** 里面有一个委托的连接对象在*/
  private final Connection connection;

  private ConnectionLogger(Connection conn, Log statementLog, int queryStack) {
    super(statementLog, queryStack);
    this.connection = conn;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] params)
      throws Throwable {
    try {
      // 如果是method是Object的方法，那么不作处理，但是这个invoke传入的实例是代理类自己，
      // 意思是自己执行，实际上和Connection执行结果应该是一样的把
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, params);
      }
      // 这Connection接口这三个方法执行的时候打印日志
      if ("prepareStatement".equals(method.getName())) {
        if (isDebugEnabled()) {
          debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
        }
        PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
        // 执行了原有的方法还会继续把代理类返回，这么看来就是责任链模式了，妙！
        stmt = PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
        return stmt;
      } else if ("prepareCall".equals(method.getName())) {
        if (isDebugEnabled()) {
          debug(" Preparing: " + removeBreakingWhitespace((String) params[0]), true);
        }
        PreparedStatement stmt = (PreparedStatement) method.invoke(connection, params);
        stmt = PreparedStatementLogger.newInstance(stmt, statementLog, queryStack);
        return stmt;
      } else if ("createStatement".equals(method.getName())) {
        Statement stmt = (Statement) method.invoke(connection, params);
        stmt = StatementLogger.newInstance(stmt, statementLog, queryStack);
        return stmt;
      } else {
        return method.invoke(connection, params);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }
  }

  /**
   * Creates a logging version of a connection.
   *
   * @param conn - the original connection
   * @return - the connection with logging
   * 生成代理类
   */
  public static Connection newInstance(Connection conn, Log statementLog, int queryStack) {
    InvocationHandler handler = new ConnectionLogger(conn, statementLog, queryStack);
    ClassLoader cl = Connection.class.getClassLoader();
    // jdk动态代理玩出花来了，看看这三个参数，之前第二个参数Interfaces按理说应该是conn.getInterfaces，获取
    // 到这个连接所有实现的接口类，但是因为我们这个代理只是针对Connection接口，所以直接指定接口完全可以，谁管你，是吧
    // 因为第三个参数要执行invoke方法，所以传入this，但是这里也玩了些花样，初始化一个InvocationHandler实例传进去，
    // 子类是父类，这个千千万万不能只停留在记忆层面，一定要多多理解
    return (Connection) Proxy.newProxyInstance(cl, new Class[]{Connection.class}, handler);
  }

  /**
   * return the wrapped connection.
   *
   * @return the connection
   */
  public Connection getConnection() {
    return connection;
  }

}
