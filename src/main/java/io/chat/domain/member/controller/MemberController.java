package io.chat.domain.member.controller;

import io.chat.domain.member.dto.LoginRequestDto;
import io.chat.domain.member.dto.MemberSaveRequestDto;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.service.MemberService;
import io.chat.global.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveRequestDto memberSaveRequestDto) {

        Member member  = memberService.create(memberSaveRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(member.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginRequestDto loginRequestDto) {

        Member member = memberService.login(loginRequestDto);

        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> loginInfo = new HashMap<>();

        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loginInfo);
    }
}
