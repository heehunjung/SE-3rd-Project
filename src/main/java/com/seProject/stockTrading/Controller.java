package com.seProject.stockTrading;

import com.seProject.stockTrading.domain.commets.Comment;
import com.seProject.stockTrading.domain.commets.CommentRepository;
import com.seProject.stockTrading.domain.dto.*;
import com.seProject.stockTrading.domain.member.Member;
import com.seProject.stockTrading.domain.member.MemberRepository;
import com.seProject.stockTrading.domain.member.MemberService;
import com.seProject.stockTrading.domain.member_stock.MemberStock;
import com.seProject.stockTrading.domain.member_stock.MemberStockRepository;
import com.seProject.stockTrading.domain.post.Post;
import com.seProject.stockTrading.domain.post.PostRepository;
import com.seProject.stockTrading.domain.post.PostService;
import com.seProject.stockTrading.domain.stock.Stock;
import com.seProject.stockTrading.domain.stock.StockRepository;
import com.seProject.stockTrading.domain.stockPrice.StockPrice;
import com.seProject.stockTrading.domain.stockPrice.StockPriceRepository;
import com.seProject.stockTrading.domain.tradeRecord.TradeRecord;
import com.seProject.stockTrading.domain.tradeRecord.TradeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class Controller {
    private final HttpEncodingAutoConfiguration httpEncodingAutoConfiguration;
    private final MemberStockRepository memberStockRepository;
    private final TradeRecordRepository tradeRecordRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PostRepository postRepository;
    private final PostService postService;
    private final CommentRepository commentRepository;
    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;



    // 모든 게시물을 list 형태로 가져오는 api
    @CrossOrigin
    @GetMapping("/board")
    public ResponseEntity<?> getBoard() {
        List<Post> postInfo = postRepository.findAllByOrderByCreatedAtDesc();
        if (postInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 없습니다.");
        }
        return ResponseEntity.ok(postInfo);
    }

    // 특정 게시물을 가져오는 api
    @CrossOrigin
    @GetMapping("/board/{id}")
    public ResponseEntity<?> getBoard(@PathVariable Long id) {
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

    // stock_price 를 stock_id로 가져오는 api
    @CrossOrigin
    @GetMapping("/stockData/yesterDay/{stockId}")
    public ResponseEntity<?> getStockPriceToday(@PathVariable Long stockId) {
        Optional<StockPrice> stockPrice = stockPriceRepository.findTop1ByStockIdOrderByDateDesc(stockId);
        if (stockPrice.isPresent()) {
            return ResponseEntity.ok(stockPrice.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주식 데이터가 존재하지 않아요.");
        }
    }

    // 해당 게시글의 댓글을 리스트 형태로 가져오는 api
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
        String password = imageDTO.getPassword();
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
        Member savedMember = memberRepository.save(member);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료.");    }

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
    public ResponseEntity<?> edit(@PathVariable Long postId, @RequestBody Post post) {
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
        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);
    }

    // 조회수 +1 update api
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

    // 상승률,하락률를 계산하여 table에 넣는 api
    @CrossOrigin
    @GetMapping("/changes/{stockId}")
    public ResponseEntity<?> calculate(@PathVariable Long stockId) {
        Optional<Stock> currentStockOpt = stockRepository.findById(stockId);
        if (currentStockOpt.isEmpty()) {
            return ResponseEntity.status(404).body("주식 정보가 없습니다.");
        }

        Optional<StockPrice> currentPriceOpt = stockPriceRepository.findTop1ByStockIdOrderByDateDesc(stockId);
        if (currentPriceOpt.isEmpty()) {
            return ResponseEntity.status(404).body("주식 가격 정보가 없습니다.");
        }

        StockPrice currentPrice = currentPriceOpt.get();

        LocalDate yesterday = currentPrice.getDate().minusDays(1);
        Optional<StockPrice> pastPriceOpt = stockPriceRepository.findTop1ByStockIdAndDateLessThanOrderByDateDesc(stockId, yesterday);
        if (pastPriceOpt.isEmpty()) {
            return ResponseEntity.status(404).body("하루 전 주식 가격 정보가 없습니다.");
        }

        StockPrice pastPrice = pastPriceOpt.get();

        float change = (currentPrice.getClosingPrice() - pastPrice.getClosingPrice()) / pastPrice.getClosingPrice();
        return ResponseEntity.ok(change);
    }

    // Stock table 정보를 요청하는 api
    @CrossOrigin
    @GetMapping("/stockData")
    public ResponseEntity<?> getStockData() {
        List<Stock> StockData = stockRepository.findAll();
        if (StockData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("주식 정보가 없습니다.");
        }
        return ResponseEntity.ok(StockData);
    }

    // 특정 Stock 정보를 전달하는 api
    @CrossOrigin
    @GetMapping("/stockData/{stockId}")
    public ResponseEntity<?> getStockData(@PathVariable Long stockId) {
        Optional<Stock> stockOpt = stockRepository.findById(stockId);
        if (stockOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주식 정보가 없습니다.");
        } else {
            return ResponseEntity.ok(stockOpt.get());
        }
    }

    // 특정 Stock Price 정보를 전달하는 api
    @CrossOrigin
    @GetMapping("/stockPrice/{stockId}")
    public ResponseEntity<?> getStockPrice(@PathVariable Long stockId) {
        List<StockPrice> stockPrices = stockPriceRepository.findAllByStockId(stockId);
        return ResponseEntity.ok(stockPrices);
    }

    // Stock id로 Stock 찾는 api
    @CrossOrigin
    @GetMapping("/stockData/name/{stockName}")
    public ResponseEntity<?> getStockData(@PathVariable String stockName) {
        Optional<Stock> stockOpt = stockRepository.findAllByStockName(stockName);
        if (stockOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주식 정보가 없습니다.");
        } else {
            return ResponseEntity.ok(stockOpt.get());
        }
    }

    // 닉네임 중복 체크를 위한 api
    @CrossOrigin
    @GetMapping("/nickname/{nickName}")
    public ResponseEntity<?> getNickname(@PathVariable String nickName) {
        Optional<Member> memberOpt = memberRepository.findByNickname(nickName);
        if (memberOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용가능한 닉네임입니다.");
        } else {
            return ResponseEntity.ok("중복된 닉네임입니다.");
        }
    }

    // 매수 API
    @CrossOrigin
    @PostMapping("/buy/{id}")
    public ResponseEntity<?> buy(@PathVariable Long id, @RequestBody MemberStockDTO memberStockDTO) {
        Optional<MemberStock> memberStock = memberStockRepository.findByMemberIdAndStockId(id, memberStockDTO.getStockId());
        Optional<Stock> stock = stockRepository.findById(memberStockDTO.getStockId());
        if (stock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주식 정보가 없습니다.");
        }
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 멤버 정보가 없습니다.");
        }
        Long currentBalance = member.get().getBalance();
        Long totalCost = (long) (memberStockDTO.getStockQuantity() * stock.get().getCurrentPrice());
        if (currentBalance < totalCost) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잔액이 부족합니다.");
        }

        member.get().setBalance(currentBalance - totalCost);
        if (memberStock.isEmpty()) {
            // 생성
            MemberStock memberStockObj = new MemberStock();
            memberStockObj.setQuantity(memberStockDTO.getStockQuantity());
            memberStockObj.setCreatedAt(Timestamp.valueOf(LocalDateTime.now())); // 현재 시간 설정
            memberStockObj.setMember(member.get());
            memberStockObj.setStock(stock.get());
            memberStockObj.setStockName(stock.get().getStockName());
            memberStockRepository.save(memberStockObj);
        } else {
            // 수정
            Long tempQuantity = memberStock.get().getQuantity();
            memberStock.get().setQuantity(memberStockDTO.getStockQuantity() + tempQuantity);
            memberStock.get().setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            memberStock.get().setStockName(stock.get().getStockName());
            memberStockRepository.save(memberStock.get());
        }

        // 거래 기록 추가
        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setMember(member.get());
        tradeRecord.setStock(stock.get());
        tradeRecord.setQuantity(Math.toIntExact(memberStockDTO.getStockQuantity()));
        tradeRecord.setPrice(stock.get().getCurrentPrice());
        tradeRecord.setType("BUY");
        tradeRecord.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        tradeRecordRepository.save(tradeRecord);

        return new ResponseEntity<>("매수에 성공하였습니다.", HttpStatus.CREATED);
    }

    // 매도 api
    @CrossOrigin
    @PostMapping("/sell/{id}")
    public ResponseEntity<?> sell(@PathVariable Long id, @RequestBody MemberStockDTO memberStockDTO) {
        Optional<MemberStock> memberStock = memberStockRepository.findByMemberIdAndStockId(id, memberStockDTO.getStockId());
        if (memberStock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 주식을 보유하고 있지 않습니다.");
        }
        Optional<Stock> stock = stockRepository.findById(memberStockDTO.getStockId());
        if (stock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주식 정보가 없습니다.");
        }
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 멤버 정보가 없습니다.");
        }
        Long currentQuantity = memberStock.get().getQuantity();
        if (currentQuantity < memberStockDTO.getStockQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("보유 주식보다 큰 수량입니다.");
        }

        Long currentBalance = member.get().getBalance();
        Long totalCost = (long) (memberStockDTO.getStockQuantity() * stock.get().getCurrentPrice());
        member.get().setBalance(currentBalance + totalCost);
        memberStock.get().setQuantity(currentQuantity - memberStockDTO.getStockQuantity());
        memberStock.get().setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        memberStock.get().setStockName(stock.get().getStockName());
        memberStockRepository.save(memberStock.get());

        // 거래 기록 추가
        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setMember(member.get());
        tradeRecord.setStock(stock.get());
        tradeRecord.setQuantity(Math.toIntExact(memberStockDTO.getStockQuantity()));
        tradeRecord.setPrice(stock.get().getCurrentPrice());
        tradeRecord.setType("SELL");
        tradeRecord.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        tradeRecordRepository.save(tradeRecord);
        return ResponseEntity.ok("매도에 성공하였습니다.");
    }

    // 관심종목 api
    @CrossOrigin
    @PostMapping("/interestedStock/{id}")
    public ResponseEntity<?> interestedStock(@PathVariable Long id, @RequestBody StockLikeDTO stockLikeDTO) {
        Optional<MemberStock> memberStockOpt = memberStockRepository.findByMemberIdAndStockId(id,stockLikeDTO.getStockId());
        Optional<Stock> stock = stockRepository.findById(stockLikeDTO.getStockId());
        if (stock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 주식 정보가 없습니다.");
        }
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 멤버 정보가 없습니다.");
        }
        if (memberStockOpt.isPresent()) {
            if (stockLikeDTO.getLike() == 0) {
                memberStockOpt.get().setIsPreferred(0);
            } else {
                memberStockOpt.get().setIsPreferred(1);
            }
            memberStockOpt.get().setStock(stock.get());
            memberStockOpt.get().setMember(member.get());
            memberStockOpt.get().setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            memberStockOpt.get().setStockName(stock.get().getStockName());
            memberStockRepository.save(memberStockOpt.get());
            return ResponseEntity.ok("관심 종목 등록에 성공하였습니다.");
        } else {
            MemberStock memberStockObj = new MemberStock();
            if (stockLikeDTO.getLike() == 0) {
                memberStockObj.setIsPreferred(0);
            } else {
                memberStockObj.setIsPreferred(1);
            }
            memberStockObj.setStock(stock.get());
            memberStockObj.setMember(member.get());
            memberStockObj.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            memberStockObj.setStockName(stock.get().getStockName());
            memberStockRepository.save(memberStockObj);
            return ResponseEntity.ok("관심 종목 등록에 성공하였습니다.");
        }
    }

    // 관심 종목을 LIST 형태로 제공하는 api
    @CrossOrigin
    @GetMapping("interestedStock/{id}")
    public ResponseEntity<?> interestedStock(@PathVariable Long id) {
        List<MemberStock> memberStockOpt = memberStockRepository.findByIsPreferredAndMemberId(1, id);
        if (memberStockOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("선호 종목이 없습니다.");
        }
        return ResponseEntity.ok(memberStockOpt);
    }

    // memberStock List 가져오는 api
    @CrossOrigin
    @GetMapping("/memberStock/{id}")
    public ResponseEntity<?> getMemberStocks(@PathVariable Long id) {
        List<MemberStock> memberStocks = memberStockRepository.findAllByMemberId(id);
        if (memberStocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 정보가 없습니다.");
        }
        return ResponseEntity.ok(memberStocks);
    }

    // memberStock 객체를 가져오는 api by stockId
    @CrossOrigin
    @GetMapping("/memberStock/stockId/{stockId}")
    public ResponseEntity<?> getMemberStocksByStockId(@PathVariable Long stockId, @RequestParam(required = false) Long memberId) {
        Optional<MemberStock> memberStockOpt = memberStockRepository.findByMemberIdAndStockId(memberId, stockId);
        if (memberStockOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(memberStockOpt.get());
        }
    }

    // 거래 기록을 가져오는 API
    @CrossOrigin
    @GetMapping("/tradeRecords/{memberId}")
    public ResponseEntity<?> getTradeRecords(@PathVariable Long memberId, @RequestParam(required = false) Long stockId) {
        List<TradeRecord> tradeRecords = tradeRecordRepository.findAllByMemberIdAndStockIdOrderByTimestampDesc(memberId, stockId);
        if (tradeRecords.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("거래 기록이 없습니다.");
        }
        return ResponseEntity.ok(tradeRecords);
    }

    // 모든 멤버 정보를 가져오는 API 추가
    @CrossOrigin
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보가 없습니다.");
        }
        return ResponseEntity.ok(members);
    }

    // 멤버 잔액 수정 API 추가
    @CrossOrigin
    @PutMapping("/members/{id}/balance")
    public ResponseEntity<?> updateMemberBalance(@PathVariable Long id, @RequestParam Long amount) {
        Optional<Member> memberOptional = memberRepository.findById(id);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            member.setBalance(member.getBalance() + amount);
            memberRepository.save(member);
            return ResponseEntity.ok().body("잔액이 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 멤버의 데이터가 존재하지 않아요.");
        }
    }
    @CrossOrigin
    @DeleteMapping("/members/{userId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long userId) {
        try {
            // 연관된 TradeRecord 삭제
            List<TradeRecord> tradeRecords = tradeRecordRepository.findAllByMemberId(userId);
            tradeRecordRepository.deleteAll(tradeRecords);

            // 연관된 MemberStock 삭제
            List<MemberStock> memberStocks = memberStockRepository.findAllByMemberId(userId);
            memberStockRepository.deleteAll(memberStocks);

            // 연관된 Post 삭제
            List<Post> posts = postRepository.findAllByMemberId(userId);
            postRepository.deleteAll(posts);

            // 마지막으로 Member 삭제
            memberRepository.deleteById(userId);

            return ResponseEntity.ok("멤버가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 시 문제가 발생했습니다: " + e.getMessage());
        }
    }
    // 모든 게시글 제목과 작성자를 반환하는 api
    @CrossOrigin
    @GetMapping("/title/username")
    public ResponseEntity<?> getPostNameMember() {
        List<Post> postInfo = postRepository.findAllByOrderByCreatedAtDesc();
        if (postInfo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 없습니다.");
        }

        List<MemberPostDTO> memberPostDTOList = new ArrayList<>();

        for (Post post : postInfo) {
            Optional<Member> memberOpt = memberRepository.findById(post.getMember().getId());
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                MemberPostDTO memberPostDTO = new MemberPostDTO(post.getTitle(), member.getUsername(), member.getNickname(), post.getId());
                memberPostDTOList.add(memberPostDTO);
            }
        }
        return ResponseEntity.ok(memberPostDTOList);
    }
    // admin에서 주식을 삭제하는 api
    @CrossOrigin
    @DeleteMapping("/stocks/{stockId}")
    public ResponseEntity<?> deleteStock(@PathVariable Long stockId) {
        try {
            // 먼저 연관된 StockPrice 삭제
            List<StockPrice> stockPrices = stockPriceRepository.findAllByStockId(stockId);
            stockPriceRepository.deleteAll(stockPrices);

            // 그 다음 연관된 TradeRecord 삭제
            List<TradeRecord> tradeRecords = tradeRecordRepository.findAllByStockId(stockId);
            tradeRecordRepository.deleteAll(tradeRecords);

            // 연관된 MemberStock 삭제
            List<MemberStock> memberStocks = memberStockRepository.findAllByStockId(stockId);
            memberStockRepository.deleteAll(memberStocks);

            // 마지막으로 Stock 삭제
            stockRepository.deleteById(stockId);

            return ResponseEntity.ok("주식이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 시 문제가 발생했습니다: " + e.getMessage());
        }
    }
    // 관심 종목 선택이 가장 많이 된 5개 주식을 return
    @CrossOrigin
    @GetMapping("/memberStock/topPreferred")
    public ResponseEntity<?> getTop5PreferredStocks() {
        Pageable topFive = PageRequest.of(0, 5);  // 첫 번째 페이지의 5개의 항목
        List<StockDTO> topPreferredStocks = memberStockRepository.findTop5PreferredStocks(topFive);
        if (topPreferredStocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 정보가 없습니다.");
        }
        return ResponseEntity.ok(topPreferredStocks);
    }
    // 랜덤 주식 api
    @CrossOrigin
    @GetMapping("/stockData/random")
    public ResponseEntity<?> getRandomStockData() {
        List<Stock> stockData = stockRepository.findAll();
        if (stockData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("주식 정보가 없습니다.");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(stockData.size());
        Stock randomStock = stockData.get(randomIndex);

        return ResponseEntity.ok(randomStock);
    }
}
