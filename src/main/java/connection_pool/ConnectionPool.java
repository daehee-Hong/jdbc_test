package connection_pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class ConnectionPool {

    private static HikariDataSource dataSource;

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://@localhost:3306/spring_security_test_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    public static DataSource getDataSource(){
        if (dataSource == null){
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDriverClassName(DRIVER);
            hikariConfig.setJdbcUrl(JDBC_URL);
            hikariConfig.setUsername(USERNAME);
            hikariConfig.setPassword(PASSWORD);
            hikariConfig.setConnectionTimeout(3000);
            hikariConfig.setMaxLifetime(58000);
            hikariConfig.setMinimumIdle(10);
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setAutoCommit(false);
            dataSource = new HikariDataSource(hikariConfig);
        }
        return dataSource;
    }
}
