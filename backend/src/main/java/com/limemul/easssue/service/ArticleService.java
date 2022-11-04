package com.limemul.easssue.service;

import com.limemul.easssue.api.dto.kwd.KwdDto;
import com.limemul.easssue.api.dto.news.ArticleDto;
import com.limemul.easssue.api.dto.news.KwdArticleDto;
import com.limemul.easssue.api.dto.news.PopularArticleDto;
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

import static com.limemul.easssue.jwt.JwtProvider.getUserFromJwt;


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

    public PopularArticleDto getPopularArticle(Integer page){
        LocalDateTime now = LocalDateTime.now();
        log.info("current datetime is {}", now);
        Pageable pageable= PageRequest.of(page, articlesSize);
        Slice<Article> articles = articleRepo.findByPubDateAfterOrderByHitDesc(now.minusDays(1),pageable);
//        List<Long> banKwdList = new ArrayList<>();

//        List<Long> banKwdList = new ArrayList<>();
//        banKwdList.add(Long.valueOf(1));
//        banKwdList.add(Long.valueOf(2));
//        banKwdList.add(Long.valueOf(3));
//        banKwdList.add(Long.valueOf(17191));
//        banKwdList.add(Long.valueOf(6561));

//        Slice<Article> articles = articleRepo.findByArticleAndKwdOrderByHit(banKwdList, pageable);
        List<ArticleDto> articleDtoList = articles.stream().map(ArticleDto::new).collect(Collectors.toList());
        return new PopularArticleDto(articleDtoList, page, articles.isLast());


    }

    public KwdArticleDto getSubsArticle(Kwd kwd, Integer page){


        // 연관키워드 리스트
        List<RelKwd> relKwds = relKwdRepo.findAllByFromKwd(kwd);
        List<KwdDto> relKwdDtoList = relKwds.stream().map(KwdDto::new).collect(Collectors.toList());


        // 기사 리스트
//        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(page, articlesSize);

        Slice<ArticleKwd> articleKwdList = articleKwdRepo.findAllByKwdOrderByArticleDesc(kwd, pageable);
        List<Article> kwdArticleList=new ArrayList<>();
        for (ArticleKwd articleKwd : articleKwdList) {
            kwdArticleList.add(articleKwd.getArticle());
        }
        List<ArticleDto> articleDtoList = kwdArticleList.stream().map(ArticleDto::new).collect(Collectors.toList());

        return new KwdArticleDto(relKwdDtoList, articleDtoList, page, articleKwdList.isLast());
    }

}