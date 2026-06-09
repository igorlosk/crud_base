package com.crud_base.api;

import com.crud_base.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserToDtoMapper {

    public User toDomain(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.username(),
                userDto.surname(),
                userDto.email(),
                userDto.age()
        );
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.id(),
                user.username(),
                user.surname(),
                user.email(),
                user.age()
        );
    }
}
