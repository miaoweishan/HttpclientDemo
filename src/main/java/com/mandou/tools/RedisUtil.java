package com.mandou.tools;
/**
 * redis操作的工具类
 * @author yuanjie
 * 
 * @date 2019年6月12日
 *
 */

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
	private static Logger log = Logger.getLogger(RedisUtil.class);
	//服务器IP地址
    private static String ADDR = PropertiesUtil.getValueBykey("redis.database.url");
    //端口
    private static int PORT = Integer.valueOf(PropertiesUtil.getValueBykey("redis.database.port"));
    //密码
    private static String AUTH = PropertiesUtil.getValueBykey("redis.database.password");
    //连接实例的最大连接数
    private static int MAX_ACTIVE = Integer.valueOf(PropertiesUtil.getValueBykey("redis.database.max_active"));
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = Integer.valueOf(PropertiesUtil.getValueBykey("redis.database.max_idle"));
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
    private static int MAX_WAIT = Integer.valueOf(PropertiesUtil.getValueBykey("redis.database.max_wait"));    
    //连接超时的时间　　
    private static int TIMEOUT = Integer.valueOf(PropertiesUtil.getValueBykey("redis.database.timeout"));
    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = Boolean.valueOf(PropertiesUtil.getValueBykey("redis.database.test_on_borrow"));

    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    static {
        try {
        	log.info("ADDR："+ADDR+" PORT："+PORT+" TIMEOUT："+TIMEOUT+" AUTH："+AUTH);
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }
    
    private static Jedis jedis = RedisUtil.getJedis(); 
    
    /**
     * 添加String数据
     * @param key
     * @param value
     */
    public static void redisStringSet(String key,String value) {  	
    	//添加数据
    	log.info("入参："+key+"  "+value);
		jedis.set(key, value);
        log.info("数据添加成功："+jedis.get(key));
        RedisUtil.redisClose(jedis);
        //设置多个键值对
//        jedis.mset("name","yc","age","22","qq","1933108196");
//        jedis.incr("age");//加1操作
    }
    
    /**
     * 查询String数据
     * @param key
     * 
     */
    public static void redisStringGet(String key) {  	
    	//添加数据
    	log.info("入参："+key);
		jedis.get(key);
        log.info("数据查询成功："+jedis.get(key));
        RedisUtil.redisClose(jedis);
    }
    
    /**
     * 拼接String数据
     * @param key
     * @param value
     */
    public static void redisStringAppend(String key,String value) {  	
    	//添加数据
    	log.info("入参："+key+"  "+value);
    	//拼接字符串
        jedis.append("name", ".com");
        log.info("数据拼接成功："+jedis.get(key));
        RedisUtil.redisClose(jedis);
    }
    
    /**
     * 删除接String,map数据
     * @param key
     * 
     */
    public static void redisStringDel(String key) {  	
    	//添加数据
    	log.info("入参："+key);
    	//删除数据
        jedis.del(key);
        log.info("数据删除成功  key："+key);
        RedisUtil.redisClose(jedis);
    }
    
    /**
     * 添加接map数据
     * @param key
     * @param value
     */
    public static void redisMapSet(String key,Map<String,String> value) {  	
    	//添加数据
    	jedis.hmset(key, value);
    	log.info("数据添加成功");
        RedisUtil.redisClose(jedis);
    }
    
    /**
     * 查询接map数据
     * @param key
     * @param fields
     */
    public static void redisMapGet(String key,String... fields) {  	
    	//添加数据
    	//取出users中的Name,执行结果:[minxr]-->注意结果是一个泛型的List
        //第一个参数是存入redis中map对象的key,后面跟的是放入map中对象的key,后面的key可以是多个，是可变的
        List<String> rsmap = jedis.hmget(key, fields);
    	log.info("数据添加成功： "+rsmap);
        RedisUtil.redisClose(jedis);
    }
    
    /**
     * 添加接map数据
     * @param key
     * @param value
     */
    public static void redisListSet(String key,String... fields) {  	
    	//添加数据
    	jedis.lpush(key, fields);
    	log.info("数据添加成功");
        RedisUtil.redisClose(jedis);
    }
    
    /**
     * redis操作set集合
     * @param key
     * @param members
     */
    public static void redisSetSet(String key, String... members) {
    	jedis.lpush(key, members);
    	
    	//删除
//        jedis.srem("user", "who");
//        System.out.println(jedis.smembers("user"));//获取所有加入的value
//        System.out.println(jedis.sismember("user", "who"));//判断who是否是user集合的元素
//        System.out.println(jedis.srandmember("user"));
//        System.out.println(jedis.scard("user"));//返回集合的元素个数   	
    }
    
    /**
     * redis排序
     */  
//    public void testSort() {
//        
//        //jedis 排序
//        //注意，此处的rpush和lpush是List的操作。是一个双向链表（但从表现来看的)
//        jedis.del("a");//先清除数据，再加入数据进行测试
//        jedis.rpush("a", "1");
//        jedis.lpush("a", "6");
//        jedis.lpush("a", "3");
//        jedis.lpush("a", "9");
//        System.out.println(jedis.lrange("a", 0, -1));
//        System.out.println(jedis.sort("a"));//[1,3,6,9] //输入排序后结果
//        System.out.println(jedis.lrange("a", 0, -1));
//        
//    }

    /**
     * 获取Jedis实例
     * 在java关键字synchronized就具有使每个线程依次排队操作共享变量的功能
     */
    public synchronized static Jedis getJedis() {

        try {

            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /***
     * 
     * 释放资源
     */   
    public static void redisClose(final Jedis jedis) {
        if(jedis != null) {
            jedisPool.close();
        }
        
    }
    
    public static void main(String[] args) {
    	RedisUtil.redisListSet("java_framework","spring","struts","hibernate");   	
    }

}
