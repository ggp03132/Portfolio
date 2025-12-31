package com.project.lms.member;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.project.lms.answer.Answer;
import com.project.lms.apply.Apply;
import com.project.lms.attempt.Attempt;
import com.project.lms.attendance.Attendance;
import com.project.lms.course.Course;
import com.project.lms.memo.Memo;
import com.project.lms.progress.Progress;
import com.project.lms.question.Question;
import com.project.lms.review.Review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberno;
	
	@Column(length =45,unique = true, nullable=false)
	private String memberid;
	
	@Column(length = 200)
	private String pwd;
	
	@Column(length = 45,nullable=false)
	private String name;
	
	@Column(length = 45,unique = true)
	private String cellnum;
	
	@Column(length = 45,nullable=false)
	private String birth;
	
	@Column(length = 45,unique = true,nullable=false)
	private String email;

	private Integer point=0;
	
	private String proimg = "/images/profile.png"; //디폴트 기본 이미지
	
	@Enumerated(EnumType.ORDINAL) //ORDINAL숫자로 저장
	private Role role;
	
	@CreatedDate
	private LocalDateTime creDate;
	
	@LastModifiedDate
	private LocalDateTime modDate;
	
	@Column(nullable = true) // 강사 등록에 필요한 정보로 학교 이름을 등록.
	private String schoolName;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Apply> applyList;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Attendance> attendanceList;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Review> reviewList;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Attempt> attemptList;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Answer> answerList;
	
	@OneToMany(mappedBy = "instructor", cascade = CascadeType.REMOVE)
	private List<Course> courseList;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Memo> memoList;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Progress> progressList;
	
	@OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
	private List<Question> questionList;
}
