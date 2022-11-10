package com.limemul.easssue.service;

import com.limemul.easssue.api.dto.kwd.KwdDto;
import com.limemul.easssue.api.dto.news.ArticleDto;
import com.limemul.easssue.api.dto.news.KwdArticleDto;
import com.limemul.easssue.api.dto.news.ArticleListDto;
import com.limemul.easssue.entity.*;
import com.limemul.easssue.repo.ArticleKwdRepo;
import com.limemul.easssue.repo.ArticleRepo;
import com.limemul.easssue.repo.RelKwdRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

//    private final UserService userService;
    private final ArticleRepo articleRepo;
    private final RelKwdRepo relKwdRepo;
    private final ArticleKwdRepo articleKwdRepo;
    private static final Integer articlesSize = 6;

    public ArticleListDto getPopularArticle(Integer page){
        LocalDateTime now = LocalDateTime.now();
        log.info("current datetime is {}", now);
        Pageable pageable= PageRequest.of(page, articlesSize);
        Slice<Article> articles = articleRepo.findByPubDateAfterOrderByHitDesc(now.minusDays(1),pageable);

        List<ArticleDto> articleDtoList = articles.stream().map(ArticleDto::new).collect(Collectors.toList());
        return new ArticleListDto(articleDtoList, page, articles.isLast());
    }

    /**
     *
     */
    public List<Article> getPopularArticleExcludeBanKwd(User user,int page){
        
    }

    public KwdArticleDto getSubsArticle(Kwd kwd, Integer page){
        // 연관키워드 리스트
        List<RelKwd> relKwds = relKwdRepo.findAllByFromKwd(kwd);
        List<KwdDto> relKwdDtoList = relKwds.stream().map(KwdDto::new).collect(Collectors.toList());

        // 기사 리스트
        Pageable pageable = PageRequest.of(page, articlesSize);

        Slice<ArticleKwd> articleKwdList = articleKwdRepo.findAllByKwdOrderByCountDescArticleDesc(kwd, pageable);
        List<Article> kwdArticleList=new ArrayList<>();
        for (ArticleKwd articleKwd : articleKwdList) {
            kwdArticleList.add(articleKwd.getArticle());
        }
        List<ArticleDto> articleDtoList = kwdArticleList.stream().map(ArticleDto::new).collect(Collectors.toList());

        return new KwdArticleDto(relKwdDtoList, articleDtoList, page, articleKwdList.isLast());
    }

    public ArticleListDto getRecommendedArticle(Kwd kwd, Integer page){
        // 기사 리스트
        Pageable pageable = PageRequest.of(page, articlesSize);

        Slice<ArticleKwd> articleKwdList = articleKwdRepo.findAllByKwdOrderByCountDescArticleDesc(kwd, pageable);
        List<Article> kwdArticleList=new ArrayList<>();
        for (ArticleKwd articleKwd : articleKwdList) {
            kwdArticleList.add(articleKwd.getArticle());
        }
        List<ArticleDto> articleDtoList = kwdArticleList.stream().map(ArticleDto::new).collect(Collectors.toList());

        return new ArticleListDto(articleDtoList, page, articleKwdList.isLast());
    }

    /**
     * 기사 조회수 올리기
     */
    @Transactional
    public void updateHit(Long articleId) {
        Optional<Article> optionalArticle = articleRepo.findById(articleId);
        if(optionalArticle.isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 기사입니다.");
        }

        Article article = optionalArticle.get();
        article.updateHit();
    }
}
