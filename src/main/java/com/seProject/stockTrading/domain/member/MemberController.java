package com.seProject.stockTrading.domain.member;

import com.seProject.stockTrading.domain.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MemberController {

    private final PasswordEncoder passwordEncoder;
    private final MemberServiceImpl memberServiceImpl;
    private final MemberRepository memberRepository;
    // join api
    @CrossOrigin
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberRequestDTO.MemberForm memberForm) {
        log.info("userName: " + memberForm.getUsername());
        if (memberServiceImpl.checkUsername(memberForm.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 있는 id 입니다.");
        }
        if (memberServiceImpl.checkPerson(memberForm.getName(), memberForm.getNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입한 사용자 입니다.");
        }

        String encodedPassword = passwordEncoder.encode(memberForm.getPassword());

        Member newMember = memberServiceImpl.CreateMember(memberForm, memberForm.getPassword());


        memberRepository.save(newMember);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료.");

    }


    @CrossOrigin
    @GetMapping("/memberInfo")
    public ResponseEntity<?> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<Member> memberInfoOptional = memberRepository.findByUsername(username);

        if (memberInfoOptional.isPresent()) {
            return ResponseEntity.ok(memberInfoOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 멤버의 데이터가 존재하지 않아요.");
        }
    }

//    // login api
//    @CrossOrigin
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody MemberDTO imageDTO) {
//        String username = imageDTO.getUsername();
//        String password = imageDTO.getPassword();
//        Optional<Member> memberOptional = memberRepository.findByUsernameAndPassword(username, password);
//        if (memberOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 회원정보입니다.");
//        } else {
//            Member member = memberOptional.get();
//            return ResponseEntity.ok(Collections.singletonMap("id", member.getId()));
//        }
//    }
}
