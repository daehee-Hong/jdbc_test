package jdbc_test;

import java.sql.*;

public class JDBC_repository {

    private String driver;
    private String url;
    private String userName;
    private String password;

    static long beforeTime;

    JDBC_repository(String driver, String url, String userName, String password) {
        this.driver = driver;
        this.url = url;
        this.userName = userName;
        this.password = password;
    }
    public void start() throws ClassNotFoundException, InterruptedException {
        Class.forName(driver);
        for (int i = 1; i <= 5; i++) {
            new MyThread(url, userName, password, i * 100000).run();
            Thread.sleep(1);
        }
        System.out.println("-----[End DB]-----");
    }

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        System.out.println("-----[Start DB]-----");
        beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

        JDBC_repository jdbc_repository = new JDBC_repository(
                "com.mysql.cj.jdbc.Driver"
                ,"jdbc:mysql://@localhost:3306/spring_security_test_db"
                ,"root"
                ,"1234"
        );

        jdbc_repository.start();
    }

    public static class MyThread implements Runnable {

        private String url;
        private String userName;
        private String password;

        private int startNum;

        MyThread(String url, String userName, String password, int startNum){
            this.url = url;
            this.userName = userName;
            this.password = password;
            this.startNum = startNum;
        }

        @Override
        synchronized public void run() {
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url, userName, password);
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            PreparedStatement stat = null;
            ResultSet rs = null;
            try {

                StringBuffer sb = new StringBuffer();
                sb.append("INSERT INTO BOARD(BOARD_ID, TITLE, CONTENT, REGDATE, USER_ID, HIT)VALUE(?,?,?,?,?,?)");
                stat = conn.prepareStatement(sb.toString());

                for (int i = 1; i <= 10000; i++) {
                    stat.setLong(1, startNum + i);
                    stat.setString(2, "테스트");
                    stat.setString(3, "테스트내용");
                    stat.setString(4, "20220529232937");
                    stat.setString(5, "admin" + (startNum + i));
                    stat.setInt(6, 0);

                    stat.addBatch();
                    stat.clearParameters();
                }

                int count = stat.executeBatch().length;
                conn.commit();
                System.out.println(count);

                long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
                long secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
                System.out.println("시간차이(m) : "+secDiffTime);

            } catch (SQLException e){
                e.printStackTrace();
            }finally {
                if (conn != null) {try {conn.close();}catch (SQLException e){}}
                if (stat != null) {try {stat.close();}catch (SQLException e){}}
                if (rs != null) {try {rs.close();}catch (SQLException e){}}
            }
        }
    }
}
