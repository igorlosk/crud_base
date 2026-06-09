package com.crud_base.api;

import com.crud_base.db.UserEntity;
import com.crud_base.domain.DbUserService;
import com.crud_base.domain.User;
import com.crud_base.domain.UserService;
import com.crud_base.domain.UserToEntityMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final DbUserService dbUserService;
    private final UserToDtoMapper userToDtoMapper;

    public UserController(
            DbUserService dbUserService,
            UserToDtoMapper userToDtoMapper) {
        this.dbUserService = dbUserService;
        this.userToDtoMapper = userToDtoMapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody @Valid UserDto userDto) {
        User user = dbUserService.createUser(userDto);
        LOGGER.info("User created: {}", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userToDtoMapper.toDto(user));
    }
}
