package com.project.lms.apply;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.course.Course;
import com.project.lms.course.CourseRepository;
import com.project.lms.member.Member;
import com.project.lms.member.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplyService {
	
	private final ApplyRepository applyRepository;
	private final CourseRepository courseRepository;
	
	//신청
	public Apply enroll(Member member, Course course) {
		if (member.getRole() == Role.INSTRUCTOR) {
			throw new IllegalStateException("강사는 수강 신청이 불가능합니다."); 
		}
		if (applyRepository.existsByMemberAndCourseAndStatus(member, course, 1)) {
			throw new DataNotFoundException("이미 신청완료 강좌");
		} 
		Apply apply = new Apply();
		apply.setMember(member);
		apply.setCourse(course);
		apply.setApldate(LocalDateTime.now());
		apply.setStatus(1);
		return applyRepository.save(apply);
	}
    
	 //전체 수강신청 조회
    public List<Apply> getAllApplies() {
        return applyRepository.findAll();
    }
	
	//신청 목록 조회
	public List<Apply> getApplyByMember(Member member){
		return applyRepository.findByMember(member);
	}
	
	//수강취소 >> 수강신청상태를 0으로 바꾸기
	public void cancle(Apply apply) {
		if(apply.getStatus() == 0) {
			throw new IllegalStateException("이미 취소된 강좌입니다.");
		}
		apply.setStatus(0);
		applyRepository.save(apply);
	}
	
	public Apply getApply(Integer applyno) {
		return applyRepository.findById(applyno).orElseThrow(() -> new DataNotFoundException("신청내역이 없습니다."));
	}
	
	//수강중인 강좌 메인에서 출력
	public List<Course> getEnrolledCourses(Member member) {
	    
	    // 1. applyRepository.findByMemberAndStatus 호출:
	    //    member가 '신청 완료' 상태(1)인 Apply 기록만 가져옴.
	    List<Apply> activeApplies = applyRepository.findByMemberAndStatus(member, 1);
	    
	    // 2. Stream을 사용하여 Apply 객체에서 Course 객체만 추출
	    List<Course> enrolledCourses = activeApplies.stream()
	            .map(Apply::getCourse) 
	            .collect(Collectors.toList());
	    
	    return enrolledCourses;
	}
	
	public Page<Apply> getActiveApplicationsByMember(Member member, Pageable pageable) {
        // status=1: '수강 중' 상태
        return applyRepository.findByMemberAndStatus(member, 1, pageable);
    }
	
	// 수강 중인지 확인 (status = 1)
    public boolean isEnrolled(Member member, Course course) {
        return applyRepository.existsByMemberAndCourseAndStatus(member, course, 1);
    }
    //회원리스트 검색기능
    public List<Apply> findAllApplies() {
        return applyRepository.findAll();
    }

    public List<Apply> findByCourseno(Integer courseno) {
        return applyRepository.findByCourseCourseno(courseno);
    }
}