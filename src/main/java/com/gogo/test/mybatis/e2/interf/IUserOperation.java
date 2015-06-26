package com.gogo.test.mybatis.e2.interf;

import com.gogo.test.mybatis.e1.modal.User;

import java.util.List;

/**
 * Created by 80107436 on 2015-06-03.
 */
public interface IUserOperation {

    public User selectUserByID(int id);

    public List<User> selectUsers(String username);

    public void addUser(User user);

    public void deleteUser(int id);

}