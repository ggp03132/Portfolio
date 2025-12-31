package com.project.lms.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {
	
	@NotBlank(message = "내용을 입력해주세요.")
	private String content;
	
	private Integer rating;
	
	private Integer lessonno;
	
	private Integer courseno;
}
