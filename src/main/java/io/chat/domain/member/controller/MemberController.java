package io.chat.domain.member.controller;

import io.chat.domain.member.dto.LoginRequestDto;
import io.chat.domain.member.dto.LoginResponseDto;
import io.chat.domain.member.dto.MemberListResponseDto;
import io.chat.domain.member.dto.SignupRequestDto;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.service.MemberService;
import io.chat.global.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Long> memberCreate(@RequestBody SignupRequestDto signupRequestDto) {

        Member member  = memberService.create(signupRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(member.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> doLogin(@RequestBody LoginRequestDto loginRequestDto) {

        Member member = memberService.login(loginRequestDto);

        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

        LoginResponseDto loginResponseDto = new LoginResponseDto(
                member.getId(),
                member.getName(),
                member.getEmail(),
                jwtToken
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loginResponseDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<MemberListResponseDto>> memberList() {

        List<MemberListResponseDto> dtos = memberService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dtos);
    }
}
