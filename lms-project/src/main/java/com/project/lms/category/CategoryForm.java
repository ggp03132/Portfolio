package com.project.lms.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {
	
	@NotBlank(message="카테고리 명은 필수 항목입니다.")
	private String title;
}
