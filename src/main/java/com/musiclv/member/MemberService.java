package com.musiclv.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(SignupForm form) {
        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        Member member = Member.of(
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                form.getName(),
                form.getPhone(),
                form.getAddress(),
                Role.USER
        );
        return memberRepository.save(member).getId();
    }

    public Member getById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. id=" + id));
    }

    public boolean isEmailTaken(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void updateProfile(Long memberId, String name, String phone, String address) {
        getById(memberId).updateProfile(name, phone, address);
    }
}
