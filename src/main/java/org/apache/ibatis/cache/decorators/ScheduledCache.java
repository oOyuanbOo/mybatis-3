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

import org.apache.ibatis.cache.Cache;

/**
 * @author Clinton Begin
<<<<<<< HEAD
=======
 * 牛批   定时器Cache，看看集成什么数据结构能玩出这种花来
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
 */
public class ScheduledCache implements Cache {

  private final Cache delegate;
<<<<<<< HEAD
  protected long clearInterval;
=======
  /**
   *
   */
  protected long clearInterval;
  /**
   *
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  protected long lastClear;

  public ScheduledCache(Cache delegate) {
    this.delegate = delegate;
<<<<<<< HEAD
    this.clearInterval = 60 * 60 * 1000; // 1 hour
=======
    // 初始化一个小时
    this.clearInterval = 60 * 60 * 1000; // 1 hour
    // 上一次清除标志  当前毫秒数
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    this.lastClear = System.currentTimeMillis();
  }

  public void setClearInterval(long clearInterval) {
    this.clearInterval = clearInterval;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

<<<<<<< HEAD
=======
  /**
   * 获取数量
   * @return
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  @Override
  public int getSize() {
    clearWhenStale();
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
    clearWhenStale();
    delegate.putObject(key, object);
  }

  @Override
  public Object getObject(Object key) {
    return clearWhenStale() ? null : delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
<<<<<<< HEAD
=======
    // 每个操作之前都会先触发清理任务
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    clearWhenStale();
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
<<<<<<< HEAD
    lastClear = System.currentTimeMillis();
=======
    // 重置清理时间
    lastClear = System.currentTimeMillis();
    // 清除所有数据
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
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

  private boolean clearWhenStale() {
<<<<<<< HEAD
    if (System.currentTimeMillis() - lastClear > clearInterval) {
=======
    // 如果当前时间据上一次清理时间已经超过了指定时长clearInterval
    if (System.currentTimeMillis() - lastClear > clearInterval) {
      // 那么就清理，返回true
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      clear();
      return true;
    }
    return false;
  }

}
