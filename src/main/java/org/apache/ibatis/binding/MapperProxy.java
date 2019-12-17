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
package org.apache.ibatis.binding;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */


/**
 * sqlSession  mapperInterface   methodCache都是干哈的，有啥作用，啥时候初始化的，了解下，下面的方法用到的挺多
 * 读源码和你平时看业务代码不同，mvc就三层，这个N层，注意抓重点
 * @param <T>
 *     Mapper接口的代理类，这里是解密mapper接口为什么没有实现的关键
 */

public class MapperProxy<T> implements InvocationHandler, Serializable {

  private static final long serialVersionUID = -6424540398559729838L;
  /**
   * 执行sql的会话
   */
  private final SqlSession sqlSession;
  /**
   * 代理类需要实现的接口吧
   */
  private final Class<T> mapperInterface;
  /**
   * 这个缓存是方法级的，就是说在动态代理生成字节码也就是编译后，装载到jvm，然后代理类被调用的时候，初始化的
   */
  private final Map<Method, MapperMethod> methodCache;

  public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
    this.sqlSession = sqlSession;
    this.mapperInterface = mapperInterface;
    this.methodCache = methodCache;
  }

  /**
   * mybatis接口的执行过程
   * @param proxy
   * @param method
   * @param args
   * @return
   * @throws Throwable
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      // 如果方法所在类是Object.class 则直接执行代理方法
      // 上面是我猜的，判断方法是否继承自Object，如toString、equals等方法
      if (Object.class.equals(method.getDeclaringClass())) {
        return method.invoke(this, args);
      } else if (method.isDefault()) {
        return invokeDefaultMethod(proxy, method, args);
      }
    } catch (Throwable t) {
      throw ExceptionUtil.unwrapThrowable(t);
    }

    // 1.mapper里面的方法进入到代理类里
    //   1.1 先缓存方法
    final MapperMethod mapperMethod = cachedMapperMethod(method);
    //   1.2 执行方法
    //   上面的方法里我找了五层才找到其中用到的参数mapperInterface，现在我要去找sqlSession的出处
    //   new SqlSessionFactoryBuilder(配置文件).build会创造SqlSessionFactory，它再openSession，这个类就出来了

    return mapperMethod.execute(sqlSession, args);
  }

  private MapperMethod cachedMapperMethod(Method method) {

    // 1.1 短短的一行，因为用了函数式编程，其实内容很多，读起来比较操蛋，第二个参数和我想的一样
    // 这里创建了MapperMethod类，并把类的成员变量mapperInterface，configuration传到了类中
    // 学习这些类，首先弄明白它的这些成员变量都是些什么玩意，为什么要把它们弄到这个类里，打算怎么用
    // 麻痹的 ，这里的第二个参数Function<? super K, ? extends V>，我以为K V是两个参数，一直看不明白，搞半天K是参数，V是返回类型

    // mapperInterface 是从XMLConfigBuilder的parse方法读取配置文件中的接口class属性 下面xml中的3，
    // 一路沿着XmlConfigBuild -> Configuration -> MapperRegistry -> MapperProxyFactory ->MapperProxy
    // 5层，mvc我基本两层最多了

//  <mappers>
//    <mapper resource="org/apache/ibatis/builder/BlogMapper.xml"/>
//    <mapper url="file:./src/test/java/org/apache/ibatis/builder/NestedBlogMapper.xml"/>
//    <mapper class="org.apache.ibatis.builder.CachedAuthorMapper"/>
//    <package name="org.apache.ibatis.builder.mapper"/>
//  </mappers>
    // 我一般是用的第一种

    return methodCache.computeIfAbsent(method, k -> new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()));
  }

  /**
   * default
   * @param proxy
   * @param method
   * @param args
   * @return
   * @throws Throwable
   * java8 通过反射执行接口的default方法
   */
  private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
      throws Throwable {
    final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
        .getDeclaredConstructor(Class.class, int.class);
    if (!constructor.isAccessible()) {
      constructor.setAccessible(true);
    }
    final Class<?> declaringClass = method.getDeclaringClass();
    return constructor
        .newInstance(declaringClass,
            MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
                | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC)
        .unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
  }
}
