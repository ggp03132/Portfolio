package com.project.lms.course;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.lms.category.Category;
import com.project.lms.member.Member;

public interface CourseRepository extends JpaRepository<Course, Integer> {
	
	//페이징
	Page<Course> findAll(Pageable pageable);
	Page<Course> findAll(Specification<Course> spec, Pageable pageable); //검색기능
	Page<Course> findByCategory(Category category, Pageable pageable);
	Page<Course> findByInstructor(Member instructor, Pageable pageable);//강사의 강좌리스트를 조회
	
	// 특정 강사가 등록한 모든 강좌 조회
    List<Course> findByInstructor(Member instructor);
}
