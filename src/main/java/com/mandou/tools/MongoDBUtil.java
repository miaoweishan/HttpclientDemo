package com.mandou.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtil {
	private static Logger log = Logger.getLogger(MongoDBUtil.class);
	//服务器IP地址
    private static String ADDR = PropertiesUtil.getValueBykey("mongodb.database.url");
    //端口
    private static int PORT = Integer.valueOf(PropertiesUtil.getValueBykey("mongodb.database.port"));
    private static String username = PropertiesUtil.getValueBykey("mongodb.database.username");
    private static String password = PropertiesUtil.getValueBykey("mongodb.database.password");
    
    
    
    public static void dropDatabase(String databaseName) {
        if (databaseName != null && !"".equals(databaseName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中应该将 MongoDB 服务器地址配置在配置文件中*/
            MongoClient mongoClient = MongoDBUtil.getConnect();

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             *
             * 注意 MongoDatabase 相当于一个 MongoDB 连接，连接可以有多个
             * MongoClient 相当于一个客户端，客户端可以只有一个，也可有多个
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**删除当前所在的数据库
             * 1）即使数据库中有集合，集合中有文档，整个数据库都会删除，show dbs 不会再有
             * 2）如果待删除的数据库实际没有存在，即 show dbs 看不到，也不影响，不抛异常
             *
             * 也可以使用 MongoClient 的 dropDatabase(String dbName) 方法进行删除
             */
            mongoDatabase.drop();

            /**关闭 MongoDB 客户端连接，释放资源*/
            mongoClient.close();
        }
    }

    //需要密码认证方式连接
    public static MongoClient getConnect2(){
        /**MongoClient 是线程安全的，可以在多个线程中共享同一个实例
         * 一个 MongoClient 相当于一个客户端，一个客户端可以有多个连接
         * */
    	MongoClient mongoClient = null;
        try {
            /** new MongoClient 创建客户端的时候，可以传入 MongoClientOptions 客户端配置选项
             * 所以可以将设置全部事先设置好
             */
            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            /**与目标数据库能够建立的最大连接数为50*/
            build.connectionsPerHost(50);

            /**如果当前所有的连接都在使用中，则每个连接上可以有50个线程排队等待*/
            build.threadsAllowedToBlockForConnectionMultiplier(50);

            /**一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为，此处为 2分钟
             * 如果超过 maxWaitTime 都没有获取到连接的话，该线程就会抛出 Exception
             * */
            build.maxWaitTime(1000 * 60 * 2);

            /**设置与数据库建立连接时最长时间为1分钟*/
            build.connectTimeout(1000 * 60 * 1);
            MongoClientOptions mongoClientOptions = build.build();

            /** 将 MongoDB 服务器的 ip 与端口先封装好
             * 连接 MongoDB 服务端地址，实际项目中应该放到配置文件进行配置
             * */
            ServerAddress serverAddress = new ServerAddress(ADDR, PORT);

            /** MongoCredential：表示 MongoDB 凭据、证书
             * createScramSha1Credential(final String userName, final String source, final char[] password)
             *      1）userName：登录的用户名
             *      2）source：用户需要验证的数据库名称，注意账号当时在哪个数据库下创建，则此时就去哪个库下面进行验证，否则即使账号密码正确也无济于事
             *      3）password：用户的密码
             *      4）实际开发中也应该放到配置文件中进行配置
             * 同理还有：
             * createCredential(final String userName, final String database, final char[] password)
             * createScramSha256Credential(final String userName, final String source, final char[] password)
             * createMongoCRCredential(final String userName, final String database, final char[] password)
             * createMongoX509Credential(final String userName)
             * createMongoX509Credential()
             * createPlainCredential(final String userName, final String source, final char[] password)
             * createGSSAPICredential(final String userName)
             * A、如果 MongoDB 服务端未开启安全认证，这里设置的账号密码连接时也不受影响，同样连接成功
             * B、如果 MongoDB 服务端开启了安全认证，但是账号密码是错误的，则此时不会里面抛异常，等到正在 CRUD 时就会抛异常：Exception authenticating
             * C、如下所示，这是事项在 admin 数据库中创建好的 管理员账号 root
             */
            MongoCredential credential = MongoCredential.createCredential(
            		username, "admin", password.toCharArray());
            /** MongoClient(final ServerAddress addr, final MongoCredential credential, final MongoClientOptions options)
             * 1）addr：MongoDB 服务器地址
             * 2）credential：MongoDB 安全认证证书
             * 3）options：MongoDB 客户端配置选项
             */
            mongoClient = new MongoClient(serverAddress, credential, mongoClientOptions);
            log.info("MongoDB 服务端地址：" + serverAddress.getHost() + ":" + serverAddress.getPort());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return mongoClient;
    }
 
    //不通过认证获取连接数据库对象
    public static MongoClient getConnect(){
    	MongoClient mongoClient = null;
    	try {
    		/** new MongoClient 创建客户端的时候，可以传入 MongoClientOptions 客户端配置选项
             * 所以可以将设置全部事先设置好
             */
            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            /**与目标数据库能够建立的最大连接数为50*/
            build.connectionsPerHost(50);

            /**如果当前所有的连接都在使用中，则每个连接上可以有50个线程排队等待*/
            build.threadsAllowedToBlockForConnectionMultiplier(50);

            /**一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为，此处为 2分钟
             * 如果超过 maxWaitTime 都没有获取到连接的话，该线程就会抛出 Exception
             * */
            build.maxWaitTime(1000 * 60 * 2);

            /**设置与数据库建立连接时最长时间为1分钟*/
            build.connectTimeout(1000 * 60 * 1);
            MongoClientOptions mongoClientOptions = build.build();
            
            List<ServerAddress> adds = new ArrayList<ServerAddress>();
            //ServerAddress()两个参数分别为 服务器地址 和 端口
            ServerAddress serverAddress = new ServerAddress(ADDR, PORT);
            adds.add(serverAddress);
            
            /**
             * 通过 ServerAddress 与 MongoClientOptions 创建连接到 MongoDB 的数据库实例
             * MongoClient(String host, int port)：
             *      1）host：MongoDB 服务端 IP
             *      2）port：MongoDB 服务端 端口，默认为 27017
             *      3）即使 MongoDB 服务端关闭，此时也不会抛出异常，只有到真正调用方法是才会
             *      4）连接 MongoDB 服务端地址，实际项目中应该放到配置文件进行配置
             * MongoClient(final ServerAddress addr, final MongoClientOptions options)
             * 重载了很多构造方法，这只是其中两个常用的
             *      */
            mongoClient = new MongoClient(adds, mongoClientOptions);

            log.info("MongoDB 服务端地址：" + serverAddress.getHost() + ":" + serverAddress.getPort());
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}

        //返回连接数据库对象
        return mongoClient;
    }
    
    public static void main(String[] args) {
    	MongoDBUtil.getConnect();
    }

}
