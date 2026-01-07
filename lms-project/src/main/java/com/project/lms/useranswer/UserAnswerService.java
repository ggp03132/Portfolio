package com.project.lms.useranswer;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.attempt.Attempt;
import com.project.lms.quiz.Quiz;
import com.project.lms.quizquestion.QuizQuestion;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAnswerService {
	
	private final UserAnswerRepository userAnswerRepository;
	
	//퀴즈에 대한 기록 가져오기
	public List<UserAnswer> getAnswerByQuiz(Quiz quiz) {
		return userAnswerRepository.findByAttempt_Quiz(quiz);
	}
	
	// 코스의 모든 학생 답안 가져오기
	public List<UserAnswer> getAnswerByCourse(Integer courseno) {
	    return userAnswerRepository.findByCourse(courseno);
	}
	
	//생성
	public UserAnswer create(Attempt attempt, QuizQuestion quizQuestion, String answer, Boolean iscorrect) {
		UserAnswer userAnswer = new UserAnswer();
		userAnswer.setAnswer(answer);
		userAnswer.setIscorrect(iscorrect);
		userAnswer.setAttempt(attempt);
		userAnswer.setQuizQuestion(quizQuestion);
		return userAnswerRepository.save(userAnswer);
	}
	
	//리스트
	public List<UserAnswer> getListByAttempt(Attempt attempt){
		return userAnswerRepository.findByAttempt(attempt);
	}
	
	//1개 조회
	public UserAnswer getAnswer(Integer useranswerno) {
	    return userAnswerRepository.findById(useranswerno)
	            .orElseThrow(() -> new DataNotFoundException("not found"));
	}
	
	//삭제
	public void delete(UserAnswer useranswer) {
		userAnswerRepository.delete(useranswer);
	}
}
