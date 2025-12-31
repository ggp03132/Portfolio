package com.project.lms.lesson;

import java.time.LocalDateTime;
import java.util.List;

import com.project.lms.course.Course;
import com.project.lms.memo.Memo;
import com.project.lms.progress.Progress;
import com.project.lms.question.Question;
import com.project.lms.review.Review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Lesson {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer lessonno;
	
	@Column(length = 45, nullable = false)
	private String title;
	@Column(length = 200, nullable = false)
	private String content;
	@Column(length = 100, nullable = false)
	private String video;
	@Column(nullable = false)
	private Integer time; //분 단위 ex) 영상 길이가 120분이면 120 << 이렇게
	private LocalDateTime credate;
	private LocalDateTime moddate;
	
	@ManyToOne
	private Course course;
	
	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Review> reviewList;
	
	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Progress> progressList;
	
	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Memo> memoList;
	
	@OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE)
	private List<Question> questionList;
}
