package com.project.lms.attendance;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.lms.member.Member;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
	//오늘 출석했는지 확인 (출석 중복 확인)
	boolean existsByMemberAndAttdate(Member member, LocalDate attDate); 
	// 특정 회원의 총 출석 횟수를 계산
    int countByMember(Member member);
}