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
package org.apache.ibatis.reflection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.*;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.domain.misc.CustomBeanWrapperFactory;
import java.util.List;
import java.util.Map;


import org.apache.ibatis.domain.misc.RichType;
import org.apache.ibatis.domain.misc.generics.GenericConcrete;
import org.junit.jupiter.api.Test;


import javax.swing.*;
import javax.xml.ws.Holder;

class MetaClassTest {

  @Test
  void shouldTestDataTypeOfGenericMethod() {
    ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
    assertEquals(Long.class, meta.getGetterType("id"));
    assertEquals(Long.class, meta.getSetterType("id"));
  }

  @Test
  void shouldThrowReflectionExceptionGetGetterType() {
    try {
      ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
      MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
      meta.getGetterType("aString");
      org.junit.jupiter.api.Assertions.fail("should have thrown ReflectionException");
    } catch (ReflectionException expected) {
      assertEquals("There is no getter for property named \'aString\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
    }
  }

  @Test
  void shouldCheckGetterExistance() {
    ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
    assertTrue(meta.hasGetter("richField"));
    assertTrue(meta.hasGetter("richProperty"));
    assertTrue(meta.hasGetter("richList"));
    assertTrue(meta.hasGetter("richMap"));
    assertTrue(meta.hasGetter("richList[0]"));

    assertTrue(meta.hasGetter("richType"));
    assertTrue(meta.hasGetter("richType.richField"));
    assertTrue(meta.hasGetter("richType.richProperty"));
    assertTrue(meta.hasGetter("richType.richList"));
    assertTrue(meta.hasGetter("richType.richMap"));
    assertTrue(meta.hasGetter("richType.richList[0]"));

    assertEquals("richType.richProperty", meta.findProperty("richType.richProperty", false));

    assertFalse(meta.hasGetter("[0]"));
  }

  @Test
  void shouldCheckSetterExistance() {
    ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
    assertTrue(meta.hasSetter("richField"));
    assertTrue(meta.hasSetter("richProperty"));
    assertTrue(meta.hasSetter("richList"));
    assertTrue(meta.hasSetter("richMap"));
    assertTrue(meta.hasSetter("richList[0]"));

    assertTrue(meta.hasSetter("richType"));
    assertTrue(meta.hasSetter("richType.richField"));
    assertTrue(meta.hasSetter("richType.richProperty"));
    assertTrue(meta.hasSetter("richType.richList"));
    assertTrue(meta.hasSetter("richType.richMap"));
    assertTrue(meta.hasSetter("richType.richList[0]"));

    assertFalse(meta.hasSetter("[0]"));
  }

  @Test
  void shouldCheckTypeForEachGetter() {
    ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
    assertEquals(String.class, meta.getGetterType("richField"));
//    assertEquals(String.class, meta.getGetterType("richProperty"));
    assertEquals(List.class, meta.getGetterType("richList"));
    assertEquals(Map.class, meta.getGetterType("richMap"));
    assertEquals(List.class, meta.getGetterType("richList[0]"));

    assertEquals(RichType.class, meta.getGetterType("richType"));

    assertEquals(String.class, meta.getGetterType("richType.parametersMap"));
    assertEquals(String.class, meta.getGetterType("richType.richField"));

    assertEquals(String.class, meta.getGetterType("richType.richProperty"));
    assertEquals(List.class, meta.getGetterType("richType.richList"));
    assertEquals(Map.class, meta.getGetterType("richType.richMap"));
    assertEquals(List.class, meta.getGetterType("richType.richList[0]"));
  }

  @Test
  void shouldCheckTypeForEachSetter() {
    ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
    assertEquals(String.class, meta.getSetterType("richField"));
    assertEquals(String.class, meta.getSetterType("richProperty"));
    assertEquals(List.class, meta.getSetterType("richList"));
    assertEquals(Map.class, meta.getSetterType("richMap"));
    assertEquals(List.class, meta.getSetterType("richList[0]"));

    assertEquals(RichType.class, meta.getSetterType("richType"));
    assertEquals(String.class, meta.getSetterType("richType.richField"));
    assertEquals(String.class, meta.getSetterType("richType.richProperty"));
    assertEquals(List.class, meta.getSetterType("richType.richList"));
    assertEquals(Map.class, meta.getSetterType("richType.richMap"));
    assertEquals(List.class, meta.getSetterType("richType.richList[0]"));
  }

  @Test
  void shouldCheckGetterAndSetterNames() {
    ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
    assertEquals(5, meta.getGetterNames().length);
    assertEquals(5, meta.getSetterNames().length);
  }

  @Test
  void shouldFindPropertyName() {
    ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
    assertEquals("richField", meta.findProperty("RICHfield"));
  }


  @Test
  void testParameterizedType() {
    Field[] fields = ParameterizedTypeBean.class.getDeclaredFields();
    // 打印所有的Field的Type是否属于ParameterizedType
    Arrays.stream(fields).filter(f -> f.getGenericType() instanceof ParameterizedType).forEach(f ->{System.out.println(f.getName() +
      " which type is " + f.getGenericType() + " instanceof ParameterizedType " + (f.getGenericType() instanceof ParameterizedType));
      System.out.println(f.getName() + " which actualTypeArguments is " + JSON.toJSONString(((ParameterizedType) f.getGenericType()).getActualTypeArguments()));
      System.out.println(f.getName() + " which rawType is " + ((ParameterizedType)f.getGenericType()).getRawType());
      System.out.println(f.getName() + " which ownerType is " + ((ParameterizedType)f.getGenericType()).getOwnerType());
      System.out.println("====================");
    });


  }

  class ParameterizedTypeBean {
    // 下面的field的Type属于ParameterizedType

    // getOwnerType() 为null
    Map<String, Person> map;
    Set<String> set1;
    Class<?> clz;
    Holder<String> holder;
    List<String> list;
    // getOwnerType() 为Map所属的Type
    Map.Entry<String, String> entry;

    // 下面的field的Type不属于ParameterizedType
    String str;
    Integer i;
    Set set;
    List aList;

    class Holder<V> {

    }

  }

  class Person {

  }


  class TypeVarableBean<K extends InputStream & Serializable, V> {
    // K的上边界是InputStream
    K key;
    // 没有指定的话， V的上边界属于Object
    V value;
    // 不属于TypeVariable
    V[] values;
    String str;
    List<K> klist;
  }

  class KBean extends InputStream implements  Serializable{
    @Override
    public int read() throws IOException {
      return 0;
    }
  }

  @Test
  void testTypeVarableBean () throws NoSuchFieldException {
    TypeVarableBean bean = new TypeVarableBean<KBean, String>();
    Field f = TypeVarableBean.class.getDeclaredField("key");
    TypeVariable gType = (TypeVariable) f.getGenericType();
    System.out.println(gType.getName());
    System.out.println(gType.getGenericDeclaration());
    System.out.println(JSON.toJSONString(gType.getBounds()));
    System.out.println(JSON.toJSONString(gType.getAnnotatedBounds()));
  }


  @Test
  public void test01() {
    RichType object = new RichType();

//    if (true) {
//      object.setRichType(new RichType());
//      object.getRichType().setRichMap(new HashMap());
//      object.getRichType().getRichMap().put("nihao", 111);
//    }

    MetaObject meta = MetaObject.forObject(object, SystemMetaObject.DEFAULT_OBJECT_FACTORY, new CustomBeanWrapperFactory(), new DefaultReflectorFactory());
    Class<?> clazz = meta.getObjectWrapper().getGetterType("richType.richMap.nihao");
    System.out.println(clazz);
  }

}
