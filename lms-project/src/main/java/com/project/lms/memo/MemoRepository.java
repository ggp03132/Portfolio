package com.project.lms.memo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

public interface MemoRepository extends JpaRepository<Memo, Integer> {

	List<Memo> findByLesson(Lesson lesson);
    Page<Memo> findByMember(Member member, Pageable pageable);

}
