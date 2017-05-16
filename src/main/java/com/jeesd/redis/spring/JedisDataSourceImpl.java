package com.jeesd.redis.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Repository
public class JedisDataSourceImpl implements JedisDataSource {
	
	@Autowired
    private JedisPool jedisPool;

	@Override
	public Jedis getRedisClient() {
		Jedis jedis = null;
        try {
        	jedis = jedisPool.getResource();
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
            if (null != jedis)
            	jedis.close();
        }
        return null;
	}

	@Override
	public void returnResource(Jedis jedis) {
		
		jedis.close();
	}


}
