package com.crud_base.domain;

import com.crud_base.api.UserDto;
import com.crud_base.db.UserEntity;

public interface UserService {

    User createUser(UserDto userDto);
    User updateUser(Long id, UserDto userDto);
    User getUserById(Long id);
    void deleteUser(Long id);

}
