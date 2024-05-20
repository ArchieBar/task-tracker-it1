package ru.itone.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserFullNameAndEmailDto {
    private String fullName;
    private String email;
}
