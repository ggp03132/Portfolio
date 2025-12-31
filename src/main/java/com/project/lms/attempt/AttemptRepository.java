package com.project.lms.attempt;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.lms.member.Member;
import com.project.lms.quiz.Quiz;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
	Optional<Attempt> findTopByMemberAndQuizOrderByScoreDesc(Member member, Quiz quiz); //가장 높은 점수 가져오기
}