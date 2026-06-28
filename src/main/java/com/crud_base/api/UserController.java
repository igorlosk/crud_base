package com.crud_base.api;

import com.crud_base.domain.*;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final DbUserService dbUserService;
    private final UserToDtoMapper userToDtoMapper;
    private final ManualCachingProductService manualCachingProductService;
    private final SpringAnnotationCachingUserService springAnnotationCachingUserService;

    public UserController(
            DbUserService dbUserService,
            UserToDtoMapper userToDtoMapper,
            ManualCachingProductService manualCachingProductService,
            SpringAnnotationCachingUserService springAnnotationCachingUserService) {
        this.dbUserService = dbUserService;
        this.userToDtoMapper = userToDtoMapper;
        this.manualCachingProductService = manualCachingProductService;
        this.springAnnotationCachingUserService = springAnnotationCachingUserService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @RequestBody @Valid UserDto userDto,
            @RequestParam(value = "cacheMode", defaultValue = "NON_CACHE") CacheMode cacheMode) {

        UserService service = resolveProductService(cacheMode);

        User user = service.createUser(userDto);
        LOGGER.info("User created with cacheMode={}", cacheMode);
        return ResponseEntity.status(HttpStatus.CREATED).body(userToDtoMapper.toDto(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id,
            @RequestParam(value = "cacheMode", defaultValue = "NON_CACHE") CacheMode cacheMode) {
        UserService service = resolveProductService(cacheMode);
        User user = service.getUserById(id);
        LOGGER.info("Getting user id={} with cacheMode={}", id, cacheMode);
        return ResponseEntity.ok(userToDtoMapper.toDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserDto userDto,
            @RequestParam(value = "cacheMode", defaultValue = "NON_CACHE") CacheMode cacheMode) {
        UserService service = resolveProductService(cacheMode);
        User user = service.updateUser(id, userDto);
        LOGGER.info("Update user id={} with casheMode={}", id, cacheMode);
        return ResponseEntity.ok(userToDtoMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable("id") Long id,
            @RequestParam(value = "cacheMode", defaultValue = "NON_CACHE") CacheMode cacheMode) {
        UserService service = resolveProductService(cacheMode);
        service.deleteUser(id);
        LOGGER.info("Deleted user id={} with cacheMode={}", id, cacheMode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<UserDto> gelAllUsers() {
        List<User> userList = dbUserService.getAllUsers();
        return userList
                .stream()
                .map(userToDtoMapper::toDto)
                .toList();
    }

    private UserService resolveProductService(CacheMode cacheMode) {

        return switch (cacheMode) {
            case NON_CACHE -> dbUserService;
            case MANUAL -> manualCachingProductService;
            case SPRING -> springAnnotationCachingUserService;
        };
    }
}
