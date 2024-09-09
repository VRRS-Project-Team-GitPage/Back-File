package com.shinhan.VRRS.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompositePK implements Serializable {
    private Long proId;
    private Long userId;
}
