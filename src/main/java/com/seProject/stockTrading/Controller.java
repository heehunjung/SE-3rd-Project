package com.seProject.stockTrading;

import com.seProject.stockTrading.domain.commets.Comment;
import com.seProject.stockTrading.domain.commets.CommentRepository;
import com.seProject.stockTrading.domain.member.Member;
import com.seProject.stockTrading.domain.member.MemberDTO;
import com.seProject.stockTrading.domain.member.MemberRepository;
import com.seProject.stockTrading.domain.member.MemberService;

import com.seProject.stockTrading.domain.post.Post;
import com.seProject.stockTrading.domain.post.PostDTO;
import com.seProject.stockTrading.domain.post.PostRepository;
import com.seProject.stockTrading.domain.post.PostService;
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
    PostService postService;
    CommentRepository commentRepository;
    @Autowired
    public Controller(
            PostService postService,
            PostRepository postRepository,
            MemberRepository memberRepository,
            MemberService memberService,
            CommentRepository commentRepository){
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.postRepository = postRepository;
        this.postService = postService;
        this.commentRepository = commentRepository;
    }

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
    @CrossOrigin
    @GetMapping("/getComment/{postId}")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return ResponseEntity.ok(comments);
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
    // 게시글 delete api
    @CrossOrigin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean isDeleted = postService.deletePost(id);
            if (isDeleted) {
                return ResponseEntity.ok().body("게시글이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }
    // 게시글 edit api
    @CrossOrigin
    @PutMapping("/post/{postId}")
    public ResponseEntity<?> edit(@PathVariable Long postId,@RequestBody Post post) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                // 기존 엔티티의 ID를 설정하여 객체를 업데이트
                post.setId(postId);
                postRepository.save(post);
                return ResponseEntity.ok().body("게시글이 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 오류가 발생했습니다.");
        }
    }
    // 댓글 upload api
    @CrossOrigin
    @PostMapping("/postComment")
    public ResponseEntity<?> uploadComment(@RequestBody Comment comment) {
        return new ResponseEntity<>(commentRepository.save(comment),HttpStatus.CREATED);
    }
    //조회수 +1 update api
    @CrossOrigin
    @PutMapping("/viewCount/{postId}")
    public ResponseEntity<?> incrementViewCount(@PathVariable Long postId) {
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                post.setView(post.getView() + 1);
                postRepository.save(post);
                return ResponseEntity.ok(post);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("조회수 증가 중 오류가 발생했습니다.");
        }
    }
/*    //상승률,하락률를 계산하여 table에 넣는 api
    @CrossOrigin
    @PostMapping("/changes")
    public ResponseEntity<?> calculateChanges()*/
/*    @CrossOrigin
    @GetMapping("/stockData")
    public ResponseEntity<?> getStockData() {

    }*/
}
