package ru.itone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.user.dto.UserDto;
import ru.itone.model.user.dto.UserResponseDto;
import ru.itone.service.user.UserService;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto findUserById(@PathVariable("userId") UUID userId) {
        log.info("Вызов GET-операции: /user/{userId}");
        return userService.findUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.toCreate.class})
    public UserResponseDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Вызов POST-операции: /user");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public UserResponseDto updateUser(@PathVariable("userId") UUID userId,
                                      @RequestBody @Valid UserDto userDto) {
        log.info("Вызов PATCH-операции: /user/{userId}");
        return userService.updateUserById(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("userId") UUID userID) {
        log.info("Вызов DELETE-операции: /user/{userId}");
        userService.deleteUserById(userID);
    }
}
