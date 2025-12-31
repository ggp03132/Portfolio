package com.project.lms.quizquestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class QuizQuestionForm {
	@NotBlank(message = "문제 내용을 입력해주세요")
    private String content;
	
	@NotBlank(message = "퀴즈 명을 입력해주세요")
    @Pattern(regexp="O|X")
    private String answer;
	
    private Integer point;
}