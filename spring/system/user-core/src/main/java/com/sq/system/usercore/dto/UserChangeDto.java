package com.sq.system.usercore.dto;

import lombok.Data;

@Data
public class UserChangeDto {
    private String oldPassword;
    private String newPassword;
    private Integer type;
}
