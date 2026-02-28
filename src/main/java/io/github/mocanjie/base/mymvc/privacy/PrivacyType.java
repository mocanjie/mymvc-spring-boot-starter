package io.github.mocanjie.base.mymvc.privacy;

public enum PrivacyType {
    /** 手机号：保留前3后4，如 138****8888 */
    PHONE(3, 4),
    /** 姓名：保留首尾各1位，如 张*明 */
    NAME(1, 1),
    /** 邮箱：特殊处理，仅遮蔽本地部分中间，如 zh***@qq.com */
    EMAIL(-1, -1),
    /** 身份证：保留前6后4，如 110101****2345 */
    ID_CARD(6, 4),
    /** 银行卡：仅保留后4位，如 ****1234 */
    BANK_CARD(0, 4),
    /** 地址：保留前6位，如 北京市朝阳区**** */
    ADDRESS(6, 0),
    /** 自定义：使用注解的 left/right/percent 属性 */
    CUSTOM(-1, -1);

    final int left;
    final int right;

    PrivacyType(int left, int right) {
        this.left = left;
        this.right = right;
    }
}
