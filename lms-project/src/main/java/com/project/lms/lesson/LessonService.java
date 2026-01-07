package com.project.lms.lesson;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.course.Course;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LessonService {
	
	private final LessonRepository lr;
	
	//강좌 기준 강의 찾기
	public List<Lesson> getLessonByCourse(Course course){
		return lr.findByCourse(course);
	}
	
	//목록
	public List<Lesson> getList(){
		return lr.findAll();
	}
	
	//조회
	public Lesson getLesson(Integer lessonno) {
		Optional<Lesson> lesson = lr.findById(lessonno);
		if(lesson.isPresent()) {
			return lesson.get();
		} else {
			throw new DataNotFoundException("lesson not found");
		}
	}
	
	//생성
	public void create(String title, String content, String video, Integer time, Course course) {
		Lesson lesson = new Lesson();
		lesson.setTitle(title);
		lesson.setContent(content);
		lesson.setVideo(video);
		lesson.setTime(time);
		lesson.setCredate(LocalDateTime.now());
		lesson.setModdate(LocalDateTime.now());
		lesson.setCourse(course);
		lr.save(lesson);
	}
	
	/*
	생성 메서드의 경우 이렇게 간단하게도 작성 가능
	public Lesson create(Lesson lesson) {
		lesson.setCredate(LocalDateTime.now());
		lesson.setModdate(LocalDateTime.now());
		return lr.save(lesson);
	}
	*/
	
	//수정
	public void modify(Lesson lesson, String title, String content, String video, Integer time) {
		lesson.setTitle(title);
		lesson.setContent(content);
		lesson.setVideo(video);
		lesson.setTime(time);
		lesson.setModdate(LocalDateTime.now());
		lr.save(lesson);
	}
	
	//삭제
	public void delete(Lesson lesson) {
		lr.delete(lesson);
	}
}
