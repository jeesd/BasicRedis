package com.jeesd.redis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

public class RedisClient {
	
	private Jedis jedis;
	
	public RedisClient(String host) {
		this.jedis = new Jedis(host);
	}
	
	public RedisClient(String host, int port) {
		this.jedis = new Jedis(host, port);
	}

	public void testString() {
		String rect = jedis.set("jk1", "jv1");
        System.out.println(rect);
        
        Long rectLong = jedis.append("jk2", "jv2"); //拼接
        System.out.println(rectLong);
        
        rectLong = jedis.del("jk1"); //删除
        System.out.println(rectLong);
        
        //设置多个键值对
        jedis.mset("name1", "password1", "name2", "password2", "name3", "password3", "age", "10");
        //进行加1操作
        jedis.incr("age");
        System.out.println(jedis.get("age"));
	}
	
	public void testHash() {
		Map<String, String> map = new HashMap<String, String>();
        map.put("name", "djoker");
        map.put("age", "12");
        map.put("qq", "123123");
        map.put("address", "xs");
        jedis.hmset("user", map);  //存入redis.使用hmset(mset已经被多个字符串存在占用了)
        
        List<String> rsmap = jedis.hmget("user", "name", "age", "qq", "address"); //取出多个值
        System.out.println(rsmap);
        
        jedis.hdel("user", "age"); //从map中删除一个键值对
        System.out.println(jedis.hmget("user", "age")); //取出map中的age键的值
        System.out.println(jedis.hlen("user")); //返回map的长度
        System.out.println(jedis.exists("user")); //判断map是否存在
        System.out.println(jedis.hkeys("user")); //取出map中所有键的列表
        System.out.println(jedis.hvals("user")); //取出map中所有值得列表
        
        Iterator<String> iter = jedis.hkeys("user").iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            System.out.println("key:" + key + " value:" + jedis.hmget("user", key));
            
            
        }
	}
	
	public void testList() {
		jedis.del("java framework");
        System.out.println(jedis.lrange("java framework", 0, -1));
        //lpush向List左边添加
        jedis.lpush("java framework", "spring");
        jedis.lpush("java framework", "struts");
        jedis.lpush("java framework", "hibernate");
        //lrange取得List的范围 0代表第一位,-1代表最后一位
        System.out.println(jedis.lrange("java framework", 0, -1));
        //rpush向List左边添加
        jedis.del("java framework");
        jedis.rpush("java framework", "spring");
        jedis.rpush("java framework", "struts");
        jedis.rpush("java framework", "hibernate");
        System.out.println(jedis.lrange("java framework", 0, -1));

        SortingParams sortingParameters = new SortingParams();
        jedis.del("num");
        jedis.lpush("num", "1");
        jedis.lpush("num", "5");
        jedis.lpush("num", "2");
        jedis.lpush("num", "10");
        jedis.lpush("num", "8");
        System.out.println(jedis.lrange("num", 0, -1)); //未排序
        System.out.println(jedis.sort("num", sortingParameters.desc())); //排序,但是排序结果不会存入redis服务器
	}
	
	public void testSet() {
		jedis.del("user");
        //添加
        jedis.sadd("user", "1111");
        jedis.sadd("user", "2222");
        jedis.sadd("user", "3333");
        jedis.sadd("user", "4444");
        //删除
        jedis.srem("user", "1111");
        System.out.println(jedis.smembers("user")); //所有的成员
        System.out.println(jedis.sismember("user", "1111")); //判断成员1111是否在user中
        System.out.println(jedis.srandmember("user"));   //随机从user中取出一个成员
        System.out.println(jedis.scard("user")); //返回user中的成员个数
        
	}
	
	public static void main(String[] args) {
		RedisClient rc = new RedisClient("127.0.0.1");
		rc.testString();
		rc.testHash();
		rc.testList();
		rc.testSet();
	}
	
}
