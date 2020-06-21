package org.jjolab.simplepay.domain.search;

import lombok.AllArgsConstructor;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostDto;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfo;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfoRepository;
import org.jjolab.simplepay.utils.MaskingUtil;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class SearchService {
    private CardPostInfoRepository cardPostInfoRepository;

    @Transactional
    public void searchPaymentInfo(SearchRequestDto searchRequestDto, SearchResponseDto searchResponseDto) throws Exception {
        CardPostInfo cardPostInfo = cardPostInfoRepository.findByUuid(searchRequestDto.getUuid());

        if (cardPostInfo == null) {
            searchResponseDto.getErrorMessages().add("uuid 에 해당하는 정보를 찾을 수 없습니다.");
            return;
        }

        CardPostDto cardPostDto = new CardPostDto();
        cardPostDto.setDtoByEntity(cardPostInfo);

        CardPostDto.CommonHeader commonHeader = cardPostDto.getCommonHeader();
        CardPostDto.DataInfo dataInfo = cardPostDto.getDataInfo();

        setSearchResponseDto(searchResponseDto, commonHeader, dataInfo);
    }

    private void setSearchResponseDto(SearchResponseDto searchResponseDto, CardPostDto.CommonHeader commonHeader, CardPostDto.DataInfo dataInfo) {
        searchResponseDto.setUuid(commonHeader.getUuid());
        searchResponseDto.setCdno(MaskingUtil.maskingCdno(dataInfo.getCdno()));
        searchResponseDto.setExpiredDate(dataInfo.getExpiredDate());
        searchResponseDto.setCvc(dataInfo.getCvc());
        searchResponseDto.setPaymentType(commonHeader.getPaymentType());
        searchResponseDto.setAmount(dataInfo.getAmount());
        searchResponseDto.setVat(dataInfo.getVat());
    }
}
