package org.jjolab.simplepay.domain.common;

public class StaticValues {
    public static final int DATA_LEN = 446;

    public static final String CRYPTO_KEY = "key";

    public static final String ALREADY_CDNO_USE = "카드번호가 결제에 사용중 입니다.";
    public static final String VAT_BIGGER_AMOUNT_ERROR = "부가가치세는 결제 금액 보다 클 수 없습니다.";
    public static final String UUID_NOT_FOUND = "uuid 에 해당하는 정보를 찾을 수 없습니다.";
    public static final String ALREADY_PAYMENT_CANCELING = "현재 원 결제에 대한 취소건이 진행중 입니다.";
    public static final String PAYMENTINFO_NOT_FOUND = "결제 정보를 찾을 수 없습니다.";
    public static final String CANCEL_AMOUNT_BIGGER_THAN_PAYMENT_AMOUNT = "총 취소 금액이 결제 금액 보다 많습니다.";
    public static final String CANCEL_VAT_BIGGER_THAN_PAYMENT_VAT = "총 취소 부가세 금액이 총 부가세 보다 많습니다.";
    public static final String REMAIN_VAT_AFTER_ALL_CANCEL_AMOUNT = "취소 금액은 모두 취소 되었으나 부가세가 남았습니다.";
    public static final String REMAIN_CANCEL_AMOUNT_ALL_CANCEL_VAT = "부가세 금액은 모두 취소 되었으나 취소금액이 남았습니다.";
}
