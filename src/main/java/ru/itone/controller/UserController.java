package ru.itone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itone.model.Marker;
import ru.itone.model.user.dto.LoginFormDto;
import ru.itone.model.user.dto.RegisterFormDto;
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

    @GetMapping("/{searchUserId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto findUserById(@RequestHeader("X-user-Id") UUID userId,
                                        @PathVariable UUID searchUserId) {
        log.info("Вызов GET-операции: /user/{userId}");
        return userService.findUserById(userId, searchUserId);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.toCreate.class})
    public UserResponseDto registerUser(@RequestBody @Valid RegisterFormDto dto) {
        log.info("Вызов POST-операции: /user");
        return userService.registerUser(dto);
    }

    @PatchMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto login(@RequestBody LoginFormDto loginFormDto) {
        log.info("Вызов PATCH-операции: /user/login");

        return userService.login(loginFormDto);
    }

    @PatchMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto logout(@RequestHeader("X-User-Id") UUID userId) {
        log.info("Вызов PATCH-операции: /user/logout");

        return userService.logout(userId);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.toUpdate.class})
    public UserResponseDto updateUser(@RequestHeader("X-User-Id") UUID userId,
                                      @RequestBody @Valid UserDto userDto) {
        log.info("Вызов PATCH-операции: /user");
        return userService.updateUserById(userId, userDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@RequestHeader("X-User-Id") UUID userID) {
        log.info("Вызов DELETE-операции: /user");
        userService.deleteUserById(userID);
    }
}
