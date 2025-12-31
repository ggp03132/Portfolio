package com.project.lms.progress;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {

    // 회원과 강의를 조회: 회원이 해당 강의에 대한 진행 데이터가 있으면 반환, 없으면 Optional.empty() 반환
    Optional<Progress> findByMember_MembernoAndLesson_Lessonno(Integer memberno, Integer lessonno);

    // 회원이 강좌에서 완료한 강의의 수를 계산
    @Query("SELECT COUNT(p) FROM Progress p " +
           "WHERE p.member.memberno = :memberId " +
           "AND p.lesson.course.courseno = :courseId " +
           "AND p.completed = true")
    int countCompletedLessons(@Param("memberId") Integer memberId, 
                              @Param("courseId") Integer courseId);

    // 해당 강좌의 전체 강의 개수
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.courseno = :courseId")
    int countTotalLessons(@Param("courseId") Integer courseId);

    // 회원이 특정 강좌에서 완료한 강의 ID 목록 반환
    @Query("SELECT p.lesson.lessonno " +
           "FROM Progress p " +
           "WHERE p.member.memberno = :memberNo " +
           "AND p.lesson.course.courseno = :courseNo " +
           "AND p.completed = true")
    List<Integer> findCompletedLessonIdsByMemberAndCourse(@Param("memberNo") Integer memberNo,
                                                          @Param("courseNo") Integer courseNo);
    
}

