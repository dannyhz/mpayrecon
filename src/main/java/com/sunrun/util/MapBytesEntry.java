
  
package com.sunrun.util;  

import java.util.HashMap;
import java.util.Map;

/**
 * @Type MapBytesEntry
 * @Desc 
 * @Version V1.0
 */
public class MapBytesEntry {

    private byte[] value;
    public MapBytesEntry(byte[] value){
        this.value=value;
    }
    public int hashCode(){
        if(value==null||value.length==0){
            return 0;
        }
        int hashcode = 0;
        for(byte _b : value){
            hashcode = hashcode*31+_b;
        }
        return hashcode;
    }
    
    public boolean equals(Object obj){
        if(obj==null||value==null
                ||!(obj instanceof MapBytesEntry)){
            return false;
        }
        MapBytesEntry _e = (MapBytesEntry)obj;
        if(_e.value==null||value.length!=_e.value.length){
            return false;
        }
        for(int i=0;i<value.length;i++){
            if(_e.value[i]!=value[i]){
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args){
        byte[] value1 = new byte[8];
        for(int i=0;i<value1.length;i++){
            value1[i]=(byte)(i*10);
        }
        MapBytesEntry e1 = new MapBytesEntry(value1);
        System.out.println("e1.hashCode()："+e1.hashCode());
        byte[] value2 = new byte[8];
        for(int i=0;i<value2.length;i++){
            value2[i]=(byte)(i*10);
        }
        MapBytesEntry e2 = new MapBytesEntry(value2);
        System.out.println("e2.hashCode()："+e2.hashCode());
        System.out.println("e1.equals e2："+e1.equals(e2));
        int count = 100;
        Map<MapBytesEntry,Integer> map = new HashMap<MapBytesEntry,Integer>();
        map.put(e1, count);
        System.out.println("e1 拿到的值："+ count);
        System.out.println("e2 拿到的值："+ map.get(e2));
        
    }
	public byte[] getValue() {
		return value;
	}
	 
    
    
}
  