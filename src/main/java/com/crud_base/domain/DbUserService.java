package com.crud_base.domain;

import com.crud_base.api.UserDto;
import com.crud_base.db.UserEntity;
import com.crud_base.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserService implements UserService {

    private final UserToEntityMapper userToEntityMapper;
    private final UserRepository userRepository;

    public DbUserService(
            UserToEntityMapper userToEntityMapper,
            UserRepository userRepository) {
        this.userToEntityMapper = userToEntityMapper;
        this.userRepository = userRepository;
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

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        userRepository.updateUser(
                id,
                userDto.username(),
                userDto.surname(),
                userDto.email(),
                userDto.age()
        );

        return userToEntityMapper.toDomain(userRepository.findById(id).orElseThrow());
    }

    @Override
    public User getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return userToEntityMapper.toDomain(userEntity);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(userEntity);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userToEntityMapper::toDomain)
                .toList();
    }
}
