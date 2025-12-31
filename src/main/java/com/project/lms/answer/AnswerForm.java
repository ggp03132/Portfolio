package com.project.lms.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {
	
	@NotBlank(message = "내용 작성은 필수입니다.")
	private String content;
}