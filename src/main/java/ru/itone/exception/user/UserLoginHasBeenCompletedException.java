package ru.itone.exception.user;

public class UserLoginHasBeenCompletedException extends RuntimeException {
    public UserLoginHasBeenCompletedException() {
        super("Вход в систему уже выполнен.");
    }
}
