package com.project.lms.attendance;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.member.Member;
import com.project.lms.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	private final MemberRepository memberRepository;
	//출석체크
	public void checkAttend(Member member) {		
		LocalDate today = LocalDate.now(); //출석 날짜 저장		
		if(attendanceRepository.existsByMemberAndAttdate(member, today)) {
			throw new DataNotFoundException("오늘은 이미 출석체크를 했습니다.");
		}		
		Attendance attendance = new Attendance();
		attendance.setMember(member);
		attendance.setPoint(10); //출석 포인트 10점으로 테스트 중
		attendance.setAttdate(today);
		attendanceRepository.save(attendance);		
		member.setPoint(member.getPoint() + attendance.getPoint()); //멤버의 기존 포인트 + 출석 포인트를 멤버에 저장
		memberRepository.save(member);
	}
	//회원의 출석일 카운트
	public int getAttendanceCount(Member member) {
		return attendanceRepository.countByMember(member);//리포지터리의 countByMember 정의한 메소드를 사용
	}
}