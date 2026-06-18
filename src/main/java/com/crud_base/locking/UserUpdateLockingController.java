package com.crud_base.locking;

import com.crud_base.api.UserDto;
import com.crud_base.api.UserToDtoMapper;
import com.crud_base.domain.DbUserService;
import com.crud_base.domain.User;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/users/lock")
public class UserUpdateLockingController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserUpdateLockingController.class);

    private final RedisLockManager redisLockManager;
    private final DbUserService dbUserService;
    private final UserToDtoMapper userToDtoMapper;

    public UserUpdateLockingController(
            RedisLockManager redisLockManager,
            DbUserService dbUserService,
            UserToDtoMapper userToDtoMapper) {
        this.redisLockManager = redisLockManager;
        this.dbUserService = dbUserService;
        this.userToDtoMapper = userToDtoMapper;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto userDto,
            @RequestParam(defaultValue = "500") long workMs
    ) {
        LOGGER.info("Updating user with id={}",id);
        String lockKye = "user:" + id;
        String lockId = redisLockManager.tryLock(lockKye, Duration.ofMinutes(1));
        if(lockId == null) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Блокировка захвачена для объекта %s. Попробуйте позже".formatted(lockKye)
            );
        }

        try {
            try {
                Thread.sleep(workMs);
            } catch (InterruptedException ignores){
                Thread.currentThread().interrupt();
            }

            User user = dbUserService.updateUser(id, userDto);
            UserDto responseUser = userToDtoMapper.toDto(user);
            LOGGER.info("User has been updated: id={}", id);
            return ResponseEntity.ok(responseUser);
        } finally {
            redisLockManager.unlockLock(lockKye,lockId);
        }
    }
}
