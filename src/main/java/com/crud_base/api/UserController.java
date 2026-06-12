package com.crud_base.api;

import com.crud_base.domain.DbUserService;
import com.crud_base.domain.User;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        LOGGER.info("User created with cacheMode={}", "none-cache");
        return ResponseEntity.status(HttpStatus.CREATED).body(userToDtoMapper.toDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        User user = dbUserService.getUserById(id);
        LOGGER.info("Getting user id={} with cacheMode={}", id, "none-cache");
        return ResponseEntity.ok(userToDtoMapper.toDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto userDto) {
        User user = dbUserService.updateUser(id, userDto);
        LOGGER.info("Update user id={} with casheMode={}", id, "none-cache");
        return ResponseEntity.ok(userToDtoMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id){
        dbUserService.deleteUser(id);
        LOGGER.info("Deleted user id={} with cacheMode={}", id, "none-cache");
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<UserDto> gelAllUsers (){
        List<User> userList = dbUserService.getAllUsers();
        return userList
                .stream()
                .map(userToDtoMapper::toDto)
                .toList();
    }
}
