package com.project.lms.attempt;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.member.Member;
import com.project.lms.quiz.Quiz;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttemptService {
	
	private final AttemptRepository attemptRepository;
	
	//가장 높은 점수 확인용
	public Integer getTopScore(Member member, Quiz quiz) {
		return attemptRepository.findTopByMemberAndQuizOrderByScoreDesc(member, quiz).map(Attempt::getScore).orElse(0);
	}
	
	public List<Attempt> getList(){
		return attemptRepository.findAll();
	}
	
	public Attempt getAttempt(Integer attemptno) {
		Optional<Attempt> attempt = attemptRepository.findById(attemptno);
		if(attempt.isPresent()) {
			return attempt.get();
		} else {
			throw new DataNotFoundException("not found");
		}
	}
	
	public Attempt create(Integer score, Quiz quiz, Member member) {
		Attempt attempt = new Attempt();
		attempt.setScore(score);
		attempt.setQuiz(quiz);
		attempt.setMember(member);
		return attemptRepository.save(attempt);
	}
	
	public void delete(Attempt attempt) {
		attemptRepository.delete(attempt);
	}
}