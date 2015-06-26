package com.gogo.test.mybatis.e2;

import com.gogo.test.mybatis.e1.modal.User;
import com.gogo.test.mybatis.e2.interf.IUserOperation;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;
import java.util.List;

/**
 * Created by 80107436 on 2015-06-03.
 */
public class Test {

    private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader;

    static {
        try {
            reader = Resources.getResourceAsReader("Configuration.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSession() {
        return sqlSessionFactory;
    }

    public void getUserList(String userName) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            IUserOperation userOperation = session.getMapper(IUserOperation.class);
            List<User> users = userOperation.selectUsers(userName);
            for (User user : users) {
                System.out.println(user.getId() + ":" + user.getUserName() + ":" + user.getUserAddress());
            }
        } finally {
            session.close();
        }
    }

    public void selectUserByID() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            IUserOperation userOperation = session.getMapper(IUserOperation.class);
            User user = userOperation.selectUserByID(1);
            System.out.println(user.getUserAddress());
            System.out.println(user.getUserName());
        } finally {
            session.close();
        }
    }

    public User addUser() {
        User user = new User();
        user.setUserAddress("人民广场");
        user.setUserName("飞鸟");
        user.setUserAge(80);
        SqlSession session = sqlSessionFactory.openSession();
        try {
            IUserOperation userOperation = session.getMapper(IUserOperation.class);
            userOperation.addUser(user);
            session.commit();
            System.out.println("当前增加的用户 id为:" + user.getId());
        } finally {
            session.close();
        }
        return user;
    }

    public void deleteUser(int id){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            IUserOperation userOperation=session.getMapper(IUserOperation.class);
            userOperation.deleteUser(id);
            session.commit();
        } finally {
            session.close();
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        User user = test.addUser();
        test.deleteUser(user.getId());
        test.selectUserByID();
        test.getUserList("%");
    }

}
