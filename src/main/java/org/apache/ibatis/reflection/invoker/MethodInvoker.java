/**
 *    Copyright 2009-2018 the original author or authors.
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
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.ibatis.reflection.Reflector;

/**
 * @author Clinton Begin
 * todo: 不知道这个类怎么用的，在MetaClass::getGenericGetterType里用到了
 */
public class MethodInvoker implements Invoker {

  private final Class<?> type;
  private final Method method;

  // 构造方法存了一个method类
  // 把有一个参数的方法的参数类型存了下来
  // 以及无参的返回类型
  public MethodInvoker(Method method) {
    this.method = method;

    if (method.getParameterTypes().length == 1) {
      type = method.getParameterTypes()[0];
    } else {
      type = method.getReturnType();
    }
  }

  /**
   * 这个方法大概就是这个用处，那个地方需要反射去执行，并且要用上type
   * @param target
   * @param args
   * @return
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    try {
      // 反射执行method
      return method.invoke(target, args);
    } catch (IllegalAccessException e) {
      // 如果是私有方法，反射执行会抛出IllegalAccessException，这时候直接
      // 使用setAccessible(true)暴力访问就可以了，牛批
      if (Reflector.canControlMemberAccessible()) {
        method.setAccessible(true);
        return method.invoke(target, args);
      } else {
        throw e;
      }
    }
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
