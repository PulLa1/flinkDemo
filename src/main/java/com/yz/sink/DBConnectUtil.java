package com.yz.sink;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * 数据库连接工具类
 */
public class DBConnectUtil {

    private static final Logger log = LoggerFactory.getLogger(DBConnectUtil.class);

    /**
     * 默认的fieldSerializer 和Connection.class 兼容有问题
     * 开启references 被序列化成 byte[] 解决这个问题但是会影响性能
     * @return
     */
    public static KryoSerializer<Connection> ConnectionSerialize(){
        KryoSerializer<Connection> kryoSerializer = new KryoSerializer<>(Connection.class, new ExecutionConfig());
        kryoSerializer.getKryo().setReferences(true);
        return kryoSerializer;
    }




    /**
     * 获取连接
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection(boolean isAutoCommit) throws SQLException {
        String url = "jdbc:mysql://192.168.8.100:3306/db_enaiter?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&autoReconnect=true";
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.error("获取mysql.jdbc.Driver失败");
            e.printStackTrace();
        }
        try {
            Properties prop = new Properties();
            prop.setProperty("driverClassName", "com.mysql.jdbc.Driver");
            prop.setProperty("user", "root");
            prop.setProperty("password", "mysql-root-pwd");
            prop.setProperty("initialSize", "5");
            prop.setProperty("minIdle", "5");
            prop.setProperty("maxActive", "10");
            prop.setProperty("maxWait", "60000");
            prop.setProperty("timeBetweenEvictionRunsMillis", "60000");
            prop.setProperty("minEvictableIdleTimeMillis", "300000");
            prop.setProperty("validationQuery", "SELECT 'x'");
            prop.setProperty("testWhileIdle", "true");
            prop.setProperty("testOnBorrow", "false");
            prop.setProperty("testOnReturn", "false");
            prop.setProperty("poolPreparedStatements", "true");
            prop.setProperty("maxPoolPreparedStatementPerConnectionSize", "20");
            conn = DriverManager.getConnection(url,prop);
            log.info("获取连接成功...");
        } catch (Exception e) {
            log.error("获取连接失败，url:" + url );
        }
        //设置手动提交
        conn.setAutoCommit(isAutoCommit);
        return conn;
    }


    public static void main(String[] args) throws SQLException {
        Connection connection = DBConnectUtil.getConnection(false);
        for (int i = 0; i < 3; i++) {
            String sql = "insert into `t_test` (`value`,`total`,`insert_time`) values (?,?,?)";
            log.info("====执行SQL:{}===", sql);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "key");
            ps.setInt(2, 20);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            //执行insert语句
            ps.execute();
            ps.close();
        }
        connection.commit();
    }
}