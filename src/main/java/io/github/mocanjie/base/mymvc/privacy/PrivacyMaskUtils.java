package io.github.mocanjie.base.mymvc.privacy;

public class PrivacyMaskUtils {

    private PrivacyMaskUtils() {}

    public static String mask(String value, Privacy privacy) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        PrivacyType type = privacy.type();
        char maskChar = privacy.maskChar();

        if (type == PrivacyType.EMAIL) {
            return maskEmail(value, maskChar);
        }
        if (type == PrivacyType.CUSTOM) {
            int left = privacy.left();
            int right = privacy.right();
            double percent = privacy.percent();
            if (left >= 0 || right >= 0) {
                return maskByPosition(value, Math.max(left, 0), Math.max(right, 0), maskChar);
            }
            if (percent > 0) {
                return maskByPercent(value, percent, maskChar);
            }
            // 兜底：遮蔽中间 50%
            return maskByPercent(value, 0.5, maskChar);
        }
        // 预设策略
        return maskByPosition(value, type.left, type.right, maskChar);
    }

    public static String maskByPosition(String value, int left, int right, char maskChar) {
        int len = value.length();
        int maskStart = Math.min(left, len);
        int maskEnd = Math.max(len - right, maskStart);
        if (maskStart >= maskEnd) {
            return value;
        }
        StringBuilder sb = new StringBuilder(len);
        sb.append(value, 0, maskStart);
        for (int i = maskStart; i < maskEnd; i++) {
            sb.append(maskChar);
        }
        sb.append(value, maskEnd, len);
        return sb.toString();
    }

    public static String maskByPercent(String value, double percent, char maskChar) {
        int len = value.length();
        int maskLen = (int) Math.round(len * percent);
        if (maskLen <= 0) {
            return value;
        }
        int maskStart = (len - maskLen) / 2;
        int maskEnd = maskStart + maskLen;
        return maskByPosition(value, maskStart, len - maskEnd, maskChar);
    }

    public static String maskEmail(String value, char maskChar) {
        int atIdx = value.indexOf('@');
        if (atIdx <= 0) {
            return value;
        }
        String local = value.substring(0, atIdx);
        String domain = value.substring(atIdx);
        String maskedLocal = maskByPercent(local, 0.5, maskChar);
        return maskedLocal + domain;
    }
}
