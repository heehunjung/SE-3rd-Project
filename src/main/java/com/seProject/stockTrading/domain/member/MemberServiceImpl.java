package com.seProject.stockTrading.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member CreateMember(MemberRequestDTO.MemberForm memberForm, String encodedPassword){
        return Member.builder().
                name(memberForm.getName()).
                password(encodedPassword).
                nickname(memberForm.getNickname()).
                username(memberForm.getUserName()).
                number(memberForm.getNumber()).
                build();
    }

    @Override
    public boolean checkUsername(String username){
        Optional<Member> foundMember = memberRepository.findByUsername(username);
        return foundMember.isPresent();
    }

    @Override
    public boolean checkPerson(String name, Long number) {
        List<Member> foundMembers = memberRepository.findByName(name);
        for (Member member : foundMembers) {
            if (member.getNumber().equals(number)) {
                return true; // 이름과 번호가 모두 일치하는 회원이 있으면 true 반환
            }
        }
        return false; // 일치하는 회원이 없으면 false 반환
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
