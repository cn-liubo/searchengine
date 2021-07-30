package com.liu.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liu.model.Result;
import com.liu.model.Weight;
import com.liu.util.Index;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

//根据前端路径，定义后端服务路径
//loadOnStartup属性表示是否在启动时初始化，默认-1启动不初始化，第一次请求初始化
@WebServlet(value = "/search", loadOnStartup = 0)
public class SearcherServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        //初始化工作：先构建正排索引，再根据正排构建倒排
        Index.buildForwardIndex();
        Index.buildInvertedIndex();
        System.out.println("init complete!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");//ajax请求，响应json格式

        //构造返回给前端的内容：使用对象，之后再序列化位json字符串
        Map<String, Object> map = new HashMap<>();
        //解析请求数据
        String query = req.getParameter("query");
        List<Result> results = new ArrayList<>();
        try {
            //校验请求数据：搜索内容
            if (query == null && query.length() == 0) {
                map.put("ok", false);
                map.put("msg", "搜索内容为空");
            } else {
                //根据搜索内容进行分词，遍历每个分词
                for (Term term : ToAnalysis.parse(query).getTerms()) {
                    String fenCi = term.getName();
                    List<Weight> weights = Index.get(fenCi);
                    //将一个文档转换为一个Result（不同分词可能存在相同的文档，需要合并）
                    for (Weight weight : weights) {
                        Result result = new Result();
                        result.setId(weight.getDocInfo().getId());
                        result.setTitle(weight.getDocInfo().getTitle());
                        result.setWeight(weight.getWeight());
                        result.setUrl(weight.getDocInfo().getUrl());
                        //文档内容超过60个长度，隐藏位...
                        String content = weight.getDocInfo().getContent();
                        result.setDesc(content.length() <= 60 ? content : content.substring(0, 60) + "...");
                        //合并操作需要在List<Result>：
                        // 1.找已有的，判断docId相同，直接在已有的Result权重加上现有的
                        // 2.不存在，直接放进去
                        int isExist = results.indexOf(result.getId());
                        if (isExist == -1) {
                            results.add(result);
                        } else {
                            Result r = results.get(isExist);
                            r.setWeight(result.getWeight() + r.getWeight());
                        }
//                        results.add(result);
                    }
                }
                //对Lis<Result>进行排序，权重降序排序
                results.sort(new Comparator<Result>() {
                    @Override
                    public int compare(Result o1, Result o2) {
                        return Integer.compare(o2.getWeight(), o1.getWeight());//权重降序
                    }
                });

                map.put("ok", true);
                map.put("data", results);
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("ok", false);
            map.put("msg", "未知的错误");
        }
        //获取输出流
        PrintWriter pw = resp.getWriter();
        //设置响应体内容：map对象序列化位json字符串
        pw.println(new ObjectMapper().writeValueAsString(map));
    }
}
