package top.nextor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MoneyToChinese {

    private static final Pattern PATTERN = Pattern.compile("[0-9]{1,20}(\\.[0-9]+)?");

    private static final String[] DIGIT = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] UNITS = {"元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾", "佰", "仟", "京", "拾", "佰", "仟"};
    private static final String[] DOT_UNITS = {"分", "角"};
    private static final String WHOLE = "整";

    public static String to(String value) {
        final Matcher matcher = PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new RuntimeException("长度越界或格式错误！");
        }

        final String[] parts = value.split("\\.");
        final String integer = parts[0];
        final String decimal = parts.length > 1 ? parts[1] : "";

        final StringBuilder builder = new StringBuilder("人民币");
        //整数部分
        boolean isZeroLastTime = false;
        boolean beenZero = true;
        int zeroTimes = 0;
        final int integerLength = integer.length();
        for (int i = 0; i < integerLength; i++) {
            final char ch = integer.charAt(i);
            final int numIdx = ch - 48;
            final int unitIdx = integerLength - i - 1;
            final boolean isZero = numIdx == 0;
            final boolean isLevel = unitIdx % 4 == 0;
            final boolean isLevelLastTime = (unitIdx + 1) % 4 == 0;

            //排除一直是0的情况
            if (beenZero && isZero) {
                continue;
            }

            if (!isZero) {
                //补0
                if ((isZeroLastTime && !isLevelLastTime) || zeroTimes == 4) {
                    builder.append(DIGIT[0]);
                }
                builder.append(DIGIT[numIdx % DIGIT.length])
                        .append(UNITS[unitIdx % UNITS.length]);

                beenZero = false;
                zeroTimes = 0;
            } else {
                zeroTimes++;
                //数级起点补单位
                if ((isLevel && zeroTimes < 4) || unitIdx == 0) {
                    builder.append(UNITS[unitIdx % UNITS.length]);
                }
            }

            isZeroLastTime = isZero;
        }

        //前面都是0处理
        if (beenZero) {
            builder.append(DIGIT[0]).append(UNITS[0]);
        }

        //小数部分
        isZeroLastTime = false;
        beenZero = true;
        final int decimalLength = Math.min(decimal.length(), DOT_UNITS.length);
        for (int i = 0; i < decimalLength; i++) {
            final char ch = decimal.charAt(i);
            final int numIdx = ch - 48;
            final int unitIdx = DOT_UNITS.length - i - 1;
            final boolean isZero = numIdx == 0;

            if (!isZero) {
                //补0
                if (isZeroLastTime) {
                    builder.append(DIGIT[0])
                            .append(DOT_UNITS[(unitIdx + 1) % DOT_UNITS.length]);
                }
                builder.append(DIGIT[numIdx % DIGIT.length])
                        .append(DOT_UNITS[unitIdx % DOT_UNITS.length]);

                beenZero = false;
            }
            isZeroLastTime = isZero;
        }

        //补整
        if (beenZero) {
            builder.append(WHOLE);
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        System.out.println(to("10000000000000000009.99"));
    }
}
