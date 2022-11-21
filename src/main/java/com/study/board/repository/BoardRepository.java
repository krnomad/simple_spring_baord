package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    // JPA Repository 아래 이름 규칙으로 method 제공함
    // findBy(컬러 이름)Containing : 포함
    // findBy(컬럼 이름) : 완전한 일치
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
}
