package com.project.lms.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizForm {
	@NotBlank(message = "퀴즈 명을 입력해주세요")
	private String title;
	@NotNull(message = "퀴즈 점수를 입력해주세요")
	private Integer totalpoint;
	
	private Integer quizno;
}