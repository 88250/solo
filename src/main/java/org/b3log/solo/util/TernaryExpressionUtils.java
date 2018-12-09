package org.b3log.solo.util;

/**
 * 三元表达式工具类.
 *
 * @author chendou
 * @date 2018/12/9
 * @since 1.0
 */
public class TernaryExpressionUtils {


    /**
     * 三元表达式选择
     *
     * @param res
     * @param eSelectOp1
     * @param eSelectOp2
     * @param <T>
     * @return
     */
    public static <T> T expressionSelectOptions(boolean res, T eSelectOp1, T eSelectOp2) {
        return res ? eSelectOp1 : eSelectOp2;
    }
}
