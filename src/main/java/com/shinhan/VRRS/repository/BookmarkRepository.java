package com.shinhan.VRRS.repository;

import com.shinhan.VRRS.entity.Bookmark;
import com.shinhan.VRRS.entity.CompositePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, CompositePK> {
}