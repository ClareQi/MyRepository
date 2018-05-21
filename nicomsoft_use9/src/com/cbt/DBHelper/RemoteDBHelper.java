package com.cbt.DBHelper;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

/**   
 * @Title: RemoteDBHelper.java 
 * @Description: 远程数据库连接池
 * @Company : www.importExpress.com
 * @author qiyue
 * @date 2016年3月29日
 * @version V1.0   
 */
public class RemoteDBHelper {
	
	
	private static final Log LOG = LogFactory.getLog(RemoteDBHelper.class);
	
	public static ComboPooledDataSource pool = null;

	/**
	 * 初始化
	 */
	public static void init() {
		try {
			long  st = new Date().getTime();
			destory();
			synchronized (RemoteDBHelper.class) {
				//读取配置文件
				InputStream ins = RemoteDBHelper.class.getResourceAsStream("../../../jdbc.properties");
				Properties p = new Properties();
				try {
					p.load(ins);//加载配置文件
				} catch (Exception e) {
					e.printStackTrace();
				}
				pool = new ComboPooledDataSource();
				pool.setDriverClass(p.getProperty("jdbc.driver")); 
				pool.setJdbcUrl(p.getProperty("remote.jdbc.url"));
				pool.setUser(p.getProperty("remote.jdbc.username"));
				pool.setPassword(p.getProperty("remote.jdbc.password"));
				pool.setMaxIdleTime(300);
				pool.setIdleConnectionTestPeriod(1800);
				pool.setAcquireIncrement(3);
				pool.setMaxPoolSize(3050);
				long  stt = new Date().getTime();
				LOG.info("初始化远程数据库:"+(stt-st));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 连接池销毁
	 */
	public static void destory() {
		try {
			if (pool != null) {
				DataSources.destroy(pool);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取远程数据库连接
	 */
	public static Connection getConnection() {
		try {
			if(pool==null){
				init();
			}
			return pool.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error("无法从数据源获取连接!");
		}
		return null;
	}

	/**
	 * 关闭连接
	 * @param conn
	 */
	public static void returnConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error("连接关闭失败!");
		}
	}
	
}
