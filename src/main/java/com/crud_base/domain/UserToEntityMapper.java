package com.crud_base.domain;

import com.crud_base.db.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserToEntityMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.username(),
                user.surname(),
                user.email(),
                user.age()
        );
    }

    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getSurname(),
                userEntity.getEmail(),
                userEntity.getAge()
        );
    }
}
