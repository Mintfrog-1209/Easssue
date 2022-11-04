package com.limemul.easssue.api.dto.news;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PopularArticleDto {

    // 인기 기사 리스트
    private List<ArticleDto> newsList;

    private Integer page;

    private Boolean last;


}
