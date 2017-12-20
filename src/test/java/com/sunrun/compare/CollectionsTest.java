package com.sunrun.compare;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Test;

import citic.hz.mpos.service.dao.po.ChannelOrder;

public class CollectionsTest {
	
	@Test
	public void testHashSet(){
		HashSet hs = new HashSet();
		
		ChannelOrder o1 = new ChannelOrder();
		
		o1.setBNO("1");
		ChannelOrder o2 = new ChannelOrder();
		
		o2.setBNO("1");
		hs.add(o1);
		hs.add(o1);
		
		System.out.println(hs.size());
		
	}

	
	@Test
	public void testArrayList(){
		ArrayList al = new ArrayList();
		
		ChannelOrder o1 = new ChannelOrder();
		
		o1.setBNO("1");
		ChannelOrder o2 = new ChannelOrder();
		
		o2.setBNO("1");
		al.add(o1);
		al.add(o1);
		o1.setBNO("2");
		System.out.println(al.size());
		System.out.println(((ChannelOrder)al.get(1)).getBNO());
	}
	
	@Test
	public void testHashSetWithSameValue(){
		HashSet hs = new HashSet();
		
		ChannelOrder o1 = new ChannelOrder();
		
		o1.setMY_ORDER_ID("1");
		o1.setTRAM("1");
		o1.setTRTP("01");
		
		ChannelOrder o2 = new ChannelOrder();
		
		o2.setMY_ORDER_ID("1");
		o2.setTRAM("1");
		o2.setTRTP("01");
		
		hs.add(o1);
		hs.add(o2);
		
		System.out.println(hs.size());
		
	}
	
}
