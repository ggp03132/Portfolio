package com.project.lms.progress;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.lms.apply.Apply;
import com.project.lms.lesson.Lesson;
import com.project.lms.lesson.LessonRepository;
import com.project.lms.member.Member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {
	private final ProgressRepository progressRepository;
	private final LessonRepository lessonRepository;

	// 회원이 특정강의를 끝까지 시청했을때 시청완료로 변경하는 메소드
	@Transactional
	public void completeLesson(Integer memberId, Integer lessonId) {
		// 회원이 해당 강의에 대한 진행도가 있는지 찾는 코드
		Progress progress = progressRepository.findByMember_MembernoAndLesson_Lessonno(memberId, lessonId)
				// 없을 경우 새로 진행도 객체 생성
				.orElseGet(() -> {
					Progress newProgress = new Progress(); // 진행도 객체생성
					Member m = new Member(); // 진행도 객체와 연관된 외래키로 인식하기 위해 멤버객체 새로 생성
					m.setMemberno(memberId);
					Lesson l = new Lesson(); // 진행도 객체와 연관된 외래키로 인식하기 위해 강의객체 새로 생성
					l.setLessonno(lessonId);
					newProgress.setMember(m);
					newProgress.setLesson(l);
					return newProgress;
				});

		// 이미 완료된 상태면 재저장하지 않음
		if (!progress.isCompleted()) {
			progress.setCompleted(true);
			progressRepository.save(progress);
		}
	}

	// 완료한 강의의 목록
	public List<Integer> getCompletedLessonIds(Integer memberId, Integer courseId) {
		return progressRepository.findCompletedLessonIdsByMemberAndCourse(memberId, courseId);
	}

	// lessonId로 courseId 찾기
	public Integer getCourseIdFromLesson(Integer lessonId) {
		Lesson lesson = lessonRepository.findById(lessonId)
				.orElseThrow(() -> new RuntimeException("해당 강의가 존재하지 않습니다."));
		return lesson.getCourse().getCourseno();
	}

	// 회원이 해당 강좌의 강의를 얼마나 시청완료 했는지를 보는 강좌 진행률 계산
	public double getCourseProgress(Integer memberId, Integer courseId) {
		// 강좌에 존재하는 전체 강의의 개수
		int total = progressRepository.countTotalLessons(courseId);
		// 시청을 완료한 강의의 개수를 구함
		int completed = progressRepository.countCompletedLessons(memberId, courseId);
		if (total == 0)
			return 0.0; // 강좌에 강의가 아직 등록되지 않은 경우에는 진행률을 0으로 반환
		return (completed * 100.0) / total; // 진행률 계산 (완료한 강의수 / 전체강의수) * 100
	}

	// 나의 학습방에서 진행률 출력
	public Map<Integer, Double> getProgressRatesForCourses(Integer memberId, List<Integer> courseIds) {
		Map<Integer, Double> progressRates = new HashMap<>();
		for (Integer courseId : courseIds) {
			double rate = getCourseProgress(memberId, courseId);
			progressRates.put(courseId, rate);
		}
		return progressRates;
	}

	// 강사 회원 리스트용 진행률 계산
	public Map<String, Double> getProgressRatesForCoursesForMembers(List<Apply> applies) {
		Map<String, Double> progressRates = new HashMap<>();
		for (Apply apply : applies) {
			Integer memberId = apply.getMember().getMemberno();
			Integer courseId = apply.getCourse().getCourseno();
			double rate = getCourseProgress(memberId, courseId);
			
			String key = memberId + "-" + courseId;
			System.out.println("저장되는 KEY = " + key + ", RATE = " + rate);
			progressRates.put(key, rate); // memberId 기준으로 진행률 저장
		}
		return progressRates;
	}
	
	// 회원이 완료한 강좌 리스트 반환 (모든 강의가 완료된 강좌만)
    public List<Integer> getCompletedCourses(Integer memberId, List<Integer> courseIds) {
        return courseIds.stream()
                .filter(courseId -> getCourseProgress(memberId, courseId) >= 100.0)
                .toList(); // Java 16 이상. 이전 버전은 collect(Collectors.toList()) 사용
    }

}
