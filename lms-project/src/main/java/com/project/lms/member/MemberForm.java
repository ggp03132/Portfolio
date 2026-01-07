package com.project.lms.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

	@NotBlank(message = "사용자 ID는 필수 항목입니다.")
	private String memberid;
	
	private String pwd;
	
	private String pwd2;
	
	@NotBlank(message = "이름은 필수 항목입니다.")
	private String name;
	
	@NotBlank(message = "전화번호는 필수 항목입니다.")
	private String cellnum;
	
	@NotBlank(message = "생년월일은 필수 항목입니다.")
	private String birth;
	
	private String role;
	
	private String schoolName;
	
	@Email
	private String email;

	private Integer memberno;
	
	private boolean isUpdate;

}