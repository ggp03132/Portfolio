package com.project.lms.quizquestion;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.lms.quiz.Quiz;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizQuestionService {

	private final QuizQuestionRepository quizQuestionRepository;

	// 문제 등록
	public QuizQuestion create(QuizQuestion quizquestion) {
		return quizQuestionRepository.save(quizquestion);
	}

	// 문제 조회(퀴즈를 기준으로 문제를 찾음)
	public List<QuizQuestion> getQuestionByQuiz(Quiz quiz) {
		return quizQuestionRepository.findByQuiz(quiz);
	}
	//문제 1개 조회
	public QuizQuestion getQuestion(Integer questionId) {
		return quizQuestionRepository.findById(questionId)
				.orElseThrow(() -> new IllegalArgumentException("해당 문제가 없습니다."));
	}
	//수정
	public void modify(QuizQuestion quizquestion,String content,String answer, Integer point) {
		quizquestion.setContent(content);
		quizquestion.setAnswer(answer);
		quizquestion.setPoint(point);
		
		quizQuestionRepository.save(quizquestion);
	}
	
	//삭제
	public void delete(QuizQuestion quizquestion) {
		quizQuestionRepository.delete(quizquestion);
	}
	
}