package com.project.lms.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	Optional<Category> findByTitle(String title);//중복검사를 위한메소드
}
