package ru.itone.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itone.model.user.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
