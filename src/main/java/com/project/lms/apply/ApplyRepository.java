package com.project.lms.apply;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.lms.course.Course;
import com.project.lms.member.Member;

public interface ApplyRepository extends JpaRepository<Apply, Integer> {
	 //유저가 신청한 강좌인지 확인하는 거 (existsBy..가 존재여부 true false로 확인)
	boolean existsByMemberAndCourseAndStatus(Member member, Course course, int status);
	List<Apply> findByMember(Member member); //멤버의 신청 목록
	List<Apply> findByCourse(Course course); //강좌 신청한 사람 목록
	List<Apply> findByMemberAndStatus(Member member, int status);
	Page<Apply> findByMemberAndStatus(Member member, int status, Pageable pageable);
	
	// 수강 중인 경우만 true
    boolean existsByMemberAndCourseAndStatus(Member member, Course course, Integer status);
    
    List<Apply> findByCourseCourseno(Integer courseno);

}