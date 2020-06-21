package org.jjolab.simplepay.domain.payment;

import lombok.AllArgsConstructor;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostDto;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfo;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfoRepository;
import org.jjolab.simplepay.domain.common.PaymentType;
import org.jjolab.simplepay.domain.common.StaticValues;
import org.jjolab.simplepay.utils.CryptoUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class PaymentService {
    private CardPostInfoRepository cardPostInfoRepository;
    @Qualifier(value = "paymentCardInfo")
    private ConcurrentHashMap<String, String> paymentCardInfo;

    @Transactional
    public void payment(PaymentRequestDto paymentRequestDto, String uuid, PaymentResponseDto paymentResponseDto) throws Exception {
        if (paymentCardInfo.contains(paymentRequestDto.getCdno())) {
            paymentResponseDto.getErrorMessages().add(StaticValues.ALREADY_CDNO_USE);
            return;
        }

        paymentCardInfo.put(paymentRequestDto.getCdno(), paymentRequestDto.getCdno());
        paymentWithValidation(paymentRequestDto, uuid, paymentResponseDto);
        paymentCardInfo.remove(paymentRequestDto.getCdno());
    }

    private void paymentWithValidation(PaymentRequestDto paymentRequestDto, String uuid, PaymentResponseDto paymentResponseDto) throws Exception {
        if (paymentRequestDto.getVat() != null && paymentRequestDto.getPaymentAmount() < paymentRequestDto.getVat()) {
            paymentResponseDto.getErrorMessages().add(StaticValues.VAT_BIGGER_AMOUNT_ERROR);
            return;
        }

        CardPostDto cardPostDto = setCardPostInfo(paymentRequestDto, uuid);
        CardPostInfo cardPostInfo = cardPostDto.toEntity("");

        cardPostInfoRepository.save(cardPostInfo);

        paymentResponseDto.setUuid(uuid);
    }

    private CardPostDto setCardPostInfo(PaymentRequestDto paymentRequestDto, String uuid) throws Exception {
        CardPostDto cardPostDto = new CardPostDto();
        CardPostDto.CommonHeader commonHeader = cardPostDto.getCommonHeader();
        commonHeader.setDataLen(446);
        commonHeader.setPaymentType(PaymentType.PAYMENT);
        commonHeader.setUuid(uuid);

        CardPostDto.DataInfo dataInfo = cardPostDto.getDataInfo();
        dataInfo.setCdno(paymentRequestDto.getCdno());
        dataInfo.setDivisionPayDate(paymentRequestDto.getDivisionPayDate());
        dataInfo.setCvc(paymentRequestDto.getCvc());
        dataInfo.setExpiredDate(paymentRequestDto.getExpiredDate());
        dataInfo.setAmount(paymentRequestDto.getPaymentAmount());
        dataInfo.setVat(
                Optional.ofNullable(paymentRequestDto.getVat())
                        .orElse(Math.round((double) paymentRequestDto.getPaymentAmount() / 11)));
        dataInfo.setOrginUUID(String.format("%20s", ""));
        dataInfo.setEncryptCardInfo(
                CryptoUtil.encryptAES256(paymentRequestDto.getCdno() + "|"
                                + paymentRequestDto.getExpiredDate() + "|"
                                + paymentRequestDto.getCvc()
                        , "key"));
        dataInfo.setReservedField(String.format("%47s", ""));

        return cardPostDto;
    }
}
