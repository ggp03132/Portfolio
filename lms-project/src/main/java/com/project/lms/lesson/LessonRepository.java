package com.project.lms.lesson;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.lms.course.Course;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
	List<Lesson> findByCourse(Course course); //강좌를 기준으로 강의 리스트 찾기 위해 (qna에서 사용할거임)

}
