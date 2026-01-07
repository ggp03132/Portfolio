package com.project.lms.review;

import java.time.LocalDateTime;

import com.project.lms.course.Course;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reviewno;
	
	private Integer rating = 5; // 별점 1 ~ 5
	
	@Column(length = 100, nullable = false)
	private String content;
	
	private LocalDateTime credate;
	
	private LocalDateTime moddate;
	
	@ManyToOne
	private Course course;
	
	@ManyToOne
	private Lesson lesson;
	
	@ManyToOne
	private Member member;
}