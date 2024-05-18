package ru.itone.exception.user;

public class UserInvalidPassword extends RuntimeException {
    public UserInvalidPassword() {
        super("Не верный пароль.");
    }
}
