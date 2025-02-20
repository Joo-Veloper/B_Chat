package io.chat.domain.member.controller;

import io.chat.domain.member.dto.MemberSaveRequestDto;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveRequestDto memberSaveRequestDto) {

        Member member = memberService.create(memberSaveRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(member);
    }
}
