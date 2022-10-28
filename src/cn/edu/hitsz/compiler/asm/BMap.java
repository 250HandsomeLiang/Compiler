package cn.edu.hitsz.compiler.asm;

import java.util.HashMap;
import java.util.Map;

//需要构造一个双向的映射
//K为变量名,V为寄存器名
public class BMap<K,V> {
    private Map<K,V> KVMap=new HashMap<>();
    private Map<V,K>  VKMap=new HashMap<>();
    public  void putKV(K k,V v){
        removeKV(k,v);
        KVMap.put(k,v);
        VKMap.put(v,k);
    }
    //同时删除k和v
    public void removeKV(K k,V v){
        removeK(k);
        removeV(v);
    }
    //移除变量和变量对应的reg
    public void removeK(K k){
        VKMap.remove(KVMap.remove(k));
    }

    //移除寄存器和寄存器对应的变量
    public void removeV(V v){
        KVMap.remove(VKMap.remove(v));
    }

    //查看某个变量是否存在
    public boolean containsKey(K k){
        return KVMap.containsKey(k);
    }

    //产看某个寄存器是否已经被使用
    public boolean containsValue(V v){
        return VKMap.containsKey(v);
    }

    //获取某个寄存器对应的value
    public K getK(V v){
        return VKMap.get(v);
    }

    //获取某个的变量对应的寄存器
    public V getV(K k){
        return KVMap.get(k);
    }
}
