package com.crud_base.domain;

import com.crud_base.api.UserDto;
import com.crud_base.api.UserToDtoMapper;
import com.crud_base.db.UserEntity;
import com.crud_base.db.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DbUserService implements UserService {

    private final UserToEntityMapper userToEntityMapper;
    private final UserRepository userRepository;
    private final UserToDtoMapper userToDtoMapper;

    public DbUserService(
            UserToEntityMapper userToEntityMapper,
            UserRepository userRepository,
            UserToDtoMapper userToDtoMapper) {
        this.userToEntityMapper = userToEntityMapper;
        this.userRepository = userRepository;
        this.userToDtoMapper = userToDtoMapper;
    }

    @Override
    public User createUser(UserDto userDto) {

        User userToSave = new User(
                null,
                userDto.username(),
                userDto.surname(),
                userDto.email(),
                userDto.age()
        );
        UserEntity savedUser = userRepository.save(userToEntityMapper.toEntity(userToSave));

        return userToEntityMapper.toDomain(savedUser);
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
