package com.crud_base.domain;

import com.crud_base.api.UserDto;
import com.crud_base.db.UserEntity;
import com.crud_base.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpringAnnotationCachingUserService implements UserService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpringAnnotationCachingUserService.class);
    private final UserToEntityMapper userToEntityMapper;
    private final UserRepository userRepository;

    public SpringAnnotationCachingUserService(
            UserToEntityMapper userToEntityMapper,
            UserRepository userRepository) {
        this.userToEntityMapper = userToEntityMapper;
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserDto userDto) {
        LOGGER.info("Creating User in DB: {}", userDto.username());
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

    @CacheEvict(
            value = "user",
            key = "#id"
    )
    @Override
    public User updateUser(Long id, UserDto userDto) {
        LOGGER.info("Update User in DB: {}", id);
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

    @Cacheable(
            value = "user",
            key = "#id"
    )
    @Override
    public User getUserById(Long id) {
        LOGGER.info("Getting User from DB: {}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return userToEntityMapper.toDomain(userEntity);
    }


    @CacheEvict(
            value = "user",
            key = "#id"
    )
    @Override
    public void deleteUser(Long id) {
        LOGGER.info("Deleting User from DB: {}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(userEntity);
    }

    @Override
    public List<User> getAllUsers() {
        LOGGER.info("Getting All users from DB");
        return userRepository.findAll()
                .stream()
                .map(userToEntityMapper::toDomain)
                .toList();
    }
}
