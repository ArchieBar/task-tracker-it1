package ru.itone.exception.user;

public class UserLoginHasBeenNotCompletedException extends RuntimeException {
    public UserLoginHasBeenNotCompletedException() {
        super("Пользователь не вошел в систему");
    }
}
