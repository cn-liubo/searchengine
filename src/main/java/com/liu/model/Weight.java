package com.liu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
/**
 * 倒排索引Map<String, List<Weight>>中，关键词对应的信息
 */
public class Weight {

    private String keyword;//关键词
    private DocInfo docInfo;
    private int weight;//权重值，通过标题和正文中，关键词的数量计算

}