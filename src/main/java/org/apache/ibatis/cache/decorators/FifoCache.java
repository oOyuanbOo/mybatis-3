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

import java.util.Deque;
import java.util.LinkedList;

import org.apache.ibatis.cache.Cache;

/**
 * FIFO (first in, first out) cache decorator.
 *
 * @author Clinton Begin
 * 先进先出的装饰器
 * 看了下方法的逻辑，其实好多的数据集合都是在简单集合的基础上，结合一个功能性的模块，实现某种复杂的功能
 * 比如这里的有序队列，就给Cache带来了FIFO的功能
 */
public class FifoCache implements Cache {

  private final Cache delegate;
  /**
   * 双向队列
   */
  private final Deque<Object> keyList;
  private int size;

  public FifoCache(Cache delegate) {
    this.delegate = delegate;
    // 这里面初始化了一个有序队列存放Cache里面的key，一一对应
    this.keyList = new LinkedList<>();
    // 大小固定为1024
    this.size = 1024;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public void putObject(Object key, Object value) {
    // 插入这个key到队列里面，这个方法会去判断队列大小，给新的key安排位置
    cycleKeyList(key);
    delegate.putObject(key, value);
  }

  @Override
  public Object getObject(Object key) {
    return delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
    keyList.clear();
  }

  private void cycleKeyList(Object key) {
    // 插入到尾部
    keyList.addLast(key);
    // 判断队列长度是否超过了设置的长度，若是
    if (keyList.size() > size) {
      Object oldestKey = keyList.removeFirst();
      // 并且缓存对应的删除这个key对应的键值对
      delegate.removeObject(oldestKey);
    }
  }

}
