import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCTest05 {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            // 1. 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 获取链接
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/jdbc?useSSL=false", "root", "12345678");
            // 3. 获取数据库操作对象
            // insert
//            String sql = "insert into dept(deptno, dname, loc) values (?, ?, ?)";
//            ps = conn.prepareStatement(sql);
//            ps.setInt(1, 60);
//            ps.setString(2, "销售部");
//            ps.setString(3, "上海");

//            // update
//            String sql = "update dept set dname = ?, loc = ? where deptno = ?";
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, "销售部12");
//            ps.setString(2, "上海12");
//            ps.setInt(3, 60);

            // delete
            String sql = "delete from dept where deptno = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, 60);


            // 4. 执行sql
            int count = ps.executeUpdate();
            System.out.println(count);
            // 5. 处理结果集
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            if(ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
