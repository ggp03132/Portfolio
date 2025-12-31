package com.project.lms.attempt;

import java.util.List;

import com.project.lms.member.Member;
import com.project.lms.quiz.Quiz;
import com.project.lms.useranswer.UserAnswer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Attempt {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer attemptno;
	
	private Integer score;
	
	@ManyToOne
	private Quiz quiz;
	
	@ManyToOne
	private Member member;
	
	@OneToMany(mappedBy = "attempt", cascade = CascadeType.REMOVE)
	private List<UserAnswer> useranserList;
}