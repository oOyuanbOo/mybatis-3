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
package org.apache.ibatis.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
=======
import java.util.Collection;
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0

import org.apache.ibatis.reflection.invoker.GetFieldInvoker;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.reflection.invoker.MethodInvoker;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * @author Clinton Begin
<<<<<<< HEAD
 * 前面看的都是铺垫，这里该是那些工具类来个华山论剑的地方了
 * 类的元数据，对类的增强，与MetaObject相对应
 */
public class MetaClass {

  /**
   * 两个工具类摆在这里
   */
=======
 */
public class MetaClass {

>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  private final ReflectorFactory reflectorFactory;
  private final Reflector reflector;

  private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
    this.reflectorFactory = reflectorFactory;
    this.reflector = reflectorFactory.findForClass(type);
  }

<<<<<<< HEAD
  /**
   * 给这个类整个工具类MetaClass，相当于包装了一层外置骨骼
   * @param type
   * @param reflectorFactory
   * @return
   */
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
    return new MetaClass(type, reflectorFactory);
  }

<<<<<<< HEAD
  /**
   * 属性也可以单独包装
   * @param name
   * @return
   */
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  public MetaClass metaClassForProperty(String name) {
    Class<?> propType = reflector.getGetterType(name);
    return MetaClass.forClass(propType, reflectorFactory);
  }

<<<<<<< HEAD
  /**
   * 不知道有什么作用，先不管，反正就是从name中获取属性
   * @param name
   * @return
   */
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  public String findProperty(String name) {
    StringBuilder prop = buildProperty(name, new StringBuilder());
    return prop.length() > 0 ? prop.toString() : null;
  }

<<<<<<< HEAD
  /**
   * 这个是从驼峰式的命名字符串中获取属性
   * @param name
   * @param useCamelCaseMapping
   * @return
   */
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  public String findProperty(String name, boolean useCamelCaseMapping) {
    if (useCamelCaseMapping) {
      name = name.replace("_", "");
    }
    return findProperty(name);
  }

<<<<<<< HEAD
  /**
   * 获取所有的getter方法
   * @return
   */
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  public String[] getGetterNames() {
    return reflector.getGetablePropertyNames();
  }

<<<<<<< HEAD
  /**
   * 获取所有的setter方法
   * @return
   */
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  public String[] getSetterNames() {
    return reflector.getSetablePropertyNames();
  }

  public Class<?> getSetterType(String name) {
<<<<<<< HEAD
    // 每个属性都要加工成这个吊样，芋道上说是进行分词
=======
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaClass metaProp = metaClassForProperty(prop.getName());
      return metaProp.getSetterType(prop.getChildren());
    } else {
      return reflector.getSetterType(prop.getName());
    }
  }

