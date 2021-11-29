package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    //前缀树
    private class TrieNode{

        private boolean isKeyWordEnd = false;

        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点
        public void addSubNodes(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        public TrieNode getSubNodes(Character c){
            return subNodes.get(c);
        }
    }


    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT = "**";
    //根节点
    private TrieNode rootNode = new TrieNode();

    //初始化一次就行，postConstruct，在spring初始化之后调用该方法
    @PostConstruct
    public void init(){
        try(
                //获取类加载器，在类路径下加载资源，target->classes之下
                InputStream is = this.getClass().getClassLoader()
                        .getResourceAsStream("sensitive-word.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                )
        {
            String keyword;
            while((keyword = reader.readLine())!= null){
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("加载敏感词文件失败"+e.getMessage());
        }

    }

    public void addKeyword(String keyword){
        TrieNode tempNodes = rootNode;
        for(int i = 0;i < keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNodes.getSubNodes(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNodes.addSubNodes(c,subNode);
            }
            tempNodes = subNode;
            if(i == keyword.length() - 1){
                tempNodes.setKeyWordEnd(true);
            }
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        TrieNode tempNodes = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();
        while (begin < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                //指针1处于根节点，将此符号记入结果，让指针2向下走一步
                if (tempNodes == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //无论符号在中间还是在开头，循环都要继续
                position++;
                continue;
            }

            //检查下级节点
            tempNodes = tempNodes.getSubNodes(c);
            if (tempNodes == null) {
                sb.append(text.charAt(begin));
                //进入下一个位置
                position = ++begin;
                tempNodes = rootNode;
            } else if (tempNodes.isKeyWordEnd()) {
                //发现敏感词
                sb.append(REPLACEMENT);
                //指针们进入下一个位置
                begin = ++position;
                tempNodes = rootNode;
            } else {
                // 检查下一个字符
                if (position < text.length() - 1) {
                    position++;
                } else if (position == text.length() - 1) {
                    sb.append(text.charAt(begin));
                    position = ++begin;
                    tempNodes = rootNode;
                }
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    public boolean isSymbol (Character c){
        //0x2E80-0x9FFF是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

}
