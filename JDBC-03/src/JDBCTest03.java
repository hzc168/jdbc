import java.sql.*;
import java.util.ResourceBundle;

public class JDBCTest03 {
    public static void main(String[] args) throws SQLException {

        // 使用资源绑定器绑定属性配置文件
        ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
        String driver = bundle.getString("driver");
        String url = bundle.getString("url");
        String user = bundle.getString("user");
        String password = bundle.getString("password");
        System.out.println("读取到的内容 ===" + driver + url + user + password);
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // 1. 注册驱动
            Class.forName(driver);
            // 2. 获取连接
            conn = DriverManager.getConnection(url, user, password);
            // 3. 获取数据库操作对象
            stmt = conn.createStatement();
            // 4. sql语句
            String sql = "select deptno as a,dname,loc from dept";
            rs = stmt.executeQuery(sql);
            // 5. 处理查询结果集
//            boolean flag = rs.next();
//            if(flag) {
//                // rs.next() 光标指向的行有数据
//                // getString()方法的特点是：不管数据库中的数据类型是什么，都以String都形式取出。
//                String deptno = rs.getString(1);    // JDBC中所有下标从1开始。不是从0开始。
//                String dname = rs.getString(2);
//                String loc = rs.getString(3);
//                System.out.println(deptno + "," + dname + "," + loc);
//            }
            while(rs.next()) {
//                int deptno = rs.getInt(1);
//                String dname = rs.getString(2);
//                String loc = rs.getString(3);
//                System.out.println(deptno + "," + dname + "," + loc);
                // 以列的名字获取
                int deptno = rs.getInt("a");    // 重点注意：列名称不是表中的列名称，是查询结果集的列名称。
                String dname = rs.getString("dname");
                String loc = rs.getString("loc");
                System.out.println(deptno + "," + dname + "," + loc);
            }
        } catch (Exception e) {
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
