/**
 *    Copyright 2009-2016 the original author or authors.
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * References a generic type.
 *
 * @param <T> the referenced type
 * @since 3.1.0
 * @author Simone Tripodi
 */
public abstract class TypeReference<T> {

<<<<<<< HEAD
  private final Type rawType;

=======
  /**
   * 原始类型
   */
  private final Type rawType;

  /**
   * 构造方法只允许子类调用
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  protected TypeReference() {
    rawType = getSuperclassTypeParameter(getClass());
  }

  Type getSuperclassTypeParameter(Class<?> clazz) {
<<<<<<< HEAD
    Type genericSuperclass = clazz.getGenericSuperclass();
    if (genericSuperclass instanceof Class) {
      // try to climb up the hierarchy until meet something useful
      if (TypeReference.class != genericSuperclass) {
        return getSuperclassTypeParameter(clazz.getSuperclass());
      }

      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
        + "Remove the extension or add a type parameter to it.");
    }

    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    // TODO remove this when Reflector is fixed to return Types
    if (rawType instanceof ParameterizedType) {
=======
    // 获取包含泛型的父类
    Type genericSuperclass = clazz.getGenericSuperclass();
    // 如果父类不包含泛型
    if (genericSuperclass instanceof Class) {
      // try to climb up the hierarchy until meet something useful
      // 如果还有父类
      if (TypeReference.class != genericSuperclass) {
        // 递归调用
        return getSuperclassTypeParameter(clazz.getSuperclass());
      }
      // 这个异常抛的爸比不知所以
      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
        + "Remove the extension or add a type parameter to it.");
    }
    // 获取原始类型
    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    // TODO remove this when Reflector is fixed to return Types
    // 如果原始类型还包含泛型
    if (rawType instanceof ParameterizedType) {
      // 就再取一层，我日   类型真是复杂
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      rawType = ((ParameterizedType) rawType).getRawType();
    }

    return rawType;
  }

  public final Type getRawType() {
    return rawType;
  }

  @Override
  public String toString() {
    return rawType.toString();
  }

}
