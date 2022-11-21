package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class BoardController {
    public static final int MAX_PAGE=10;
    @Autowired
    private BoardService boardService;

    // 게시물 작성 폼
    @GetMapping("/board/write") // localhost:8090/board/write
    public String boardWriteForm() {

        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws IOException {
        System.out.println("제목 : " + board.getTitle());
        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        boardService.write(board, file);

        return "message";
    }

    // http://localhost:8090/board/list?page=1&size=10
    // http://localhost:8090/board/list?searchKeyword=11&page=1
    // http://localhost:8090/board/list?searchKeyword=11
    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page=0, size=MAX_PAGE, sort="id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {

        Page<Board> list;
        if( searchKeyword == null ) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        // TODO: fix page bug. Apply page algorithm
        int nowPage = pageable.getPageNumber()+1 ;
        int pageAxis = (nowPage / MAX_PAGE) * MAX_PAGE;
        int startPage = pageAxis+1;
        int endPage = Math.min(pageAxis+MAX_PAGE, list.getTotalPages())+1;
        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }

    @GetMapping("/board/view") // localhost:8090/board/view?id=1
    public String boardView(Model model, Integer id) {

        model.addAttribute("board", boardService.boardView(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

    // 역슬러시 뒤에 있는 부분이 PathVariable에 맞춰 인식됨
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify";
    }

    // URL을 paramter로 넘기는 2가지 방법 - QueryString, PathVarible
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, MultipartFile file) throws IOException {
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardService.write(boardTemp, file);
        return "redirect:/board/list";
    }
}
