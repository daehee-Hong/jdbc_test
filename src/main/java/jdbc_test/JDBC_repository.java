package jdbc_test;

import connection_pool.ConnectionPool;

import javax.sql.DataSource;
import java.sql.*;

public class JDBC_repository {
    static long beforeTime;

    public void start() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            Runnable myThread = new MyThread(i * 100000);
            new Thread(myThread).start();
            Thread.sleep(1);
        }
        System.out.println("-----[End DB]-----");
    }

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        System.out.println("-----[Start DB]-----");
        beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

        JDBC_repository jdbc_repository = new JDBC_repository();
        jdbc_repository.start();
    }

    public static class MyThread implements Runnable {
        private DataSource dataSource;
        private int startNum;

        MyThread(int startNum){
            this.startNum = startNum;
            this.dataSource = ConnectionPool.getDataSource();
        }

        @Override
        public void run() {
            Connection conn = null;
            PreparedStatement stat = null;
            ResultSet rs = null;
            try {
                conn = dataSource.getConnection();
                conn.setAutoCommit(false);

                System.out.println(startNum);
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
                if (rs != null) {try {rs.close();}catch (SQLException e){}}
                if (stat != null) {try {stat.close();}catch (SQLException e){}}
                if (conn != null) {try {conn.close();}catch (SQLException e){}}
            }
        }
    }
}
