package com.jeesd.redis.spring;

import redis.clients.jedis.Jedis;

/**获取/释放连接
 * jedi
 * @author song
 *
 */
public interface JedisDataSource {
	
	/**
	 * 从连接池获取jedis连接
	 * @return
	 */
	Jedis getRedisClient(); 
	/**
	 * 返回连接到连接池
	 * @param jedis
	 */
	void returnResource(Jedis jedis);

}
