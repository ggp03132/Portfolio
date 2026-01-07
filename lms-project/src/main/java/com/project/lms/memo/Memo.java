package com.project.lms.memo;

import java.time.LocalDateTime;

import com.project.lms.course.Course;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Memo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memono;
	
	@Column(length = 100, nullable = false)
	private String content;
	
	private LocalDateTime creDate;
	
	@ManyToOne
	private Member member;
	
	@ManyToOne
	@JoinColumn(name = "courseno")
	private Course course;
	
	@ManyToOne
	private Lesson lesson;
}
