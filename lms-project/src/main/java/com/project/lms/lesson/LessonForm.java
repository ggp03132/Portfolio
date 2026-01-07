package com.project.lms.lesson;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonForm {
	
	@NotBlank(message = "강의 명은 필수항목입니다.")
	private String title;
	
	@NotBlank(message = "강의 내용은 필수항목입니다.")
	private String content;
	
	@NotBlank(message = "강의영상은 필수입니다.")
	private String video;
	
	@NotNull(message = "강의시간은 필수입니다.")
	private Integer time;
	
	private LocalDateTime credate;
	private LocalDateTime moddate;
	
	private Integer lessonno;

	
}
