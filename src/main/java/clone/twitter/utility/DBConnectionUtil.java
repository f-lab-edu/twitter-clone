package clone.twitter.utility;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

import static clone.twitter.utility.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection() {

        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("haro, get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }


//        Connection con = null;
//
//        String server = "jdbc:mysql://127.0.0.1/mocksns"; // 서버 주소
//        String user_name = "root"; //  접속자 id
//        String password = ""; // 접속자 pw
//
//        // JDBC 드라이버 로드
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            System.err.println("JDBC 드라이버를 로드하는데에 문제 발생" + e.getMessage());
//            e.printStackTrace();
//        }
//
//        // 접속
//        try {
//            con = DriverManager.getConnection(server + "?useSSL=false&allowPublicKeyRetrieval=true", user_name, password);
//            System.out.println("연결 완료!");
//        } catch(SQLException e) {
//            System.err.println("연결 오류" + e.getMessage());
//            e.printStackTrace();
//        }
//
//        // 접속 종료
//        try {
//            if(con != null)
//                con.close();
//        } catch (SQLException e) {}
    }

}
