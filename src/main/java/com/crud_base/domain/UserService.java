package com.crud_base.domain;

import com.crud_base.api.UserDto;
import com.crud_base.db.UserEntity;

import java.util.List;

public interface UserService {

    User createUser(UserDto userDto);
    User updateUser(Long id, UserDto userDto);
    User getUserById(Long id);
    void deleteUser(Long id);
    List<User> getAllUsers();

}
