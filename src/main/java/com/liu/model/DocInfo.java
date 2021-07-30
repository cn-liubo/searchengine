package com.liu.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString

/**
 * 每一个本地的html文件对应一个文档对象
 */
public class DocInfo {
    private Integer id;//类似数据库的主键，识别不同的文档
    private String title;//标题：html文件名作为标题
    private String url;//oracle官网api文档下html的url
    private String content;//网页正文：html(<标签>内容</标签>)，内容为正文

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocInfo docInfo = (DocInfo) o;
        return Objects.equals(id, docInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
