package io.chat.domain.member.service;


import io.chat.domain.member.dto.LoginRequestDto;
import io.chat.domain.member.dto.MemberListResponseDto;
import io.chat.domain.member.dto.SignupRequestDto;
import io.chat.domain.member.entity.Member;

import java.util.List;

public interface MemberService {

    Member create(SignupRequestDto signupRequestDto);

    Member login(LoginRequestDto loginRequestDto);

    List<MemberListResponseDto> findAll();
}
