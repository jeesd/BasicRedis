package com.jeesd.redis.lock;

import redis.clients.jedis.Jedis;

/**
 * redis实现分布式锁
 * @author song
 *
 */
public class DistributedLock {
	
	private Jedis jedis;
	
	/**
	 * 锁超时时间，防止线程在入锁以后，无限的执行等待
	 */
	private static final int EXPIRE_TIME = 60 * 1000;
	/**
     * 锁等待时间，防止线程饥饿
     */
	 private static final int RETRY_TIME = 10 * 1000; 
	 /**
	  * 延迟100 毫秒
	  */
	 private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;
	
	/**
     * Lock key path.
     */
	 private static final String LOCK_KEY = "jedis_lock";

	 
    private volatile boolean locked = false;
    
    private long lockValue;
    
	public DistributedLock(String host) {
		this.jedis = new Jedis(host);
	}
	
	/**
	 * 获取锁
	 * @return
	 */
	public synchronized boolean lock() {
		int retryTime = RETRY_TIME;
        try {
        	while(retryTime > 0) {
        		//锁到期时间
        		lockValue = System.currentTimeMillis() + EXPIRE_TIME + 1;
        		String lockValueStr = String.valueOf(lockValue);
        		//
        		//判断能否获取锁
        		if (jedis.setnx(LOCK_KEY, lockValueStr) == 1) {
        			//成功获取锁
        			locked = true;
        			return locked;
        		}
        		//锁被其他线程持有
        		String currLockVal = jedis.get(LOCK_KEY);
        		// 判断锁是否已经失效
        		if (currLockVal != null && Long.valueOf(currLockVal) < System.currentTimeMillis()) {
        			//锁已经失效，使用命令getset设置最新的过期时间
        			String oldLockVal = jedis.getSet(LOCK_KEY, lockValueStr);
        			//判断锁是否已经被抢占
        			if (oldLockVal != null && oldLockVal.equals(currLockVal)) {
        				locked = true;
        				return locked;
        			}
        		}
        		retryTime -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS;
        		//延迟100 毫秒,
			Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
        	}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return false;
	}
	
	/**
	 * 释放锁
	 */
	public synchronized void unlock() {
        if(locked) {
            String currLockVal = jedis.get(LOCK_KEY);
            if(currLockVal != null && Long.valueOf(currLockVal) == lockValue) {
                jedis.del(LOCK_KEY);
                locked = false;
            }
        }
	}
	
	public static void main(String[] args) throws InterruptedException {
		DistributedLock redLock = new DistributedLock("127.0.0.1");
		if(redLock.lock()) {
            System.out.println(Thread.currentThread().getName() + ": 获得锁！");
            Thread.sleep(100000);
            System.out.println(Thread.currentThread().getName() + ": 处理完成！");
            redLock.unlock();
            System.out.println(Thread.currentThread().getName() + ": 释放锁！");
        }else {
            System.out.println("get lock fail!!!");
        }
	}

}
