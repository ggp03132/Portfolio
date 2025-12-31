package com.project.lms.apply;

import java.time.LocalDateTime;

import com.project.lms.course.Course;
import com.project.lms.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Apply {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer applyno;
	
	private LocalDateTime apldate;
	private Integer status = 0; //수강중 = 1 수강취소 = 0
	
	@ManyToOne
	private Member member;
	
	@ManyToOne
	private Course course;
}