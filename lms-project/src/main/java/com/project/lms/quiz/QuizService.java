package com.project.lms.quiz;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.course.Course;
import com.project.lms.lesson.Lesson;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService {
	private final QuizRepository qr;
	
	//강좌에 맞는 리스트
	public List<Quiz> getQuizByCourse(Integer courseno){
		return qr.findByCourse_Courseno(courseno);
	}
	
	//리스트
	public List<Quiz> getList(){
		return qr.findAll();
	}
	
	//상세 조회
	public Quiz getQuiz(Integer quizno) {
		Optional<Quiz> quiz = qr.findById(quizno);
		if(quiz.isPresent()) {
			return quiz.get();
		} else {
			throw new DataNotFoundException("quiz not found"); 
		}
	}
	
	//생성
	public void create(String title, Integer totalpoint, Course course) {
		Quiz quiz = new Quiz();
		quiz.setTitle(title);
		quiz.setTotalpoint(totalpoint);
		quiz.setCourse(course);
		qr.save(quiz);
	}
	
	//수정
	public void modify(Quiz quiz, String title, Integer totalpoint) {
		quiz.setTitle(title);
		quiz.setTotalpoint(totalpoint);
		qr.save(quiz);
	}
	
	//삭제
	public void delete(Quiz quiz) {
		qr.delete(quiz);
	}
}