package com.shinhan.VRRS.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String nickname;
    private Integer vegTypeId;
}
