import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 实现功能：
 * 1. 需求：
 *      模拟用户登录功能的实现
 * 2. 业务描述：
 *      程序运行的时候提供一个输入的入口，可以让用户输入用户名和密码
 *      用户输入用户名和密码之后，提交信息，java程序收集到用户信息
 *      Java程序连接数据库验证用户名和密码是否合法
 *      合法：显示登录成功
 *      不合法：显示登录失败
 * 3. 数据的准备：
 *      使用建模工具进行数据库表的设计。
 * 4. 当前程序存在等问题：
 *      用户名：abc
 *      密码：abc' or '1'='1
 *      登录成功
 *      这种现象被称为SQL注入（安全隐患）。
 * 5. 导致sql注入等根本原因是什么？
 *      用户提供等"非法信息"编译到sql中，导致列原sql语句到含义被扭曲。
 * 6. 对比一下Statement和PreparedStatement？
 *  - Statement存在sql注入问题，PreparedStatement解决列SQL注入问题。
 *  - Statement是编译一次执行一次。PreparedStatement是编译一次，可执行M次。PreparedStatement效率较高一些。
 *  - PreparedStatement会在编译阶段做类型到安全检查。
 *  综上：PreparedStatement使用较多。只有极少数到情况下需要使用Statement。
 *  7. 什么情况下必须使用Statement呢？
 *      业务方面要求必须支持SQL注入到时候。
 *      Statement支持SQL注入，凡是业务方面要求是需要进行sql语句拼接到，必须使用Statement。
 */
public class JDBCTest04 {
    public static void main(String[] args) {
        // 初始化一个界面
        Map<String, String> userLoginInfo = initUI();
        // 验证用户名和密码
        boolean loginSuccess = login(userLoginInfo);
        // 最后输出结果
        System.out.println(loginSuccess ? "登录成功" : "登录失败");
    }

    /**
     * 初始化用户界面
     * @return 用户输入的用户名和密码等登录信息
     */
    private static Map<String, String> initUI() {
        Scanner s = new Scanner(System.in);

        System.out.println("用户名：");
        String loginName = s.nextLine();

        System.out.println("密码：");
        String loginPwd = s.nextLine();

        Map<String, String> userLoginInfo = new HashMap<>();
        userLoginInfo.put("loginName", loginName);
        userLoginInfo.put("loginPwd", loginPwd);

        return userLoginInfo;
    }

    /**
     * 用户登录
     * @param userLoginInfo 用户登录信息
     * @return false表示失败，true表示成功
     */
    private static boolean login(Map<String, String> userLoginInfo) {
        // 打标记等意识
        boolean loginSuccess = false;
        // 单独定义变量
        String loginName = userLoginInfo.get("loginName");
        String loginPwd = userLoginInfo.get("loginPwd");

        // JDBC 代码
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // 1. 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 获取链接
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/jdbc?useSSL=false", "root", "12345678");
            // 3. 获取数据库操作对象
            String sql = "select * from t_user where loginName = ? and loginPwd = ?";
            // 程序执行到此处，会发送sql语句框子给DBMS,然后DBMS进行sql语句到预先编译。
            ps = conn.prepareStatement(sql);
            // 给占位符 ? 传值（第一个问号下标是1，第二个问号下标是2，jdbc中所有下标从1开始）
            ps.setString(1, loginName);
            ps.setString(2, loginPwd);
            // 4. 执行sql语句
            rs = ps.executeQuery();
            // 5. 处理结果集
            if(rs.next()) {
                // 登录成功
                loginSuccess = true;
            }
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
        return loginSuccess;
    }
}

