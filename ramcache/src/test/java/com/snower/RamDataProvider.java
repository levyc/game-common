package com.snower;

import com.snower.data.IDataProvider;
import java.util.concurrent.ConcurrentHashMap;

public class RamDataProvider implements IDataProvider<String, String> {

  private ConcurrentHashMap<String, String> datas = new ConcurrentHashMap<String, String>();

  public RamDataProvider() {
    datas.put("1", "1");
    datas.put("2", "2");
    datas.put("3", "3");
    datas.put("4", "4");
  }

  @Override
  public String load(String key) {
    return datas.get(key);
  }

  @Override
  public void insert(String key, String value) {
    String s = datas.putIfAbsent(key, value);
    if (s != null) {
      throw new RuntimeException("插入一个已经存在的数据");
    }
  }

  @Override
  public void update(String key, String value) {
    String before = datas.putIfAbsent(key, value);
    if (before == null) {
      throw new RuntimeException("更新一个不存在的数据");
    }
  }

  @Override
  public void delete(String key) {
    datas.remove(key);
  }
}
