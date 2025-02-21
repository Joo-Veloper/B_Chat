package io.chat.domain.member.service;

import io.chat.domain.member.dto.LoginRequestDto;
import io.chat.domain.member.dto.MemberListResponseDto;
import io.chat.domain.member.dto.SignupRequestDto;
import io.chat.domain.member.entity.Member;
import io.chat.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member create(SignupRequestDto signupRequestDto) {

        validateEmail(signupRequestDto);

        Member savedMember = Member.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())
                .pw(passwordEncoder.encode(signupRequestDto.getPw()))
                .build();

        return memberRepository.save(savedMember);
    }

    private void validateEmail(SignupRequestDto signupRequestDto) {

        if (memberRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }
    }

    @Override
    public Member login(LoginRequestDto loginRequestDto) {

        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

        validatePassword(loginRequestDto, member);

        return member;
    }

    private void validatePassword(LoginRequestDto loginRequestDto, Member member) {

        if (!passwordEncoder.matches(loginRequestDto.getPw(), member.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public List<MemberListResponseDto> findAll() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(member -> new MemberListResponseDto(
                        member.getId(),
                        member.getName(),
                        member.getEmail()))
                .collect(Collectors.toList());
    }
}
