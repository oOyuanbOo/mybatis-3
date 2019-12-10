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
package org.apache.ibatis.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.jupiter.api.Test;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

class WeakCacheTest {

  @Test
  void shouldDemonstrateObjectsBeingCollectedAsNeeded() {

    long t1 = System.currentTimeMillis();
    final int N = 30000;
    WeakCache cache = new WeakCache(new PerpetualCache("default"));
    for (int i = 0; i < N; i++) {
      cache.putObject(i, i);
      cache.getObject(i);
      if (cache.getSize() < i + 1) {
        // System.out.println("Cache exceeded with " + (i + 1) + " entries.");
        break;
      }
      if ((i + 1) % 100000 == 0) {
        // Try performing GC.

      }
    }
    System.gc();
    System.out.println(cache.getSize());
    System.out.println(System.currentTimeMillis() - t1);
    cache.clear();
    assertTrue(cache.getSize() < N);
  }

  @Test
  void shouldDemonstrateCopiesAreEqual() {
    Cache cache = new WeakCache(new PerpetualCache("default"));
    cache = new SerializedCache(cache);
    for (int i = 0; i < 1000; i++) {
      cache.putObject(i, i);

//      if (i % 50 == 0) {
//        System.gc();
//      }
      Object value = cache.getObject(i);
      assertTrue(value == null || value.equals(i));
    }
    System.gc();
    System.out.println(cache.getSize());

  }

  @Test
  void testWeakReference() {
//    ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
//    WeakReference<String> wee = new WeakReference<String>("1");
    int SIZE = 500000;
    HashMap<Integer, WeakReference<Integer>> map = new HashMap<>();
    for (int i = 0; i < SIZE; i++) {
      map.put(i, new WeakReference<>(i));
    }
    System.out.println("beforeGC : "  + map.size());
    while (true){
      System.gc();
      if (map.size() <  SIZE) {
        System.out.println("afterGC : "  + map.size());
      }
    }
  }

  @Test
  void shouldRemoveItemOnDemand() {
    WeakCache cache = new WeakCache(new PerpetualCache("default"));
    cache.putObject(0, 0);
    assertNotNull(cache.getObject(0));
    cache.removeObject(0);
    assertNull(cache.getObject(0));
  }

  @Test
  void shouldFlushAllItemsOnDemand() {
    WeakCache cache = new WeakCache(new PerpetualCache("default"));
    for (int i = 0; i < 5; i++) {
      cache.putObject(i, i);
    }
    assertNotNull(cache.getObject(0));
    assertNotNull(cache.getObject(4));
    cache.clear();
    assertNull(cache.getObject(0));
    assertNull(cache.getObject(4));
  }

}
