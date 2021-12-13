## jdbc

### 1. JDBC是什么？

Java DataBase Connectivity（Java语言连接数据库）

### 2. JDBC是什么？

**JDBC是SUN公司制定的一套接口（interface）**

​	java.sql.*; (这个软件包下有很多接口)

接口都有调用者和实现者。

面向接口调用、面向接口写实现类，这都属于面向接口编程。

#### 为什么要面向接口编程？

解耦合：降低程序的耦合度，提高程序的扩展力。

多台机制就是非常典型的：瞄向抽象编程。（不要面向具体编程）

建议：

```java
Animal a = new Cat();
Animal a = new Dog();
// 喂养的方法
public void feed(Animal a) {	// 面向父类型编程。
  
}
```

不建议：

```java
Dog d = new Dog();
Cat c = new Cat();
```

#### 为什么SUN制定一套JDBC接口呢？

- 因为每一个数据库的底层实现原理都不一样。
- Oracle数据库有自己的原理。
- MySQL数据库也有自己的原理。
- MS SqlServer数据库也有自己的原理。
- ....
- 每一个数据库产品都有自己独特的实现原理。

#### JDBC的本质到底是什么？

- 一套接口

### 3. 准备工作

先从官网下载对应的驱动jar包，然后将其配置到环境变量classpath当中。

```
classpath=.;D:\course\06-JDBC\resources\MySql Connector Java 5.1.23\mysql-connector-java-5.1.23-bin.jar
```

以上的配置是针对于文本编辑器的方式开发，使用IDEA工具的时候，不需要配置以上的环境变量。
IDEA有自己的配置方式。

### 4. JDBC流程六步

1. 注册驱动（作用：告诉Java程序，即将要连接的是那个品牌的数据库）
2. 获取链接（表示JVM的进程和数据库进程之间的通道打开类，者属于进程之间的通信，重量级的，使用完之后一定要关闭通道。）
3. 获取数据库操作对象（专门执行sql语句的对象）
4. 执行SQL语句（DQL DML....）
5. 处理查询结果集（只有当第四步执行的是select语句的时候，才有这第五步处理查询结果集。）
6. 释放资源（使用完资源之后一定要关闭资源。Java和数据库属于进程间的通信，开启之后一定要关闭。）

### 5. 实现

注意引入mysql驱动。

#### 5.1 数据库中插入数据

```java
import java.sql.*;

public class JDBCTest01 {
    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 1. 注册驱动
            Driver driver = new com.mysql.cj.jdbc.Driver();    // 多态，父类型引用指向子类型对象。
            // Driver driver = new oracle.jdbc.driver.OracleDriver();   // oracle的驱动。
            DriverManager.registerDriver(driver);
            // 2. 获取连接
            String url = "jdbc:mysql://127.0.0.1:3306/jdbc?useSSL=false";
            String user = "root";
            String password = "12345678";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("数据库连接对象 = " + conn);

            // 3. 获取数据库操作对象
            stmt = conn.createStatement();

            // 4. 执行sql
            String sql = "insert into dept(deptno, dname, loc) values(50, '人事部', '北京')";
            // 专门执行DML语句的(insert delete update)
            // 返回值是"影响数据库中的记录条数"
            int count = stmt.executeUpdate(sql);
            System.out.println(count == 1 ? "保存成功" : "保存失败");

            // 5. 处理查询结果
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            // 为了保证资源一定释放，在finally语句块中关闭资源
            // 并且要遵守从小到大依次关闭
            // 分别对其try...catch
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
```

#### 5.2 修改删除数据/另一种注册驱动方式

```java
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
```

#### 5.3 读取配置文件

```java
// 使用资源绑定器绑定属性配置文件
ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
String driver = bundle.getString("driver");
String url = bundle.getString("url");
String user = bundle.getString("user");
String password = bundle.getString("password");
```

#### 5.4 处理查询结果

```java
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
```

### 6. 模拟用户登录功能

**实现功能：**

1. 需求：
		模拟用户登录功能的实现
2. 业务描述：
   - 程序运行的时候提供一个输入的入口，可以让用户输入用户名和密码
   - 用户输入用户名和密码之后，提交信息，java程序收集到用户信息
   - Java程序连接数据库验证用户名和密码是否合法
     - 合法：显示登录成功
     - 不合法：显示登录失败

3. 数据的准备：

  ​	使用建模工具进行数据库表的设计。

4. 当前程序存在等问题：

  ​	用户名：abc

  ​	密码：abc' or '1'='1
  ​	登录成功
  ​	这种现象被称为SQL注入（安全隐患）。

5. 导致sql注入等根本原因是什么？

   ​	用户提供等"非法信息"编译到sql中，导致列原sql语句到含义被扭曲。

6. 对比一下Statement和PreparedStatement？

    - Statement存在sql注入问题，PreparedStatement解决列SQL注入问题。
    - Statement是编译一次执行一次。PreparedStatement是编译一次，可执行M次。PreparedStatement效率较高一些。
    - PreparedStatement会在编译阶段做类型到安全检查。
    
	**综上**：PreparedStatement使用较多。只有极少数到情况下需要使用Statement。

 7. 什么情况下必须使用Statement呢？
    
     - 业务方面要求必须支持SQL注入到时候。
     - Statement支持SQL注入，凡是业务方面要求是需要进行sql语句拼接到，必须使用Statement。

```java
import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // 1. 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. 获取链接
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/jdbc?useSSL=false", "root", "12345678");
            // 3. 获取数据库操作对象
            stmt = conn.createStatement();
            String sql = "select * from t_user where loginName = '" + loginName + "' and loginPwd = '" + loginPwd + "'";
            // 4. 执行sql语句
            rs = stmt.executeQuery(sql);
            // 5. 处理结果集
            if(rs.next()) {
                // 登录成功
                loginSuccess = true;
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
        return loginSuccess;
    }
}
```

由于会导致sql注入问题，所以我们采用占位符方法：

```java
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
```

### 7. 增改删功能

```java
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
```



### 8. 事务回滚

重点三行代码：

```java
conn.setAutoCommit(false);
conn.commit();
conn.rollback();
```

```java
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
```























































