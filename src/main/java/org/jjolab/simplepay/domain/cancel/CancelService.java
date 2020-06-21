package org.jjolab.simplepay.domain.cancel;

import lombok.AllArgsConstructor;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostDto;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfo;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfoRepository;
import org.jjolab.simplepay.domain.common.PaymentType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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
            cancelResponseDto.getErrorMessages().add("현재 원 결제에 대한 취소건이 진행중 입니다.");
            return;
        }

        cancelPaymentInfo.put(cancelRequestDto.getUuid(), cancelRequestDto.getUuid());
        cancelWithValidation(cancelRequestDto, uuid, cancelResponseDto);
        cancelPaymentInfo.remove(cancelRequestDto.getUuid());
    }

    private void cancelWithValidation(CancelRequestDto cancelRequestDto, String uuid, CancelResponseDto cancelResponseDto) throws Exception {
        CardPostInfo cardPostInfo = cardPostInfoRepository.findByUuid(cancelRequestDto.getUuid());
        if (!Optional.ofNullable(cardPostInfo).isPresent()) {
            cancelResponseDto.getErrorMessages().add("결제 정보를 찾을 수 없습니다.");
            return;
        }

        CardPostDto cardPostDto = new CardPostDto();
        cardPostDto.setDtoByEntity(cardPostInfo);
        CardPostDto.CommonHeader commonHeader = cardPostDto.getCommonHeader();
        CardPostDto.DataInfo dataInfo = cardPostDto.getDataInfo();

        List<CardPostInfo> allByOriginUuid = cardPostInfoRepository.findAllByOriginUuid(cardPostInfo.getUuid());

        Long cancelAmountSum = allByOriginUuid.stream()
                .map(each -> Long.parseLong(each.getPostInfo().substring(63, 73).trim()))
                .reduce(0L, Long::sum);

        if (cancelAmountSum + cancelRequestDto.getCancelAmount() > dataInfo.getAmount()) {
            cancelResponseDto.getErrorMessages().add("총 취소 금액이 결제 금액 보다 많습니다.");
            return;
        }

        Long vatAmountSum = allByOriginUuid.stream()
                .map(each -> Long.parseLong(each.getPostInfo().substring(73, 83).trim()))
                .reduce(0L, Long::sum);

        if (!Optional.ofNullable(cancelRequestDto.getVat()).isPresent()
                && cancelAmountSum + cancelRequestDto.getCancelAmount() == dataInfo.getAmount()) {
            cancelRequestDto.setVat(dataInfo.getVat() - vatAmountSum);
        } else if (!Optional.ofNullable(cancelRequestDto.getVat()).isPresent()) {
            cancelRequestDto.setVat(Math.round((double) cancelRequestDto.getCancelAmount() / 11));
        }

        if (vatAmountSum + cancelRequestDto.getVat() > dataInfo.getVat()) {
            cancelResponseDto.getErrorMessages().add("총 취소 부가세 금액이 총 부가세 보다 많습니다.");
            return;
        }

        if ((cancelAmountSum + cancelRequestDto.getCancelAmount() == dataInfo.getAmount()) &&
                (vatAmountSum + cancelRequestDto.getVat() != dataInfo.getVat())) {
            cancelResponseDto.getErrorMessages().add("취소 금액은 모두 취소 되었으나 부가세가 남았습니다.");
            return;
        }

        if ((cancelAmountSum + cancelRequestDto.getCancelAmount() != dataInfo.getAmount()) &&
                (vatAmountSum + cancelRequestDto.getVat() == dataInfo.getVat())) {
            cancelResponseDto.getErrorMessages().add("부가세 금액은 모두 취소 되었으나 취소금액이 남았습니다.");
            return;
        }

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
