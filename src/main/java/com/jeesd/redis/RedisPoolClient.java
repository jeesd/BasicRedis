package com.jeesd.redis;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;

public class RedisPoolClient {
	
	private static String address = "127.0.0.1";
    
    private static int port = 6379;
    
    private static int MAX_IDLE = 200;
    
    private static int MAX_TOTAL = 1024;
    
    private static int MAX_WAIT = 10000;
    
    private static int TIMEOUT = 10000;
    
    private static boolean TEST_ON__BORROW = true;
    
    private static JedisPool jedisPool = null;
    
    static{
        try{
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(MAX_IDLE);
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON__BORROW);
            jedisPool = new JedisPool(config,address, port,TIMEOUT );
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public synchronized static Jedis getJedis() {
    	try {
    		if(null != jedisPool) {
    			Jedis jedis = jedisPool.getResource();
    			return jedis;
    		}
    		return null;
    	} catch(Exception e) {
    		e.printStackTrace();

    		return null;
    	}
    }
    
    @SuppressWarnings("deprecation")
	public static void returnResource(final Jedis jedis){
        if(null != jedis) {
            jedisPool.returnBrokenResource(jedis);
        }
    }
    
	public static void testString(Jedis jedis) {
		String rect = jedis.set("jkp", "jv1");
        System.out.println(rect);
        
        Long rectLong = jedis.append("jkp", "jv2"); //拼接
        System.out.println(rectLong);
        
        rectLong = jedis.del("jkp"); //删除
        System.out.println(rectLong);
        
        //设置多个键值对
        jedis.mset("name1", "password1", "name2", "password2", "name3", "password3", "age", "10");
        //进行加1操作
        jedis.incr("age");
        System.out.println(jedis.get("age"));
	}
	
	public static void testList(Jedis jedis)
	{
	    jedis.flushDB();
	    System.out.println("===========添加一个list===========");
	    jedis.lpush("collections", "ArrayList", "Vector", "Stack", "HashMap", "WeakHashMap", "LinkedHashMap");
	    jedis.lpush("collections", "HashSet");
	    jedis.lpush("collections", "TreeSet");
	    jedis.lpush("collections", "TreeMap");
	    System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));//-1代表倒数第一个元素，-2代表倒数第二个元素
	    System.out.println("collections区间0-3的元素："+jedis.lrange("collections",0,3));
	    System.out.println("===============================");
	    // 删除列表指定的值 ，第二个参数为删除的个数（有重复时），后add进去的值先被删，类似于出栈
	    System.out.println("删除指定元素个数："+jedis.lrem("collections", 2, "HashMap"));
	    System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));
	    System.out.println("删除下表0-3区间之外的元素："+jedis.ltrim("collections", 0, 3));
	    System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));
	    System.out.println("collections列表出栈（左端）："+jedis.lpop("collections"));
	    System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));
	    System.out.println("collections添加元素，从列表右端，与lpush相对应："+jedis.rpush("collections", "EnumMap"));
	    System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));
	    System.out.println("collections列表出栈（右端）："+jedis.rpop("collections"));
	    System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));
	    System.out.println("修改collections指定下标1的内容："+jedis.lset("collections", 1, "LinkedArrayList"));
	    System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));
	    System.out.println("===============================");
	    System.out.println("collections的长度："+jedis.llen("collections"));
	    System.out.println("获取collections下标为2的元素："+jedis.lindex("collections", 2));
	    System.out.println("===============================");
	    jedis.lpush("sortedList", "3","6","2","0","7","4");
	    System.out.println("sortedList排序前："+jedis.lrange("sortedList", 0, -1));
	    System.out.println(jedis.sort("sortedList"));
	    System.out.println("sortedList排序后："+jedis.lrange("sortedList", 0, -1));
	}
	
	public static void testHash(Jedis jedis)
    {
        jedis.flushDB();
        Map<String,String> map = new HashMap<>();
        map.put("key1","value1");
        map.put("key2","value2");
        map.put("key3","value3");
        map.put("key4","value4");
        jedis.hmset("hash",map);
        jedis.hset("hash", "key5", "value5");
        System.out.println("散列hash的所有键值对为："+jedis.hgetAll("hash"));//return Map<String,String>
        System.out.println("散列hash的所有键为："+jedis.hkeys("hash"));//return Set<String>
        System.out.println("散列hash的所有值为："+jedis.hvals("hash"));//return List<String>
        System.out.println("将key6保存的值加上一个整数，如果key6不存在则添加key6："+jedis.hincrBy("hash", "key6", 6));
        System.out.println("散列hash的所有键值对为："+jedis.hgetAll("hash"));
        System.out.println("将key6保存的值加上一个整数，如果key6不存在则添加key6："+jedis.hincrBy("hash", "key6", 3));
        System.out.println("散列hash的所有键值对为："+jedis.hgetAll("hash"));
        System.out.println("删除一个或者多个键值对："+jedis.hdel("hash", "key2"));
        System.out.println("散列hash的所有键值对为："+jedis.hgetAll("hash"));
        System.out.println("散列hash中键值对的个数："+jedis.hlen("hash"));
        System.out.println("判断hash中是否存在key2："+jedis.hexists("hash","key2"));
        System.out.println("判断hash中是否存在key3："+jedis.hexists("hash","key3"));
        System.out.println("获取hash中的值："+jedis.hmget("hash","key3"));
        System.out.println("获取hash中的值："+jedis.hmget("hash","key3","key4"));
    }
	
	public static void testSortedSet(Jedis jedis)
    {
        jedis.flushDB();
        Map<String, Double> map = new HashMap<>();
        map.put("key2",1.2);
        map.put("key3",4.0);
        map.put("key4",5.0);
        map.put("key5",0.2);
        System.out.println(jedis.zadd("zset", 3,"key1"));
        System.out.println(jedis.zadd("zset",map));
        System.out.println("zset中的所有元素："+jedis.zrange("zset", 0, -1));
        System.out.println("zset中的所有元素："+jedis.zrangeWithScores("zset", 0, -1));
        System.out.println("zset中的所有元素："+jedis.zrangeByScore("zset", 0,100));
        System.out.println("zset中的所有元素："+jedis.zrangeByScoreWithScores("zset", 0,100));
        System.out.println("zset中key2的分值："+jedis.zscore("zset", "key2"));
        System.out.println("zset中key2的排名："+jedis.zrank("zset", "key2"));
        System.out.println("删除zset中的元素key3："+jedis.zrem("zset", "key3"));
        System.out.println("zset中的所有元素："+jedis.zrange("zset", 0, -1));
        System.out.println("zset中元素的个数："+jedis.zcard("zset"));
        System.out.println("zset中分值在1-4之间的元素的个数："+jedis.zcount("zset", 1, 4));
        System.out.println("key2的分值加上5："+jedis.zincrby("zset", 5, "key2"));
        System.out.println("key3的分值加上4："+jedis.zincrby("zset", 4, "key3"));
        System.out.println("zset中的所有元素："+jedis.zrange("zset", 0, -1));
    }
    
	public static void testSort(Jedis jedis)
    {
        jedis.flushDB();
        jedis.lpush("collections", "ArrayList", "Vector", "Stack", "HashMap", "WeakHashMap", "LinkedHashMap");
        System.out.println("collections的内容："+jedis.lrange("collections", 0, -1));
        SortingParams sortingParameters = new SortingParams();
        System.out.println(jedis.sort("collections",sortingParameters.alpha()));
        System.out.println("===============================");
        jedis.lpush("sortedList", "3","6","2","0","7","4");
        System.out.println("sortedList排序前："+jedis.lrange("sortedList", 0, -1));
        System.out.println("升序："+jedis.sort("sortedList", sortingParameters.asc()));
        System.out.println("升序："+jedis.sort("sortedList", sortingParameters.desc()));
        System.out.println("===============================");
        jedis.lpush("userlist", "33");  
        jedis.lpush("userlist", "22");  
        jedis.lpush("userlist", "55");  
        jedis.lpush("userlist", "11");  
        jedis.hset("user:66", "name", "66");  
        jedis.hset("user:55", "name", "55");  
        jedis.hset("user:33", "name", "33");  
        jedis.hset("user:22", "name", "79");  
        jedis.hset("user:11", "name", "24");  
        jedis.hset("user:11", "add", "beijing");  
        jedis.hset("user:22", "add", "shanghai");  
        jedis.hset("user:33", "add", "guangzhou");  
        jedis.hset("user:55", "add", "chongqing");  
        jedis.hset("user:66", "add", "xi'an");  
        sortingParameters = new SortingParams();
        sortingParameters.get("user:*->name");  
        sortingParameters.get("user:*->add"); 
        System.out.println(jedis.sort("userlist",sortingParameters));
    }
	
    public static void main(String[] args) {
    	//从连接池中获取Jedis
        Jedis jedis = RedisPoolClient.getJedis();
        
        RedisPoolClient.testString(jedis);
        RedisPoolClient.testList(jedis);
        RedisPoolClient.testSort(jedis);
        RedisPoolClient.testHash(jedis);
        RedisPoolClient.testSortedSet(jedis);
        
        RedisPoolClient.returnResource(jedis);
	}

}
