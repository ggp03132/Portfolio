package com.project.lms.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.member.Member;
import com.project.lms.question.Question;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {
	
	private final AnswerRepository answerRepository;
	
	public Answer getAnswer(Integer answerno) {
		Optional<Answer> answer = answerRepository.findById(answerno);
		if(answer.isPresent()) {
			return answer.get();
		} else {
			throw new DataNotFoundException("not found");
		}
	}
	
	public void create(Question question, String content, Member member) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setCredate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setMember(member);
		answerRepository.save(answer);
	}
	
	public void modify(Answer answer, String content) {
		answer.setContent(content);
		answer.setModdate(LocalDateTime.now());
		answerRepository.save(answer);
	}
	
	public void delete(Answer answer) {
		answerRepository.delete(answer);
	}
}