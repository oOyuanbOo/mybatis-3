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
package org.apache.ibatis.cache.decorators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.io.Resources;

/**
 * @author Clinton Begin
<<<<<<< HEAD
=======
 * 序列化缓存
 * 看看怎么序列化的
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
 */
public class SerializedCache implements Cache {

  private final Cache delegate;

  public SerializedCache(Cache delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
<<<<<<< HEAD
    if (object == null || object instanceof Serializable) {
=======
    // 要存的数据首先是实现了序列化接口
    if (object == null || object instanceof Serializable) {
      // 存的也是序列化之后的字节数组
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      delegate.putObject(key, serialize((Serializable) object));
    } else {
      throw new CacheException("SharedCache failed to make a copy of a non-serializable object: " + object);
    }
  }

<<<<<<< HEAD
=======
  /**
   * get自然就反过来，反序列化
   * @param key The key
   * @return
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  @Override
  public Object getObject(Object key) {
    Object object = delegate.getObject(key);
    return object == null ? null : deserialize((byte[]) object);
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  private byte[] serialize(Serializable value) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bos)) {
<<<<<<< HEAD
      oos.writeObject(value);
      oos.flush();
=======
      // 用字节流读取value中的数据
      oos.writeObject(value);
      // 从缓冲区刷新到流中
      oos.flush();
      // 获取字节数组
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      return bos.toByteArray();
    } catch (Exception e) {
      throw new CacheException("Error serializing object.  Cause: " + e, e);
    }
  }

  private Serializable deserialize(byte[] value) {
    Serializable result;
    try (ByteArrayInputStream bis = new ByteArrayInputStream(value);
<<<<<<< HEAD
=======
         // 用的下面重写的一个CustomObjectInputStream
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
         ObjectInputStream ois = new CustomObjectInputStream(bis)) {
      result = (Serializable) ois.readObject();
    } catch (Exception e) {
      throw new CacheException("Error deserializing object.  Cause: " + e, e);
    }
    return result;
  }

  public static class CustomObjectInputStream extends ObjectInputStream {

    public CustomObjectInputStream(InputStream in) throws IOException {
      super(in);
    }

<<<<<<< HEAD
=======
    // 这里这个重写的方法，会从上下文中加载这个序列化后的类
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws ClassNotFoundException {
      return Resources.classForName(desc.getName());
    }

  }

}
