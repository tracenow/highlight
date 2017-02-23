package cn.trace.nlp.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

/**
 * @author trace
 * 
 */
public class Highlighter {

    public static String highlight(String source, Set<String> patterns) {
        return highlight(source, patterns, "<font color='red'>", "</font>");
    }
    
    /**
     * 按关键词长度优先不重叠高亮处理
     * 
     * @param source
     * @param patterns
     * @param preTag
     * @param postTag
     * @return
     */
    public static String highlight(String source, Set<String> patterns, String preTag, String postTag) {
        StringBuilder result = new StringBuilder();
        if(StringUtils.isNotBlank(source) && CollectionUtils.isNotEmpty(patterns)
                && StringUtils.isNotBlank(preTag) && StringUtils.isNotBlank(postTag)) {
            TrieBuilder builder = Trie.builder().caseInsensitive();
            for(String pattern : patterns) {
                builder.addKeyword(pattern);
            }
            Trie trie = builder.build();
            List<Emit> emits = (List<Emit>) trie.parseText(source);
            Collections.sort(emits, new Comparator<Emit>(){

                @Override
                public int compare(Emit o1, Emit o2) {
                    return o2.getKeyword().length() - o1.getKeyword().length();
                }
                
            });
            boolean[] boolArray = new boolean[source.length()];
            for(Emit emit : emits) {
                int start = emit.getStart();
                int end = emit.getEnd();
                if(!(boolArray[start] || boolArray[end])) {
                    for(int i = start; i <= end; i++) {
                        boolArray[i] = true;
                    }
                }
            }
            boolean flag = false;
            for(int i = 0; i < source.length(); i++) {
                boolean bool = boolArray[i];
                if(bool && !flag) {
                    result.append(preTag).append(source.charAt(i));
                    flag = true;
                } else if(!bool && flag) {
                    result.append(postTag).append(source.charAt(i));
                    flag = false;
                } else {
                    result.append(source.charAt(i));
                }
            }
            if(flag) {
                result.append(postTag);
            }
            return result.toString();
        }
        return source;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        String source = "https://github.com";
        Set<String> patterns = Sets.newHashSet("git", "com");
        System.out.println(Highlighter.highlight(source, patterns));
    }
}
