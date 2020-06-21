package org.jjolab.simplepay.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskingUtil {
    /*
     * 카드번호 masking utill
     * 출처 : https://m.blog.naver.com/PostView.nhn?blogId=dress07&logNo=221147496529&proxyReferer=https:%2F%2Fwww.google.com%2F
     * */
    public static String maskingCdno(String str) {
        StringBuffer replaceString = new StringBuffer(str);
        Matcher matcher = Pattern.compile("^(\\d{6})(\\d{7,14})(\\d{3})$").matcher(str);

        if (matcher.matches()) {
            replaceString = new StringBuffer();

            boolean isHyphen = false;
            if (str.contains("-")) {
                isHyphen = true;
            }

            for (int i = 1; i <= matcher.groupCount(); i++) {
                String replaceTarget = matcher.group(i);
                if (i == 2) {
                    char[] c = new char[replaceTarget.length()];
                    Arrays.fill(c, '*');

                    replaceString.append(String.valueOf(c));
                } else {
                    replaceString.append(replaceTarget);
                }

                if (isHyphen && i < matcher.groupCount()) {
                    replaceString.append("-");
                }
            }
        }

        return replaceString.toString();
    }
}
