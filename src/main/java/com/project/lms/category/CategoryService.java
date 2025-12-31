package com.project.lms.category;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.lms.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository cr;
	
	//리스트
	public List<Category> getList(){
		return cr.findAll();
	}
	
	//카테고리별 리스트
	public List<Category> getAllCategories() {
        return cr.findAll();
    }
	
	//생성
	public void create(String title) {
        Optional<Category> existingCategory = cr.findByTitle(title);//중복검사        
        if (existingCategory.isPresent()) {            
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + title); //중복시 예외
        }       
        Category category = new Category();
        category.setTitle(title);
        cr.save(category);
    }	
	
	//수정
	public void modify(Category category, String title) {
		category.setTitle(title);
		cr.save(category);
	}
	//삭제
	public void delete(Category category) {
		cr.delete(category);
	}
	//조회
	public Category getCategory(Integer categoryno) {
		Optional<Category> category = cr.findById(categoryno);
		if(category.isPresent()) {
			return category.get();
		} else {
			throw new DataNotFoundException("category not found");
		}
	}
}
