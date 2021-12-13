import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JDBC事务机制：
 *  1. JDBC中事务是自动提交到，什么是自动提交？
 *      只要执行任意一条DML语句，则自动提交一次。这是JDBC默认到事务行为。
 *      但是在实际到业务当中，通常都是N条DML语句共同联合才能完成的，
 *      必须保证他们这些DML语句在同一个事务中同时成功或者同时失败。
 *
 *  2. 以下程序先来验证以下JDBC的事务是否是自动提交机制！
 */
public class JDBCTest06 {
    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            // 1. 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 获取链接
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/jdbc?useSSL=false", "root", "12345678");
            // 将自动提交机制修改为手动提交
            conn.setAutoCommit(false);  // 开启事务

            // 3. 获取数据库操作对象
            String sql = "update t_act set balance = ? where actno = ?";
            ps = conn.prepareStatement(sql);

            // 给 ? 传值
            ps.setDouble(1, 20000);
            ps.setInt(2, 1);
            int count = ps.executeUpdate();

            String s = null;
            s.toString();

            // 给 ? 传值
            ps.setDouble(1, 10000);
            ps.setInt(2, 2);
            count += ps.executeUpdate();

            System.out.println(count == 2 ? "转账成功" : "转账失败");

            // 程序能够走到这里说明以上程序没有异常，事务结束，手动提交数据
            conn.commit();  // 提交事务

        } catch (Exception e) {
            // 回滚事务
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
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