<<<<<<< HEAD
  /**
   * 获取getter的返回类型
   * @param name
   * @return
   */
  public Class<?> getGetterType(String name) {
    // 分词，这个类就是个遍历的属性类，类似HashMap中的Node有个next，实现链表
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      // 获取属性
=======
  public Class<?> getGetterType(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      MetaClass metaProp = metaClassForProperty(prop);
      return metaProp.getGetterType(prop.getChildren());
    }
    // issue #506. Resolve the type inside a Collection Object
    return getGetterType(prop);
  }

  private MetaClass metaClassForProperty(PropertyTokenizer prop) {
    Class<?> propType = getGetterType(prop);
    return MetaClass.forClass(propType, reflectorFactory);
  }

  private Class<?> getGetterType(PropertyTokenizer prop) {
<<<<<<< HEAD
    Class<?> type = reflector.getGetterType(prop.getName());
    if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
      Type returnType = getGenericGetterType(prop.getName());
      if (returnType instanceof ParameterizedType) {
        Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
        // 返回类型的泛型参数只有一个？ 比如List<String>这种，而不是Map<String, Object>
        // 这个你是如何得出的呢，找找public方法，看看测试怎么处理
=======
    // 获取属性get方法的返回类型，map真的是个很有用的数据结构，相当于一个缓存，放在类里面，随用随取
    Class<?> type = reflector.getGetterType(prop.getName());
    // 如果下标不为空，并且是集合类的儿子，那么能断定这是个集合类的属性
    if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
      // 获取返回类型
      Type returnType = getGenericGetterType(prop.getName());
      // 如果是泛型
      if (returnType instanceof ParameterizedType) {
        // 这个方法是获取真实类型的？ getActualTypeArguments
        // 因为是获取的泛型，所以返回的应该是Collection<?>，只有一个
        // <?>这个在五大类型中是类型变量(TypeVariable)
        Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
        // TODO：说实话，不打断点，我都不知道actualTypeArguments是个啥
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
        if (actualTypeArguments != null && actualTypeArguments.length == 1) {
          returnType = actualTypeArguments[0];
          if (returnType instanceof Class) {
            type = (Class<?>) returnType;
          } else if (returnType instanceof ParameterizedType) {
            type = (Class<?>) ((ParameterizedType) returnType).getRawType();
          }
        }
      }
    }
    return type;
  }

  private Type getGenericGetterType(String propertyName) {
    try {
<<<<<<< HEAD
      Invoker invoker = reflector.getGetInvoker(propertyName);
      if (invoker instanceof MethodInvoker) {
        Field _method = MethodInvoker.class.getDeclaredField("method");
        _method.setAccessible(true);
        Method method = (Method) _method.get(invoker);
        return TypeParameterResolver.resolveReturnType(method, reflector.getType());
      } else if (invoker instanceof GetFieldInvoker) {
=======
      // 获取泛型的时候用到了getInvoker，里面貌似就一个invoke方法，以及一个getType
      Invoker invoker = reflector.getGetInvoker(propertyName);
      // 如果是MethodInvoker的实现
      if (invoker instanceof MethodInvoker) {
        // 这里把之前封装的method作为属性拿了出来
        Field _method = MethodInvoker.class.getDeclaredField("method");
        _method.setAccessible(true);
        // 然后如何把这个method的Field转变成method，容爹再看看
        // Field这个get(obj) 方法是从obj中获取值，这样就把这个属性的getter方法取了出来
        Method method = (Method) _method.get(invoker);
        // TODO：这里用到了TypeParameterResolver这个类，获取到返回类型
        return TypeParameterResolver.resolveReturnType(method, reflector.getType());
        // 这里还有一个GetFieldInvoker  区分这两种类型是在Reflector中
        // Reflector中在addGetField方法里把那些不是$开头的变量 序列号  class这些属性，
        // 应该是和编译后的class文件有关系，这些我也模棱两可，要去test中是一袭
      } else if (invoker instanceof GetFieldInvoker) {
        // 区别是上面获取的是属性，哦~~   难道是那些不需要get的方法，比如直接在属性声明的时候初始化
        // 应该就是！   因为这个Invoker里面有只field成员变量
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
        Field _field = GetFieldInvoker.class.getDeclaredField("field");
        _field.setAccessible(true);
        Field field = (Field) _field.get(invoker);
        return TypeParameterResolver.resolveFieldType(field, reflector.getType());
      }
    } catch (NoSuchFieldException | IllegalAccessException ignored) {
    }
    return null;
  }

  public boolean hasSetter(String name) {
<<<<<<< HEAD
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      if (reflector.hasSetter(prop.getName())) {
        MetaClass metaProp = metaClassForProperty(prop.getName());
=======
    // 拆词都是第一步
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      // 调用的reflector的hasSetter
      if (reflector.hasSetter(prop.getName())) {
        // 把属性包装成MetaClass
        MetaClass metaProp = metaClassForProperty(prop.getName());
        // 然后递归，方法优雅的地方在于prop是封装好的遍历器，所以你可以不停的调自己，直到不满足条件执行其他逻辑
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
        return metaProp.hasSetter(prop.getChildren());
      } else {
        return false;
      }
    } else {
      return reflector.hasSetter(prop.getName());
    }
  }

  public boolean hasGetter(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      if (reflector.hasGetter(prop.getName())) {
        MetaClass metaProp = metaClassForProperty(prop);
        return metaProp.hasGetter(prop.getChildren());
      } else {
        return false;
      }
    } else {
      return reflector.hasGetter(prop.getName());
    }
  }

<<<<<<< HEAD
=======
  // 都是调用reflector的方法，可以这么说MetaClass就是Reflector的又一层外骨骼
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  public Invoker getGetInvoker(String name) {
    return reflector.getGetInvoker(name);
  }

  public Invoker getSetInvoker(String name) {
    return reflector.getSetInvoker(name);
  }

<<<<<<< HEAD
  private StringBuilder buildProperty(String name, StringBuilder builder) {
    // 先包装一层，可以一级一级遍历
    PropertyTokenizer prop = new PropertyTokenizer(name);
=======
  /**
   * 这是将属性名用.连起来
   * name 可能是一串字符  prop1.prop11.prop11
   * 先拆词，在找属性，然后连起来，相当于一个校验，看看这一串属性是不是都在目标类里面
   * @param name
   * @param builder
   * @return
   */
  private StringBuilder buildProperty(String name, StringBuilder builder) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    // 又是递归，
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    if (prop.hasNext()) {
      String propertyName = reflector.findPropertyName(prop.getName());
      if (propertyName != null) {
        builder.append(propertyName);
        builder.append(".");
        MetaClass metaProp = metaClassForProperty(propertyName);
        metaProp.buildProperty(prop.getChildren(), builder);
      }
    } else {
      String propertyName = reflector.findPropertyName(name);
      if (propertyName != null) {
        builder.append(propertyName);
      }
    }
    return builder;
  }

  public boolean hasDefaultConstructor() {
    return reflector.hasDefaultConstructor();
  }

}
