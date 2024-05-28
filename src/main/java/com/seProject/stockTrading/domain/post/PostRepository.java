package com.seProject.stockTrading.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitle(String title);
    List<Post> findAllByOrderByIdDesc();
    List<Post> findAllByOrderByCreatedAtDesc();
}
