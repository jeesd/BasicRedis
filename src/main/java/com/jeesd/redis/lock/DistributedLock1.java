package com.jeesd.redis.lock;

import redis.clients.jedis.Jedis;

/**
 * 失效方式分布式锁
 * @author song
 *
 */
public class DistributedLock1 {

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
    
	public DistributedLock1(String host) {
		this.jedis = new Jedis(host);
	}
	
	public synchronized boolean lock(){
        int retryTime = RETRY_TIME;
        try {
            while (retryTime > 0) {
                lockValue = System.nanoTime();
                if ("OK".equalsIgnoreCase(jedis.set(LOCK_KEY, String.valueOf(lockValue), "NX", "PX", EXPIRE_TIME))) {
                    locked = true;
                    return locked;
                }
                retryTime -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS;
                Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void unlock(){
        if(locked) {
            String currLockVal = jedis.get(LOCK_KEY);
            if(currLockVal!=null && Long.valueOf(currLockVal) == lockValue){
                jedis.del(LOCK_KEY);
                locked = false;
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
		DistributedLock1 redLock = new DistributedLock1("127.0.0.1");
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
