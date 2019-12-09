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
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * @author Clinton Begin
 */
public interface Interceptor {

  /**
   * 核心方法，会覆盖原来的方法，invocation是个拦截器，它实现的invoke最终实现增强
   * @param invocation
   * @return
   * @throws Throwable
   */
  Object intercept(Invocation invocation) throws Throwable;

  /**
   * 这个相当于bind，创建代理对象，target是委托类
   * @param target
   * @return
   */
  default Object plugin(Object target) {
    // 这里就相当于创建构造器，这里面调用Plugin，然后Plugin再返回代理对象，这个叫啥模式，相当于Plugin是更详细的一层处理
    // 而且还会把plugin中的invoke传给代理类，然后plugin的invoke又调用回来去执行intercept方法
    return Plugin.wrap(target, this);
  }

  /**
   * 初始化插件的时候，设置一些参数
   * @param properties
   */
  default void setProperties(Properties properties) {
    // NOP
  }

}
