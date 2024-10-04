package com.seProject.stockTrading.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final PasswordEncoder passwordEncoder;
    private final MemberServiceImpl memberServiceImpl;
    private final MemberRepository memberRepository;
    // join api
    @CrossOrigin
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberRequestDTO.MemberForm memberForm) {
        if (memberServiceImpl.checkUsername(memberForm.getUserName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 있는 id 입니다.");
        }
        if (memberServiceImpl.checkPerson(memberForm.getName(), memberForm.getNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입한 사용자 입니다.");
        }

        String encodedPassword = passwordEncoder.encode(memberForm.getPassword());

        Member newMember = memberServiceImpl.CreateMember(memberForm, encodedPassword);

        memberRepository.save(newMember);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료.");

    }
}
