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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;

/**
 * Simple blocking decorator
 *
 * Simple and inefficient version of EhCache's BlockingCache decorator.
 * It sets a lock over a cache key when the element is not found in cache.
 * This way, other threads will wait until this element is filled instead of hitting the database.
 *
 * @author Eduardo Macarron
<<<<<<< HEAD
 *
=======
 * 给Cache加了个阻塞功能
 * 这里的阻塞比较特殊，当线程去获取缓存值时，如果不存在，则会阻塞后续的其他线程去获取该缓存。
 * 为什么这么有这样的设计呢？因为当线程 A 在获取不到缓存值时，一般会去设置对应的缓存值，
 * 这样就避免其他也需要该缓存的线程 B、C 等，重复添加缓存。
 *    BlockingCache 是阻塞版本的缓存装饰器，它保证只有一个线程到数据库中查找指定key对应的数据。
 *    你要是取到空，一般都会去数据库查，这样可以防止缓存击穿，至于你不去存，那么这个key的锁你就拿着呗
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
 */
public class BlockingCache implements Cache {

  private long timeout;
  private final Cache delegate;
<<<<<<< HEAD
=======
  /**
   * 包含一个线程安全的map，里面是对象对应着可重入锁
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  private final ConcurrentHashMap<Object, ReentrantLock> locks;

  public BlockingCache(Cache delegate) {
    this.delegate = delegate;
    this.locks = new ConcurrentHashMap<>();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

<<<<<<< HEAD
=======
  /**
   * 缓存数据的时候不需要先去获取锁
   * 之前getObject的时候取到value为null的线程，尚未释放锁，
   * 此时可能会过来存数据，如果存了数据，就会顺势释放这把锁，这样可以避免其他线程取到null，这个叫做缓存穿透吧
   *    BlockingCache 是阻塞版本的缓存装饰器，它保证只有一个线程到数据库中查找指定key对应的数据。 印证了我的想法
   * @param key Can be any object but usually it is a
   * @param value The result of a select.
   */
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
  @Override
  public void putObject(Object key, Object value) {
    try {
      delegate.putObject(key, value);
    } finally {
      releaseLock(key);
    }
  }

<<<<<<< HEAD
  @Override
  public Object getObject(Object key) {
    acquireLock(key);
    Object value = delegate.getObject(key);
    if (value != null) {
      releaseLock(key);
    }
=======
  /**
   * 获取数据的时候用到了锁
   * @param key The key
   * @return
   */
  @Override
  public Object getObject(Object key) {
    // 多个线程同时获取数据，加锁! 获取这个key对应的锁
    acquireLock(key);
    Object value = delegate.getObject(key);
    // 如果获取到了值，就释放锁
    if (value != null) {
      releaseLock(key);
    }
    // 不然就一直锁着
    // 那么什么时候会释放锁呢，如果一直没有线程来存这个key，是不是就一直释放不了
    // 获得缓存值成功时，会释放锁，这样被阻塞等待的其他线程就可以去获取缓存了。
    // 但是，如果获得缓存值失败时，就需要在 #putObject(Object key, Object value) 方法中，
    // 添加缓存时，才会释放锁，这样被阻塞等待的其它线程就不会重复添加缓存了。
    // TODO: 还是不理解，获取失败，阻塞会自旋不停尝试吗，我只看到返回value，并且value为空，没有释放锁
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    return value;
  }

  @Override
  public Object removeObject(Object key) {
    // despite of its name, this method is called only to release locks
    releaseLock(key);
    return null;
  }

  @Override
  public void clear() {
    delegate.clear();
  }

  private ReentrantLock getLockForKey(Object key) {
<<<<<<< HEAD
=======
    // 又看到了computeIfAbsent，真是个高级的用法
    // 其实就是ConcurrentHashMap的一个方法，里面的实现真的牛批，巨长
    // 意思是，获取key对应的value，不然就给key赋值 k -> new ReentrantLock()
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    return locks.computeIfAbsent(key, k -> new ReentrantLock());
  }

  private void acquireLock(Object key) {
<<<<<<< HEAD
    Lock lock = getLockForKey(key);
    if (timeout > 0) {
      try {
        boolean acquired = lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        if (!acquired) {
          throw new CacheException("Couldn't get a lock in " + timeout + " for the key " +  key + " at the cache " + delegate.getId());
        }
=======
    // 获取锁
    Lock lock = getLockForKey(key);
    // 如果设置了超时时间
    if (timeout > 0) {
      try {
        // 可重入锁的超时加锁方法，避免死锁也可以用到这个方法
        boolean acquired = lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        // 如果没获取到锁
        if (!acquired) {
          throw new CacheException("Couldn't get a lock in " + timeout + " for the key " +  key + " at the cache " + delegate.getId());
        }
        // 阻塞的时候被中断，会抛出InterruptedException
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      } catch (InterruptedException e) {
        throw new CacheException("Got interrupted while trying to acquire lock for key " + key, e);
      }
    } else {
<<<<<<< HEAD
=======
      // 没设置超时时间，直接加锁
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
      lock.lock();
    }
  }

<<<<<<< HEAD
  private void releaseLock(Object key) {
    ReentrantLock lock = locks.get(key);
=======
  /**
   * 所以put里面其实用的是写写并行，都来写没关系，以为ConcurrentHaspMap用的是CAS写入数据
   * 不加锁，你要是值和期望不一样，就会阻塞自旋
   * 读就不同了，你读这个key的值，必须先去获取这个key的锁，读到值就释放锁，但是如果中间来了一个线程要写，会怎么样
   * @param key
   */
  private void releaseLock(Object key) {
    // 释放锁的时候先去获取put的key的锁
    ReentrantLock lock = locks.get(key);
    // 如果锁被当前线程持有，那么就释放
>>>>>>> 5301c684afb0817920e573143b83a7605127b2e0
    if (lock.isHeldByCurrentThread()) {
      lock.unlock();
    }
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
}
