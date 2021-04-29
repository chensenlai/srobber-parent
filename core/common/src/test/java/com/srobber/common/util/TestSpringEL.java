package com.srobber.common.util;

import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StopWatch;

/**
 * SpringEL性能测试
 *
 * @author chensenlai
 * 2020-10-23 下午1:10
 */
public class TestSpringEL {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Test
    public void test() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Expression expression = parser.parseExpression("#name");
        StopWatch stopWatch = new StopWatch("test");
        stopWatch.start("springEL");
        for(int i=0; i<100000; i++) {
            context.setVariable("name", "chensenlai"+i);
            expression.getValue(context, String.class);
        }
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }
}
