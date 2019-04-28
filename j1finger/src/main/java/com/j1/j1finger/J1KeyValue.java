package com.j1.j1finger;

/**
 * Created by weijie lv on 2019/4/19.in j1
 *
 * 存储键值对的数据结构。
 */

public class J1KeyValue<K ,V> {

    private K key;
    private V value;

    public J1KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
