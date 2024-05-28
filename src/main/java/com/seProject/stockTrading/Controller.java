package com.seProject.stockTrading;

import com.seProject.stockTrading.domain.member.Member;
import com.seProject.stockTrading.domain.member.MemberDTO;
import com.seProject.stockTrading.domain.member.MemberRepository;
import com.seProject.stockTrading.domain.member.MemberService;

import com.seProject.stockTrading.domain.post.Post;
import com.seProject.stockTrading.domain.post.PostDTO;
import com.seProject.stockTrading.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class Controller {
    MemberRepository memberRepository;
    MemberService memberService;
    PostRepository postRepository;
    @Autowired
    public Controller(
            PostRepository postRepository,
            MemberRepository memberRepository,
            MemberService memberService){
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.postRepository = postRepository;
    }

 /*   @GetMapping("/upload")
    public String upload(@RequestParam("member_id") Long memberId, Model model) {
        MemberInfo image = new MemberInfo();
        model.addAttribute("image", image);
        model.addAttribute("member_id", memberId);  // member_id 추가
        System.out.println("get 메소드 upload 사용");
        return "upload";  // 파일 이름에서 슬래시 제거
    }
    @PostMapping("/upload")
    public String uploadImage(
                              @RequestParam("member_id")Long memberId,
                              @RequestParam("title")String title,
                              @RequestParam("data") MultipartFile file,
                              @RequestParam("content")String content){
        System.out.println("upload로 들어감.");
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setMember(member);
        memberInfo.setName(file.getOriginalFilename());
        memberInfoRepository.save(memberInfo);

        return "redirect:/index?id=" + memberId;
    }
    @PostMapping("upload/{id}")
    public String fix(@PathVariable Long id,
                      @RequestParam("title")String title,
                      @RequestParam("data")MultipartFile file,
                      @RequestParam("content")String content){
        Optional<MemberInfo> imageOptional = memberInfoRepository.findById(id);
        System.out.println("upload fix 제대로 들어감.");
        if (imageOptional.isPresent()) {
                MemberInfo memberInfo = imageOptional.get();
                memberInfo.setName(file.getOriginalFilename());
                memberInfoRepository.save(memberInfo);
        }
        else{
            System.out.println("fix error");
            return "redirect:/index";
        }
        return "redirect:/index";
    }
*//*    @GetMapping("/images/display/{id}") //image 데이터를 변환해주는
    @ResponseBody
    public ResponseEntity<byte[]> displayImage(@PathVariable Long id) {
        Optional<MemberInfo> imageOptional = memberInfoRepository.findById(id);
        if (imageOptional.isPresent()) {
            MemberInfo memberInfo = imageOptional.get();

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(mimeType)) // 동적 MIME 타입 설정
                    .body(imageData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }*//*
    @GetMapping("/viewer/{id}")
    public String viewer(@PathVariable Long id,Model model){
        Optional<MemberInfo> imageOptional = memberInfoRepository.findById(id);
        if(imageOptional.isPresent()){
            MemberInfo memberInfo = imageOptional.get();
            model.addAttribute("image",memberInfo);
            return "viewer";
        }
        else{
            return "index";
        }
    }
    @GetMapping("/viewer/fix/{id}")
    public String fixButton(@PathVariable Long id,Model model){
        Optional<MemberInfo> imageOptional = memberInfoRepository.findById(id);
        if(imageOptional.isPresent()){
            MemberInfo memberInfo = imageOptional.get();
            model.addAttribute("image",memberInfo);
            return "upload";
        }
        else{
            return "viewer";
        }
    }
    @PostMapping("/viewer/delete/{id}")
    public String delete(@PathVariable Long id){
        Optional<MemberInfo> imageOptional = memberInfoRepository.findById(id);
        if(imageOptional.isPresent()){
            MemberInfo memberInfo = imageOptional.get();
            memberInfoRepository.delete(memberInfo);
            return "redirect:/index";
        }
        else{
            System.out.println("delete 실패");
            return "viewer";
        }
    }*/
/*    @CrossOrigin
    @GetMapping("")
    public String firstPage(){
        return "/login";
    }
    @RequestMapping("/index")
    public String start(Model model, @RequestParam("id") Long memberId) {
        List<Member> images = memberRepository.findAllById(memberId);
        model.addAttribute("images", images);
        return "index";
    }*/
    // 모든 게시물을 list 형태로 가져오는 api
    @CrossOrigin
    @GetMapping("/board")
    public ResponseEntity<?> getBoard(){
        List<Post> postInfo = postRepository.findAllByOrderByCreatedAtDesc();
        if (postInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 없습니다.");
        }
        return ResponseEntity.ok(postInfo);
    }
    @CrossOrigin
    @GetMapping("/board/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id){
        Optional<Post> postInfo = postRepository.findById(id);
        if (postInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 없습니다.");
        }
        return ResponseEntity.ok(postInfo);
    }
    // 멤버 id로 해당 멤버 객체 가져오는 api
    @CrossOrigin
    @GetMapping("/memberInfo/{id}")
    public ResponseEntity<?> getMemberInfo(@PathVariable Long id) {
        Optional<Member> memberInfoOptional = memberRepository.findById(id);
        if (memberInfoOptional.isPresent()) {
            return ResponseEntity.ok(memberInfoOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 멤버의 데이터가 존재하지 않아요.");
        }
    }
    // login api
    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDTO imageDTO) {
        String username = imageDTO.getUsername();
        Long password = imageDTO.getPassword();
        Optional<Member> memberOptional = memberRepository.findByUsernameAndPassword(username, password);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 회원정보입니다.");
        } else {
            Member member = memberOptional.get();
            return ResponseEntity.ok(Collections.singletonMap("id", member.getId()));
        }
    }
    // join api
    @CrossOrigin
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody Member member) {
        if (memberService.checkUsername(member.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 있는 id 입니다.");
        }
        if (memberService.checkPerson(member.getName(), member.getNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입한 사용자 입니다.");
        }
        return new ResponseEntity<>(memberRepository.save(member), HttpStatus.CREATED);
    }
    // 게시글 upload api
    @CrossOrigin
    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody Post post) {
        return new ResponseEntity<>(postRepository.save(post), HttpStatus.CREATED);
    }
    @GetMapping("/join")
    public String joinPage(){
        return "join";
    }
}
