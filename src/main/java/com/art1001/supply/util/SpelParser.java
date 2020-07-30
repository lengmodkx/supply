package com.art1001.supply.util;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelParser {

    /**
     *
     * @param key el表达式字符串，占位符以#开头
     * @param parameterNames  形参名称，可以理解为占位符名称
     * @param args   参数值，可以理解为占位符真实的值
     * @return  返回el表达式经过参数替换后的字符串值
     */
    public static String getKey(String key, String[] parameterNames, Object[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        //（第一步）将key字符串解析为el表达式
        Expression exp = parser.parseExpression(key);
        //（第二步）将形参和形参值以配对的方式配置到赋值上下文中
        EvaluationContext context = new StandardEvaluationContext();//初始化赋值上下文
        if(args.length<=0){
            return null;
        }
        for (int i =0;i<args.length;i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        //（第三步）根据赋值上下文运算el表达式
        return exp.getValue(context, String.class);
    }
}
