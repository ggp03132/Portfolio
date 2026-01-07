package com.project.lms.memo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;
import com.project.lms.lesson.Lesson;
import com.project.lms.member.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemoService {
	
	private final MemoRepository memoRepository;
	
	public List<Memo> getList(Lesson lesson){
		return memoRepository.findByLesson(lesson);
	}
	public List<Memo> getAllMemo(){
		return memoRepository.findAll();
	}
	public Page<Memo> getMemoByMember(int page, Member member) {
	    List<Sort.Order> sorts = new ArrayList<>();
	    sorts.add(Sort.Order.desc("creDate"));

	    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

	    return memoRepository.findByMember(member, pageable);
	}
	public Memo getMemo(Integer memono) {
	    return memoRepository.findById(memono)
	            .orElseThrow(() -> new DataNotFoundException("memo not found"));
	}
	//등록
	public void Memocreate(String content,Lesson lesson,Member member) {
		
		Memo memo = new Memo();
		memo.setContent(content);
		memo.setCreDate(LocalDateTime.now());
		memo.setLesson(lesson);
		memo.setMember(member);
		memo.setCourse(lesson.getCourse());
		
		memoRepository.save(memo);
	}
	//수정
	public void modify(Integer memono,String content) {
		
		Memo memo = getMemo(memono); //기존엔티티조회
		
		memo.setContent(content);
		memo.setCreDate(LocalDateTime.now());
		
		memoRepository.save(memo);
	}
	//삭제
	public void delete(Memo memo) {
		memoRepository.delete(memo);
	}
	
}
