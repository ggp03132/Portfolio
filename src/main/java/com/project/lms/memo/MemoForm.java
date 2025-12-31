package com.project.lms.memo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoForm {

	  @NotBlank(message = "내용을 입력하세요.")
	    private String content;
}
