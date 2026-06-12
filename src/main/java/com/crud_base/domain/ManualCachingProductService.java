package com.crud_base.domain;

import com.crud_base.api.UserDto;
import com.crud_base.db.UserEntity;
import com.crud_base.db.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ManualCachingProductService implements UserService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ManualCachingProductService.class);
    private final UserToEntityMapper userToEntityMapper;
    private final UserRepository userRepository;
    private final RedisTemplate<String, UserEntity> redisTemplate;
    private final ObjectMapper objectMapper;

    private final static String CACHE_KEY_PREFIX = "user:";
    private final static long CACHE_TTL_MINUTES = 1;

    public ManualCachingProductService(
            UserToEntityMapper userToEntityMapper,
            UserRepository userRepository,
            RedisTemplate<String, UserEntity> redisTemplate,
            ObjectMapper objectMapper) {
        this.userToEntityMapper = userToEntityMapper;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;

        this.objectMapper = objectMapper;
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
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        String cacheKey = CACHE_KEY_PREFIX + id;
        redisTemplate.opsForValue().set(cacheKey, userEntity,CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        LOGGER.info("Cache invalidated for update user id={}", id);

        return userToEntityMapper.toDomain(userRepository.findById(id).orElseThrow());
    }

    @Override
    public User getUserById(Long id) {
        LOGGER.info("Getting User from DB: {}", id);
        String cacheKey = CACHE_KEY_PREFIX + id;
        UserEntity entityFromCache = redisTemplate.opsForValue().get(cacheKey);
        if(entityFromCache != null){
            LOGGER.info("User found in cache: id={}", id);
            return userToEntityMapper.toDomain(entityFromCache);
        }
        LOGGER.info("User not found in cache: id={}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        redisTemplate.opsForValue().set(cacheKey, userEntity,CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        LOGGER.info("User cashed: id={}",id);

        return userToEntityMapper.toDomain(userEntity);
    }


    @Override
    public void deleteUser(Long id) {
        LOGGER.info("Deleting User from DB: {}", id);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(userEntity);
        String cacheKey = CACHE_KEY_PREFIX + id;
        redisTemplate.delete(cacheKey);
        LOGGER.info("Cache invalidated for deleted user id={}", id);
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
