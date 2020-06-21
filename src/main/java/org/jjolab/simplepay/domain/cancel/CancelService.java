package org.jjolab.simplepay.domain.cancel;

import lombok.AllArgsConstructor;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostDto;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfo;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfoRepository;
import org.jjolab.simplepay.domain.common.PaymentType;
import org.jjolab.simplepay.domain.common.StaticValues;
import org.jjolab.simplepay.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class CancelService {
    private CardPostInfoRepository cardPostInfoRepository;
    @Qualifier(value = "cancelPaymentInfo")
    ConcurrentHashMap<String, String> cancelPaymentInfo;

    @Transactional
    public void cancel(CancelRequestDto cancelRequestDto, String uuid, CancelResponseDto cancelResponseDto) throws Exception {
        if (cancelPaymentInfo.contains(cancelRequestDto.getUuid())) {
            cancelResponseDto.getErrorMessages().add(StaticValues.ALREADY_PAYMENT_CANCELING);
            return;
        }

        try {
            cancelPaymentInfo.put(cancelRequestDto.getUuid(), cancelRequestDto.getUuid());
            cancelWithValidation(cancelRequestDto, uuid, cancelResponseDto);
        } finally {
            cancelPaymentInfo.remove(cancelRequestDto.getUuid());
        }
    }

    private void cancelWithValidation(CancelRequestDto cancelRequestDto, String uuid, CancelResponseDto cancelResponseDto) throws Exception {
        CardPostInfo cardPostInfo = cardPostInfoRepository.findByUuid(cancelRequestDto.getUuid());
        if (CommonUtil.isEmpty(cardPostInfo)) {
            cancelResponseDto.getErrorMessages().add(StaticValues.PAYMENTINFO_NOT_FOUND);
            return;
        }

        CardPostDto cardPostDto = new CardPostDto();
        cardPostDto.setDtoByEntity(cardPostInfo);
        CardPostDto.CommonHeader commonHeader = cardPostDto.getCommonHeader();
        CardPostDto.DataInfo dataInfo = cardPostDto.getDataInfo();

        List<CardPostInfo> allByOriginUuid = cardPostInfoRepository.findAllByOriginUuid(cardPostInfo.getUuid());
        Long cancelAmountSum = getAllCancelAmount(allByOriginUuid);
        Long vatAmountSum = getAllVatAmount(allByOriginUuid);

        setVatAmountWithCondition(cancelRequestDto, dataInfo, cancelAmountSum, vatAmountSum);

        if (checkValidation(cancelRequestDto, cancelResponseDto, dataInfo, cancelAmountSum, vatAmountSum)) {
            return;
        }

        setDataAndSave(cancelRequestDto, uuid, cancelResponseDto, cardPostDto, commonHeader, dataInfo);
    }

    private Long getAllCancelAmount(List<CardPostInfo> allByOriginUuid) {
        return allByOriginUuid.stream()
                .map(each -> Long.parseLong(each.getPostInfo().substring(63, 73).trim()))
                .reduce(0L, Long::sum);
    }

    private Long getAllVatAmount(List<CardPostInfo> allByOriginUuid) {
        return allByOriginUuid.stream()
                .map(each -> Long.parseLong(each.getPostInfo().substring(73, 83).trim()))
                .reduce(0L, Long::sum);
    }

    private void setVatAmountWithCondition(CancelRequestDto cancelRequestDto, CardPostDto.DataInfo dataInfo, Long cancelAmountSum, Long vatAmountSum) {
        if (CommonUtil.isEmpty(cancelRequestDto.getVat())
                && cancelAmountSum + cancelRequestDto.getCancelAmount() == dataInfo.getAmount()) {
            cancelRequestDto.setVat(dataInfo.getVat() - vatAmountSum);
        } else if (CommonUtil.isEmpty(cancelRequestDto.getVat())) {
            cancelRequestDto.setVat(Math.round((double) cancelRequestDto.getCancelAmount() / 11));
        }
    }

    private boolean checkValidation(CancelRequestDto cancelRequestDto, CancelResponseDto cancelResponseDto, CardPostDto.DataInfo dataInfo, Long cancelAmountSum, Long vatAmountSum) {
        if (cancelAmountSum + cancelRequestDto.getCancelAmount() > dataInfo.getAmount()) {
            cancelResponseDto.getErrorMessages().add(StaticValues.CANCEL_AMOUNT_BIGGER_THAN_PAYMENT_AMOUNT);
            return true;
        }

        if (vatAmountSum + cancelRequestDto.getVat() > dataInfo.getVat()) {
            cancelResponseDto.getErrorMessages().add(StaticValues.CANCEL_VAT_BIGGER_THAN_PAYMENT_VAT);
            return true;
        }

        if ((cancelAmountSum + cancelRequestDto.getCancelAmount() == dataInfo.getAmount()) &&
                (vatAmountSum + cancelRequestDto.getVat() != dataInfo.getVat())) {
            cancelResponseDto.getErrorMessages().add(StaticValues.REMAIN_VAT_AFTER_ALL_CANCEL_AMOUNT);
            return true;
        }

        if ((cancelAmountSum + cancelRequestDto.getCancelAmount() != dataInfo.getAmount()) &&
                (vatAmountSum + cancelRequestDto.getVat() == dataInfo.getVat())) {
            cancelResponseDto.getErrorMessages().add(StaticValues.REMAIN_CANCEL_AMOUNT_ALL_CANCEL_VAT);
            return true;
        }
        return false;
    }

    private void setDataAndSave(CancelRequestDto cancelRequestDto, String uuid, CancelResponseDto cancelResponseDto, CardPostDto cardPostDto, CardPostDto.CommonHeader commonHeader, CardPostDto.DataInfo dataInfo) {
        commonHeader.setPaymentType(PaymentType.CANCEL);
        String originUuid = commonHeader.getUuid();
        commonHeader.setUuid(uuid);

        dataInfo.setAmount(cancelRequestDto.getCancelAmount());
        dataInfo.setVat(cancelRequestDto.getVat());
        dataInfo.setOrginUUID(originUuid);

        cardPostInfoRepository.save(cardPostDto.toEntity(originUuid));

        cancelResponseDto.setUuid(uuid);
    }
}
