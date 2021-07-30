package com.liu.util;

import com.liu.model.DocInfo;
import com.liu.model.Weight;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 构建索引：
 * 正排索引：从本地文件数据中读取到java文件（类似数据库保存的数据）
 * 倒排索引：构建Map<String, List<信息>（类似数据库hash索引）
 * Map键：关键词（分词来做）
 * Map值-信息：
 * （1）docInfo对象引用或时docInfo的id，权重
 * （2）权重（标题对应关键词数量*10+正文对应关键词数量*1）
 * （3）关键词（是否需要，待定）
 */
public class Index {

    // 索引类需要包含两方面的内容. 正排索引, 倒排索引
    // 正排索引是 docId => DocInfo 直接把 docId 作为数组下标就行了
    public static List<DocInfo> forwardIndex = new ArrayList<>();
    // 倒排索引 词 => 一组 docId
    // 不光能得到每个词在哪些文档中出现过, 还想知道这个词在该文档中的权重是多少
    public static Map<String, List<Weight>> invertedIndex = new HashMap<>();

    /*
        构建正排索引的内容：从本地raw_data.txt从读取并保存
     */
    public static void buildForwardIndex() {
        try {
            FileReader fr = new FileReader(Parser.RAW_DATA);
            BufferedReader br = new BufferedReader(fr);
            int id = 0;//行号设置为DocInfo的id
            String line = null;
            long start = System.currentTimeMillis();
            while ((line = br.readLine()) != null) {
                if (line.trim().equals("")) continue;
                //一行对应一个DocInfo对象，类型数据库一行数据对应Java对象
                DocInfo docInfo = new DocInfo();
                docInfo.setId(++id);
                String[] parts = line.split("\3");
                docInfo.setTitle(parts[0]);
                docInfo.setUrl(parts[1]);
                docInfo.setContent(parts[2]);
//                if (parts[0].contains("�")) {
//                    System.out.println("title=========[url: " + docInfo.getUrl() + "]");
//                }
//                if (parts[1].contains("�")) {
//                    System.out.println("title=========[url: " + docInfo.getUrl() + "]");
//                }
                forwardIndex.add(docInfo);
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start + "ms");
        } catch (IOException e) {
            //不要吃异常，初始化操作有异常尽量往外抛，让线程不捕获异常从而结束程序
            //初始化（启动tomcat），有异常，尽早暴露问题
            throw new RuntimeException();
        }
    }

    //构建倒排索引：从内存中正排索引获取文档信息来构建
    public static void buildInvertedIndex() {
        for (DocInfo docInfo : forwardIndex) {//doc + 分词 对应 weight（doc和分词一对多，分词和weight一对一）
            //一个doc，分别对标题和正文分词，每一个分词生成weight对象，需要计算权重值
            //如标题为：清华大学/计算机/专业/使用/计算机/炒菜
            //第一次出现的关键词，要new Weight对象，之后出现相同分词关键词时，
            //要获取之前已经拿到的相同关键词weight对象，再更新权重（把自己的权限加进去）
            //实现逻辑：先构建一个HashMap，保存分词（key）和weight对象（value）
            Map<String, Weight> cache = new HashMap<>();
            List<Term> titleFenCis = ToAnalysis.parse(docInfo.getTitle()).getTerms();
            for (Term titleFenCi : titleFenCis) {//标题分词并遍历处理
//                if (titleFenCi.getName().contains("�")) {
//                    System.out.println("title fenci===========[url: " + docInfo.getUrl() + "]");
//                }
                Weight weight = cache.get(titleFenCi.getName());//获取标题分词键对应的weight
                if (weight == null) {//如果没有，就创建一个并放到map中
                    weight = new Weight();
                    weight.setDocInfo(docInfo);
                    weight.setKeyword(titleFenCi.getName());
                    cache.put(titleFenCi.getName(), weight);
                }
                //标题分词权重 + 10
                weight.setWeight(weight.getWeight() +10);
            }
            //正文的分词处理：逻辑和标题分词逻辑一样
            List<Term> contentFenCis = ToAnalysis.parse(docInfo.getContent()).getTerms();
            for (Term contentFenCi : contentFenCis) {//正文分词并遍历处理
//                if (contentFenCi.getName().contains("�")) {
//                    System.out.println("title fenci===========[url: " + docInfo.getUrl() + "]");
//                }
                Weight weight = cache.get(contentFenCi.getName());
                if (weight == null) {
                    weight = new Weight();
                    weight.setDocInfo(docInfo);
                    weight.setKeyword(contentFenCi.getName());
                    cache.put(contentFenCi.getName(), weight);
                }
                //正文分词，权重 + 1
                weight.setWeight(weight.getWeight() + 1);
            }

            //把临时保存的map数据（keyword-weight）全部保存到倒排索引中
            for (Map.Entry<String, Weight> entry : cache.entrySet()) {
                String keyword = entry.getKey();
                Weight weight = entry.getValue();
                //更新保存到倒排索引Map<String, List<Weight>> --> 多个文档，同一个关键词，保存在一个list
                //现在倒排索引中，通过keyword获取已有的值
                List<Weight> weights = invertedIndex.get(keyword);
                if (weights == null) {////如果拿不到，就创建一个，并存放进倒排索引
                    weights = new ArrayList<>();
                    invertedIndex.put(keyword, weights);
                }
                weights.add(weight);//倒排中，添加当前文档每个分词对应的weight对象
            }
        }
    }

    //通过关键词（分词）在倒排中查找映射等待文档（多个文档，倒排拉链）
    public static List<Weight> get(String keyword) {
        return invertedIndex.get(keyword);
    }

    public static void main(String[] args) {
        Index.buildForwardIndex();
        Index.buildInvertedIndex();
        System.out.println("构建完成");

//        List<Weight> list = Index.get("arraylist");
//        for (Weight weight : list) {
//            System.out.println(weight.getDocInfo().getTitle());
//            System.out.println(weight.getDocInfo().getUrl());
//            System.out.println(weight.getKeyword());
//            System.out.println(weight.getWeight());
//            System.out.println("=============================");
//        }

//        for (Map.Entry<String,List<Weight>> e : invertedIndex.entrySet()) {
//            String keyword = e.getKey();
//            System.out.print(keyword+": ");
//            List<Weight> weights = e.getValue();
//            weights.stream()
//                    .map(w->{//map操作：把list中每一个对应转换为其他对象
//                        return "（"+w.getDocInfo().getId()+", "+w.getWeight()+"）";
//                    })//转换完，会变成List<String>
////                    .collect(Collectors.toList());//返回List<String>
//                    .forEach(System.out::print);
//            System.out.println();
//        }
    }
}
