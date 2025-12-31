package com.project.lms.course;

import java.time.LocalDateTime;
import java.util.List;

import com.project.lms.apply.Apply;
import com.project.lms.category.Category;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;
import com.project.lms.memo.Memo;
import com.project.lms.question.Question;
import com.project.lms.quiz.Quiz;
import com.project.lms.review.Review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer courseno;
	
	@Column(length = 45, nullable = false)
	private String title;
	
	@Column(length = 200, nullable = false)
	private String content;
	
	@Column(length = 255, nullable = false)
	private String courseimg;
	
	@Column(nullable = false)
	private Integer grade;
	private String book;
	private String bookimg;
	private LocalDateTime credate;
	private LocalDateTime moddate;
	
	@ManyToOne
	private Category category; //카테고리
	@ManyToOne 
    private Member instructor; // 강사 역할을 하는 Member 엔티티
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	private List<Lesson> lessonList;	//강의
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	private List<Apply> applyList;	//수강신청
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	private List<Memo> memoList;
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	private List<Question> questionList; 
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
	private List<Review> reviewList;
	
	@OneToOne(mappedBy = "course", cascade = CascadeType.REMOVE)
	private Quiz quiz; //퀴즈
}
