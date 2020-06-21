package org.jjolab.simplepay.domain.cardPostInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jjolab.simplepay.domain.common.PaymentType;
import org.jjolab.simplepay.utils.CryptoUtil;

@Getter
@Setter
@NoArgsConstructor
public class CardPostDto {
    CommonHeader commonHeader = new CommonHeader();
    DataInfo dataInfo = new DataInfo();

    public CardPostInfo toEntity(String originUuid) {
        return CardPostInfo.builder()
                .uuid(this.commonHeader.getUuid())
                .originUuid(originUuid)
                .postInfo(this.toString())
                .build();
    }

    public void setDtoByEntity(CardPostInfo cardPostInfo) throws Exception {
        String postInfo = cardPostInfo.getPostInfo();

        CommonHeader commonHeader = this.getCommonHeader();

        commonHeader.setDataLen(Integer.parseInt(postInfo.substring(0, 4).trim()));
        commonHeader.setPaymentType(PaymentType.valueOf(postInfo.substring(4, 14).trim()));
        commonHeader.setUuid(postInfo.substring(14, 34).trim());

        DataInfo dataInfo = this.getDataInfo();

        String decrypted = CryptoUtil.decryptAES256(postInfo.substring(103, 403).trim(), "key");
        String[] decryptedCardInfo = decrypted.split("\\|");

        dataInfo.setCdno(decryptedCardInfo[0]);
        dataInfo.setDivisionPayDate(Integer.parseInt(postInfo.substring(54, 56).trim()));
        dataInfo.setExpiredDate(decryptedCardInfo[1]);
        dataInfo.setCvc(decryptedCardInfo[2]);
        dataInfo.setAmount(Long.parseLong(postInfo.substring(63, 73).trim()));
        dataInfo.setVat(Long.parseLong(postInfo.substring(73, 83).trim()));
        dataInfo.setOrginUUID(postInfo.substring(83, 103).trim());
        dataInfo.setEncryptCardInfo(postInfo.substring(103, 403).trim());
    }

    @Override
    public String toString() {
        return this.commonHeader.toString() + this.dataInfo.toString();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommonHeader {
        // 데이터길이, 4자리, 숫자
        private Integer dataLen;
        // 데이터구분, 10자리, 문자
        private PaymentType paymentType;
        // 관리번호, 20자리, 문자
        private String uuid;

        @Override
        public String toString() {
            return String.format("%4d" +
                            "%10s" +
                            "%20s",
                    dataLen,
                    paymentType.toString(),
                    uuid);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DataInfo {
        // 카드번호, 20자리, 숫자(L)
        private String cdno;
        // 할부개월수, 2자리, 숫자(0)
        private Integer divisionPayDate;
        // 유효기간, 4자리, 숫자(L)
        private String expiredDate;
        // cvc, 3자리, 숫자(L)
        private String cvc;
        // 거래금액, 10자리, 숫자
        private Long amount;
        // 부가가치세, 10자리, 숫자(0)
        private Long vat;
        // 원거래 관리번호 (취소시에만 결제 관리번호 저장)
        private String orginUUID;
        // 암호화된 카드정보, 300자리, 문자
        private String encryptCardInfo;
        // 예비, 47자리, 문자
        private String reservedField;

        @Override
        public String toString() {
            return String.format("%-20s" +
                            "%02d" +
                            "%-4s" +
                            "%-3s" +
                            "%10d" +
                            "%010d" +
                            "%20s" +
                            "%300s" +
                            "%47s",
                    cdno,
                    divisionPayDate,
                    expiredDate,
                    cvc,
                    amount,
                    vat,
                    orginUUID,
                    encryptCardInfo,
                    reservedField);
        }
    }
}
