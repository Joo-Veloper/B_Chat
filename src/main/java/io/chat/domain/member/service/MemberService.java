package io.chat.domain.member.service;


import io.chat.domain.member.dto.MemberSaveRequestDto;
import io.chat.domain.member.entity.Member;

public interface MemberService {
    Member create(MemberSaveRequestDto memberSaveRequestDto);

}
