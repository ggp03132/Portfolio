package com.project.lms.attendance;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.project.lms.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Attendance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer attendanceno;
	
	@CreationTimestamp //현재 날짜 자동 설정
    @Column(nullable = false)
	private LocalDate attdate; //localdatetime으로 하면 같은 날인데 다른 시간으로 출석 처리가 될 수 있어서 localdate 사용
	
	private Integer point;
	
	@ManyToOne
	private Member member;
}