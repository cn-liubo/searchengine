package com.liu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Result {
    //合并文档，排序用
    private Integer id;//docInfo的id，文档合并时，文档
    private int weight;//权重：同一个文档合并后，权限相加，再排序
    //返回给前端用
    private String title;//docInfo的标题
    private String url;//docInfo的url
    private String desc;//docInfo的content（超长时，截取指定长度）

}
