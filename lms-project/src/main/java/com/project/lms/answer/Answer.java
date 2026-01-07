package com.project.lms.answer;

import java.time.LocalDateTime;

import com.project.lms.member.Member;
import com.project.lms.question.Question;

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
public class Answer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer answerno;
	
	@Column(length = 100, nullable = false)
	private String content;
	
	private LocalDateTime credate;
	private LocalDateTime moddate;
	
	@ManyToOne
	private Question question;
	
	@ManyToOne
	private Member member;
}