package com.project.lms.course;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseForm {
	
	@NotBlank(message = "강좌 명은 필수항목입니다.")
	private String title;
	@NotBlank(message = "강좌 설명은 필수항목입니다.")
	private String content;
	@NotBlank(message = "강좌 이미지를 넣어주세요")
	private String courseimg;
	@NotNull(message = "학년을 적어주세요")
	@Min(1)
	@Max(6)
	private Integer grade; //int 같은 숫자 타입은 notblank가 안되고 notnull 써야함
	
	private String book;
	private String bookimg;
	private LocalDateTime credate;
	private LocalDateTime moddate;
	
	private Integer courseno;
	
}
