package com.project.lms.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.course.Course;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	
	private final QuestionRepository questionRepository;
	
//검색기능은 answer도 만든 다음에 하기
	
	public Page<Question> getList(int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("credate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		return questionRepository.findAll(pageable);
	}
	
	public List<Question> getList(){
		return questionRepository.findAll(Sort.by(Sort.Direction.DESC, "questionno")); //최신순
	}
	
	public Question getQuestion(Integer questionno) {
		Optional<Question> question = questionRepository.findById(questionno);
		if(question.isPresent()) {
			return question.get();
		} else {
			throw new DataNotFoundException("not found");
		}
	}
	
	public void create(String title, String content, Member member, Lesson lesson, Course course) {
		Question question = new Question();
		question.setTitle(title);
		question.setContent(content);
		question.setCredate(LocalDateTime.now());
		question.setModdate(LocalDateTime.now());
		question.setMember(member);
		question.setLesson(lesson);
		question.setCourse(course);
		questionRepository.save(question);
	}
	
	public void modify(Question question, String title, String content) {
		question.setTitle(title);
		question.setContent(content);
		question.setModdate(LocalDateTime.now());
		questionRepository.save(question);
	}
	
	public void delete(Question question) {
		questionRepository.delete(question);
	}
	
	//강좌에 속한 질문 리스트
	public List<Question> getListByCourse(Course course){
		return questionRepository.findByCourse(course);
	}
	
	public List<Question> getRecentQuestions(int instructorId, int limit) {
	    return questionRepository.findRecentQuestionsByInstructor(
	        instructorId,
	        PageRequest.of(0, limit)
	    );
	}
}