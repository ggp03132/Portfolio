package com.project.lms.member;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	boolean existsByMemberid(String memberid);
	boolean existsByCellnum(String cellnum);
	boolean existsByEmail(String email);
	boolean existsByMemberidAndMembernoNot(String memberid,Integer memberno);
	boolean existsByEmailAndMembernoNot(String email,Integer memberno);
	boolean existsByCellnumAndMembernoNot(String cellnum,Integer memberno);
	
	Optional<Member> findByMemberid(String memberid); //findById는 기본키로 잇어서 pk!
	Optional<Member> findByCellnum(String cellnum);
	Optional<Member> findByEmail(String email);
	
	@Query("SELECT m FROM Member m WHERE m.role = :role")
	Page<Member> findByRoleValue(@Param("role") Role role, Pageable pageable);
	
	// 검색 기능 추가
	@Query("SELECT m FROM Member m WHERE " +
	       "(:keyword IS NULL OR :keyword = '' OR " +
	       "m.memberid LIKE %:keyword% OR " +
	       "m.name LIKE %:keyword% OR " +
	       "m.email LIKE %:keyword% OR " +
	       "m.cellnum LIKE %:keyword%)")
	Page<Member> searchMembers(@Param("keyword") String keyword, Pageable pageable);
	
	@Query("SELECT m FROM Member m WHERE m.role = :role AND " +
	       "(:keyword IS NULL OR :keyword = '' OR " +
	       "m.memberid LIKE %:keyword% OR " +
	       "m.name LIKE %:keyword% OR " +
	       "m.email LIKE %:keyword% OR " +
	       "m.cellnum LIKE %:keyword%)")
	Page<Member> searchMembersByRole(@Param("role") Role role, @Param("keyword") String keyword, Pageable pageable);
	}