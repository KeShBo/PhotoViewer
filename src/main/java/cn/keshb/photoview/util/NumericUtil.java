package cn.keshb.photoview.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumericUtil {

    public static double retain(double number, int n) {
        return BigDecimal.valueOf(number).setScale(n, RoundingMode.HALF_UP).doubleValue();
    }
}
