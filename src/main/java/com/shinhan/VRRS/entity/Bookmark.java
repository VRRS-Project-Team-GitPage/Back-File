package com.shinhan.VRRS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@IdClass(CompositePK.class)
public class Bookmark {
    @Id
    private Long proId;
    @Id
    private Long userId;

    private LocalDateTime date;

    public Bookmark(Long proId, Long userId) {
        this.proId = proId;
        this.userId = userId;
        this.date = LocalDateTime.now();
    }
}