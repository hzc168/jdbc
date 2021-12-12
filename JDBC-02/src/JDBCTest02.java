import java.sql.*;

public class JDBCTest02 {
    public static void main(String[] args) throws SQLException {
        Statement stmt = null;
        Connection conn = null;
        try {
            // 1. 注册驱动
            // 注册驱动的第一种写法
            // DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            // 注册驱动的第二种方式：常用的。
            // 为什么这种方式常用？因为参数是一个字符串，字符串可以写到xxx.properties文件中。
            // 以下方法不需要接收返回值，因为我们只想用他的类家在动作
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 获取连接
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/jdbc?useSSL=false", "root", "12345678");
            // 3. 获取数据库操作对象
            stmt = conn.createStatement();
            // 4. 执行SQL语句
//            String sql = "delete from dept where deptno = 40";
            String sql = "update dept set dname = '销售部', loc = '天津' where deptno = 50";
            int count = stmt.executeUpdate(sql);
//            System.out.println(count == 1 ? "删除成功" : "删除失败");
            System.out.println(count == 1 ? "修改成功" : "修改失败");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            if(stmt != null) {
                try {
                    stmt.close();
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
