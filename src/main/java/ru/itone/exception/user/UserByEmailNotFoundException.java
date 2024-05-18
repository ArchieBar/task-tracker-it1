package ru.itone.exception.user;

public class UserByEmailNotFoundException extends RuntimeException {
    public UserByEmailNotFoundException(String email) {
        super(String.format("Пользователь с email: %s не найден.", email));
    }
}
