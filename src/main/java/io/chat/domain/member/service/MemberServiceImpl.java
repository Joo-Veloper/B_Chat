package io.chat.domain.member.service;

import io.chat.domain.member.dto.MemberSaveRequestDto;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public Member create(MemberSaveRequestDto memberSaveRequestDto) {

        // 이미 가입 되어 있는 이메일 검증
        if (memberRepository.findByEmail(memberSaveRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Member newMember = Member.builder()
                .name(memberSaveRequestDto.getName())
                .email(memberSaveRequestDto.getEmail())
                .pw(memberSaveRequestDto.getPw())
                .build();
        Member member = memberRepository.save(newMember);

        return member;
    }
}
