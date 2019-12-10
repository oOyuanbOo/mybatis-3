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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Deque;
import java.util.LinkedList;

import org.apache.ibatis.cache.Cache;

/**
 * Soft Reference cache decorator
 * Thanks to Dr. Heinz Kabutz for his guidance here.
 *
 * @author Clinton Begin
<<<<<<< HEAD
 *
 * 软引用  就是鸡肋，除非装不下了，会去回收
 */
public class SoftCache implements Cache {
  private final Deque<Object> hardLinksToAvoidGarbageCollection;
  private final ReferenceQueue<Object> queueOfGarbageCollectedEntries;
  private final Cache delegate;
=======
 * 这个应该挺难的，因为比较soft，用到了ReferenceQueue，软引用，虚引用，都是垃圾回收一块的知识
 * GC后一定会回收的是虚引用
 * 忘了
 * “强引用”，我若在，谁（垃圾收集器）都不敢动你，你若动他，我便毁你天堂～
 * “软引用”，就像很贵的人参，吃起来特别难吃，但是你扔了又可惜，如果我身上的东西满的快溢出来的时候，
 *         没办法，我只能扔了你，割肉般的心疼！
 * “弱引用”，就像我每次搬家的时候，发现不太想用的东西，直接扔掉，反正我也不心疼，
 *         但是一般没搬家的时候，我就放在那里也不动他，反正放在那里也不碍事
 * “虚引用”，这个东西就像名字所说虚无缥缈，就像是我一点都不心疼的东西，比如1块钱买的东西，
 *         不管是我扔掉，还是爸妈帮我扔了，我无所谓，只要告诉我一下，你扔他了就行（划重点：被垃圾收集器回收时收到一个系统通知）
 */
public class SoftCache implements Cache {
  /**
   * 强引用避免被GC
   */
  private final Deque<Object> hardLinksToAvoidGarbageCollection;
  /**
   * 准备被GC的引用队列
   */
  private final ReferenceQueue<Object> queueOfGarbageCollectedEntries;
  private final Cache delegate;
  /**
   * 强引用数量
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  private int numberOfHardLinks;

  public SoftCache(Cache delegate) {
    this.delegate = delegate;
<<<<<<< HEAD
=======
    // 默认长度256
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    this.numberOfHardLinks = 256;
    this.hardLinksToAvoidGarbageCollection = new LinkedList<>();
    this.queueOfGarbageCollectedEntries = new ReferenceQueue<>();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
<<<<<<< HEAD
=======
    // 获取长度这里就开始清理了
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    removeGarbageCollectedItems();
    return delegate.getSize();
  }


  public void setSize(int size) {
    this.numberOfHardLinks = size;
  }

  @Override
  public void putObject(Object key, Object value) {
<<<<<<< HEAD
    removeGarbageCollectedItems();
    delegate.putObject(key, new SoftEntry(key, value, queueOfGarbageCollectedEntries));
  }

=======
    // 类似上面
    removeGarbageCollectedItems();
    // 只不过往value里面存的是一个带特异功能的节点
    delegate.putObject(key, new SoftEntry(key, value, queueOfGarbageCollectedEntries));
  }

  /**
   * 看长度这个是重点
   * @param key The key
   * @return
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  @Override
  public Object getObject(Object key) {
    Object result = null;
    @SuppressWarnings("unchecked") // assumed delegate cache is totally managed by this cache
    SoftReference<Object> softReference = (SoftReference<Object>) delegate.getObject(key);
    if (softReference != null) {
      result = softReference.get();
<<<<<<< HEAD
      if (result == null) {
        delegate.removeObject(key);
      } else {
        // See #586 (and #335) modifications need more than a read lock
        synchronized (hardLinksToAvoidGarbageCollection) {
          hardLinksToAvoidGarbageCollection.addFirst(result);
=======
      // 引用在，但是value是null
      if (result == null) {
        delegate.removeObject(key);
      } else {
        //如果value不为null，那么先抢着加锁，把这个key加到强引用里面
        // See #586 (and #335) modifications need more than a read lock
        synchronized (hardLinksToAvoidGarbageCollection) {
          hardLinksToAvoidGarbageCollection.addFirst(result);
          // 如果超过长度，删除最后一个
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
          if (hardLinksToAvoidGarbageCollection.size() > numberOfHardLinks) {
            hardLinksToAvoidGarbageCollection.removeLast();
          }
        }
      }
    }
    return result;
  }

  @Override
  public Object removeObject(Object key) {
    removeGarbageCollectedItems();
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    synchronized (hardLinksToAvoidGarbageCollection) {
      hardLinksToAvoidGarbageCollection.clear();
    }
    removeGarbageCollectedItems();
    delegate.clear();
  }

  private void removeGarbageCollectedItems() {
    SoftEntry sv;
<<<<<<< HEAD
    while ((sv = (SoftEntry) queueOfGarbageCollectedEntries.poll()) != null) {
=======
    // poll 删除并返回，如果没有立刻返回null
    while ((sv = (SoftEntry) queueOfGarbageCollectedEntries.poll()) != null) {
      // 这个只负责听指挥，上头说删你就删
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      delegate.removeObject(sv.key);
    }
  }

  private static class SoftEntry extends SoftReference<Object> {
    private final Object key;

    SoftEntry(Object key, Object value, ReferenceQueue<Object> garbageCollectionQueue) {
      super(value, garbageCollectionQueue);
      this.key = key;
    }
  }

}
