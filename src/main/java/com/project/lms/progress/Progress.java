package com.project.lms.progress;

import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer progressno; // 진행도 번호

    // 회원 정보 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberno", nullable = false)
    private Member member;

    // 강의 정보 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lessonno", nullable = false)
    private Lesson lesson;

    // 시청 완료 여부
    @Column(nullable = false)
    private boolean completed = false;
    
}
