package com.project.lms.quizquestion;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.lms.DataNotFoundException;
import com.project.lms.quiz.Quiz;
import com.project.lms.quiz.QuizService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/quizquestion")
@RequiredArgsConstructor
public class QuizQuestionController {
	
	private final QuizService quizService;
	private final QuizQuestionService quizQuestionService;
	
	
	//문제 등록 폼
	@GetMapping("/create/{quizno}")
	public String createQuestion(Model model, @PathVariable("quizno") Integer quizno) {
		
		// QuizService를 사용하여 퀴즈 조회
		Quiz quiz = quizService.getQuiz(quizno);
		
		model.addAttribute("quizno", quizno);
		model.addAttribute("quiz", quiz);
		model.addAttribute("course", quiz.getCourse());
		return "quiz_question/quiz_question_form";
	}
	
	//문제 등록 처리
	@PostMapping("/create/{quizno}")
	public String createQuestion(@PathVariable("quizno") Integer quizno,
								 @RequestParam("content") String content,
								 @RequestParam("type") String type,
								 @RequestParam("answer") String answer,
								 @RequestParam("point") Integer point,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            Quiz quiz = quizService.getQuiz(quizno);
            
            QuizQuestion quizQuestion = new QuizQuestion();
            
            try {
                // 입력된 문자열의 공백을 제거하고 대문자로 변환한 후 Enum으로 변환
                quizQuestion.setType(QuizType.valueOf(type.toUpperCase().trim())); 
            } catch (IllegalArgumentException e) {
                // 문제 유형 오류 발생 시 RedirectAttributes를 통해 메시지 전달 후 폼으로 복귀
                String validTypes = "MULTIPLE_CHOICE, SHORT_ANSWER, OX_QUIZ";
                redirectAttributes.addFlashAttribute("errorMessage", validTypes + " 유형의 정답을 입력해 주세요. 현재 입력된 값: " + type);
                return "redirect:/quizquestion/create/" + quizno;
            }
            
            quizQuestion.setContent(content);
            quizQuestion.setAnswer(answer);
            quizQuestion.setQuiz(quiz);
            quizQuestion.setPoint(point);
            
            quizQuestionService.create(quizQuestion);
            
            return "redirect:/quizquestion/detail/" + quizno; 

        } catch (DataNotFoundException e) {
            // 퀴즈를 찾을 수 없을 때
            redirectAttributes.addFlashAttribute("errorMessage", "오류: 해당 퀴즈(ID: " + quizno + ")를 찾을 수 없습니다.");
            return "redirect:/quiz/list"; 
        }
	}
	
	//문제 상세페이지
	@GetMapping("/detail/{quizno}")
	public String quizDetail(@PathVariable("quizno") Integer quizno, Model model) {

	    Quiz quiz = quizService.getQuiz(quizno);
	    List<QuizQuestion> quizquestionList = quiz.getQuizquestionList();

	    model.addAttribute("quiz", quiz);
	    model.addAttribute("questionList", quizquestionList);

	    return "quiz_question/quiz_question_detail";
	}
	
	//문제 정보 수정 폼 (GET)
	@GetMapping("/modify/{quizquestionno}")
	public String modifyQuestion(Model model, 
                                 @PathVariable("quizquestionno") Integer quizquestionno, 
                                 RedirectAttributes redirectAttributes) {
        
        try {
            // 문제 번호로 문제(QuizQuestion)를 찾고, 그 문제에서 퀴즈(Quiz)를 가져옵니다.
            QuizQuestion quizquestion = quizQuestionService.getQuestion(quizquestionno);
            Quiz quiz = quizquestion.getQuiz(); 
            
            List<QuizQuestion> quizquestionList = quiz.getQuizquestionList();
            
            model.addAttribute("quiz", quiz);
            model.addAttribute("questionList", quizquestionList);
            model.addAttribute("quizquestion", quizquestion); // 수정할 문제 객체
            
            return "quiz_question/quiz_question_modify";
        
        } catch (DataNotFoundException e) { 
            // 퀴즈 문제를 찾지 못했을 때의 
            redirectAttributes.addFlashAttribute("errorMessage", "오류: 수정할 퀴즈 문제(ID: " + quizquestionno + ")를 찾을 수 없습니다. 수정 페이지 로드 실패.");
            return "redirect:/quiz/list"; 
        }
	}
	
	//문제 정보 수정 처리 (POST)
	@PostMapping("/modify/{quizquestionno}")
	public String modifyQuestion(@PathVariable("quizquestionno") Integer quizquestionno,
			 					 @RequestParam("content") String content,
								 @RequestParam("answer") String answer,
								 @RequestParam("point") Integer point,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            QuizQuestion quizQuestion = quizQuestionService.getQuestion(quizquestionno);
            
            quizQuestion.setContent(content);
            quizQuestion.setAnswer(answer);
            
            quizQuestionService.modify(quizQuestion, content, answer, point);
            
            return "redirect:/quizquestion/detail/" + quizQuestion.getQuiz().getQuizno();

        } catch (DataNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "오류: 수정할 퀴즈 문제(ID: " + quizquestionno + ")를 찾을 수 없어 수정에 실패했습니다.");
            return "redirect:/quiz/list";
        }
	}
	
	//문제 정보 삭제
	@PostMapping("/delete/{quizquestionno}")
	public String deleteQuestion(@PathVariable("quizquestionno") Integer quizquestionno, RedirectAttributes redirectAttributes) {
        
        try {
            QuizQuestion quizQuestion = quizQuestionService.getQuestion(quizquestionno);
            Integer quizno = quizQuestion.getQuiz().getQuizno();
            
            quizQuestionService.delete(quizQuestion);
            return "redirect:/quizquestion/detail/" + quizno;
        
        } catch (DataNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "오류: 삭제할 퀴즈 문제(ID: " + quizquestionno + ")를 찾을 수 없어 삭제에 실패했습니다.");
            return "redirect:/quiz/list";
        }
	}
}