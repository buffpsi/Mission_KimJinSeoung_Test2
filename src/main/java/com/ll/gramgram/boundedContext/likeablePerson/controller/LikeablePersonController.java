package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            List<LikeablePerson> likeablePeople = likeablePersonService.findByFromInstaMemberId(instaMember.getId());
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }

    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeablePerson/add";
    }

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        RsData<LikeablePerson> createRsData = likeablePersonService.like(rq.getMember(), addForm.getUsername(), addForm.getAttractiveTypeCode());

        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", createRsData);
    }


//1차 목표의 메서드
//    @GetMapping("delete/{id}")
//    public String deleteLikeablePerson(@PathVariable Long id) {
//        likeablePersonService.deleteLikeablePerson(id);
//        return "redirect:/likeablePerson/list";
//    }

    //2차목표 + 3차 목표 메시지 까지 같이 출력해주는 형태로 만들기
//    @GetMapping("delete/{id}")
//    public String deleteLikeablePerson(@PathVariable Integer id) {
//        RsData<LikeablePerson> likeablePersonRsData = likeablePersonService.delete(id);
//        if (likeablePersonRsData == null) {
//            return rq.historyBack("데이터를 찾을 수 없습니다.");
//        } else if (likeablePersonRsData.isSuccess()) {
//            return rq.redirectWithMsg("/likeablePerson/list", "삭제 완료하였습니다!");
//        } else {
//            return rq.historyBack(likeablePersonRsData);
//        }
//    }
    @GetMapping("/delete/{id}")
    public String deleteLikeablePerson(@PathVariable Integer id){
        RsData<LikeablePerson> likeablePersonRsData = this.likeablePersonService.delete(id);
        //데이터값이 없을 수가있을까?에대해서 한번 더 생각해봐야함 하지만 혹시나 하는 모든 예외처리 케이스를 다 적어놓았다
        if (likeablePersonRsData == null){
            return rq.historyBack("데이터를 찾을 수 없습니다");
        } else if (likeablePersonRsData.isSuccess()) { //삭제를 성공했다면 메시지를 출력할 수 있도록 하였다
            return rq.redirectWithMsg("/likeablePerson/list", "삭제 완료!");
        }
        else { //실패한 경우에 뒤로 돌아가도록 정의했다.
            return rq.historyBack(likeablePersonRsData);
        }
    }
}
