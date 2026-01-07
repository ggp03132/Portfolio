package com.project.lms.category;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
	
	private final CategoryService cs;
	
	//리스트
	@GetMapping("/list")
	public String list(Model model) {
		List<Category> categoryList = cs.getList();
		model.addAttribute("categoryList", categoryList);
		return "category/category_list";
	}
	
	//등록
    @PreAuthorize("hasRole('ADMIN')") //관리자 권한
    @PostMapping("/create")
    public String categoryCreate(@RequestParam("title") String title, Model model,RedirectAttributes ra) {
    	try {
            cs.create(title);
            // 성공 메시지 추가
            ra.addFlashAttribute("message", "카테고리 **" + title + "**가 성공적으로 추가되었습니다.");
            ra.addFlashAttribute("alertClass", "alert-success");
        } catch (IllegalArgumentException e) {
            // 실패(중복) 메시지 추가
            ra.addFlashAttribute("message", e.getMessage());
            ra.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/category/list";
    }
    
	//수정
    @PreAuthorize("hasRole('ADMIN')") //관리자 권한
	@PostMapping("/update")
	public String categoryUpdate(@RequestParam("title") String title, Category category) {
		cs.modify(category,title);
		return "redirect:/category/list";
	}
    //삭제
    @PreAuthorize("hasRole('ADMIN')") //관리자 권한
    @PostMapping("/delete/{categoryno}")
    public String categoryDelete(@PathVariable("categoryno") Integer categoryno) { 
    	Category category = cs.getCategory(categoryno);
        cs.delete(category);      
        return "redirect:/category/list";
    }
	
}