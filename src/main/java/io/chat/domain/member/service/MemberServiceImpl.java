package io.chat.domain.member.service;

import io.chat.domain.member.dto.LoginRequestDto;
import io.chat.domain.member.dto.MemberSaveRequestDto;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member create(MemberSaveRequestDto memberSaveRequestDto) {

        // 이미 가입 되어 있는 이메일 검증
        if (memberRepository.findByEmail(memberSaveRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Member savedMember = Member.builder()
                .name(memberSaveRequestDto.getName())
                .email(memberSaveRequestDto.getEmail())
                .pw(passwordEncoder.encode(memberSaveRequestDto.getPw()))
                .build();
        Member member = memberRepository.save(savedMember);

        return member;
    }

    @Override
    public Member login(LoginRequestDto loginRequestDto) {

        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(
                        () -> new EntityNotFoundException("존재하지 않는 이메일입니다.")
                );

        if (!passwordEncoder.matches(loginRequestDto.getPw(), member.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
