package com.project.lms.category;

import java.util.List;

import com.project.lms.course.Course;

//import com.project.lms.course.Course;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer categoryno;
	
	@Column(length = 45,nullable = false)
	private String title;
	
	@OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
	private List<Course> courseList;
}
