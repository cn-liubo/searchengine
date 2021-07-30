package com.liu.util;

import com.liu.model.DocInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 步骤一：
 * 从本地 api 目录，遍历静态 html 文件
 * 每一个 html 需要构建正排索引：本地某个文件
 * 正文索引信息 List<DocInfo>
 *     DocInfo(id,title,content,url)
 */
public class Parser {

    //api目录
    public static final String LOCAL_PATH = "D:\\docs\\jdk-8u281-docs-all\\api";
    //构建的本地文件正排索引
    public static final String RAW_DATA = "D:\\raw_data1.txt";
    //官方api文档的根路径(拼接本地api路径)
    public static final String API_PATH = "https://docs.oracle.com/javase/8/docs/api";

    public static void main(String[] args) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(RAW_DATA)));
        //找到api本地路径下所有的html文件
        List<File> fileList = listHtml(new File(LOCAL_PATH));
        //针对枚举出来的html文件路径进行遍历, 依次打开每个文件, 并读取内容.把内容转换成需要的结构化的数据(DocInfo对象)
        System.out.println("开始构建正排索引文件");
        long start = System.currentTimeMillis();
        for (File file : fileList) {
            //最终输出的 raw_data 文件是一个行文本文件. 每一行对应一个 html 文件，line 这个对象就对应到一个文件.
            DocInfo docInfo = convertLine(file);
            bw.write(docInfo.getTitle() + '\3' + docInfo.getUrl() + '\3' + docInfo.getContent() + "\n");
        }
        bw.close();
        long end = System.currentTimeMillis();
        System.out.println(end - start + "ms");//15760ms
        System.out.println("正排索引文件构建完成");
    }

    public static DocInfo convertLine(File file) throws IOException {
        DocInfo docInfo = new DocInfo();
        String title = convertTitle(file);
        String url = convertUrl(file);
        String content = convertContent(file);
        docInfo.setTitle(title);
        docInfo.setUrl(url);
        docInfo.setContent(content);
        return docInfo;
    }

    public static String convertTitle(File file) {
        return file.getName().substring(0, file.getName().length() - ".html".length());
    }

    public static String convertUrl(File file) {
        return API_PATH + file.getAbsolutePath().substring(LOCAL_PATH.length());
    }

    public static String convertContent(File file) throws IOException{
        //把html中的标签和换行符去掉
        BufferedReader br = new BufferedReader(new FileReader(file));
        boolean isContent = true;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        //一次读取一个字符
        while ((i = br.read()) != -1) {
            char c = (char) i;
            if (isContent) {
                if (c == '<') {
                    isContent = false;
                    continue;
                } else if (c == '\r' || c == '\n') {
                    sb.append(" ");
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '>') {
                    isContent = true;
                }
            }
        }
        return sb.toString().trim();
    }

    /**
     * 找到这个路径下的所有html文件
     * @param dir
     * @return
     */
    public static List<File> listHtml(File dir) {
        List<File> fileList = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(listHtml(file));
                } else if (file.getName().endsWith(".html")) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }
}
