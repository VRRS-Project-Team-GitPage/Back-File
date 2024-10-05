package com.shinhan.VRRS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Ingredient {
    @Id
    private Integer id;
    private String name;
    private Integer vegTypeId;
}