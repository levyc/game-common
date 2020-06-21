package com.snower;

import com.snower.cache.ICacheProvider;
import com.snower.cache.LRUCacheProvider;
import com.snower.data.IDataProvider;
import org.junit.Assert;
import org.junit.Test;

public class CacheContainerTest {

  ICacheProvider<String, String> cacheProvider = new LRUCacheProvider<String, String>(10, 1);
  IDataProvider<String, String> dataProvider = new RamDataProvider();
  private CacheContainer<String, String> cacheContainer = new CacheContainer<String, String>("levy",
      cacheProvider, dataProvider);


  @Test
  public void testContainerLoad() {
    String value = cacheContainer.load("1");
    System.out.println("testContainerLoad value:" + value);
    Assert.assertTrue("1".equals(value));
  }

  @Test(expected = RuntimeException.class)
  public void testContainerAddExist() {
    cacheContainer.add("1", "1");
  }

  @Test()
  public void testContainerAddNotExist() {
    cacheContainer.add("8", "8");
    String value = cacheContainer.load("8");
    Assert.assertTrue("8".equals(value));
  }

  @Test()
  public void testContainerAsycAddNotExist() {
    cacheContainer.add("asyncAdd", "asyncAdd");
    try {
      Thread.sleep(13000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    String value = cacheContainer.load("asyncAdd");
    Assert.assertTrue("asyncAdd".equals(value));
  }

  @Test(expected = RuntimeException.class)
  public void testContainerUpdateNotExist() {
    cacheContainer.update("9", "9");
  }

  @Test()
  public void testContainerUpdate() {
    cacheContainer.load("3");
    cacheContainer.update("3", "3");
    String value = cacheContainer.load("3");
    Assert.assertTrue("3".equals(value));
  }

  @Test()
  public void testContainerDelete() {
    cacheContainer.delete("1");
    String value = cacheContainer.load("1");
    Assert.assertTrue(value == null);
  }


}
