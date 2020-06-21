package org.jjolab.simplepay.domain.search;

import lombok.AllArgsConstructor;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostDto;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfo;
import org.jjolab.simplepay.domain.cardPostInfo.CardPostInfoRepository;
import org.jjolab.simplepay.domain.common.StaticValues;
import org.jjolab.simplepay.utils.CommonUtil;
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

        if (CommonUtil.isEmpty(cardPostInfo)) {
            searchResponseDto.getErrorMessages().add(StaticValues.UUID_NOT_FOUND);
            return;
        }

        CardPostDto cardPostDto = new CardPostDto();
        cardPostDto.setDtoByEntity(cardPostInfo);

        setSearchResponseDto(searchResponseDto, cardPostDto.getCommonHeader(), cardPostDto.getDataInfo());
    }

    private void setSearchResponseDto(SearchResponseDto searchResponseDto,
                                      CardPostDto.CommonHeader commonHeader, CardPostDto.DataInfo dataInfo) {
        searchResponseDto.setUuid(commonHeader.getUuid());
        searchResponseDto.setCdno(MaskingUtil.maskingCdno(dataInfo.getCdno()));
        searchResponseDto.setExpiredDate(dataInfo.getExpiredDate());
        searchResponseDto.setCvc(dataInfo.getCvc());
        searchResponseDto.setPaymentType(commonHeader.getPaymentType());
        searchResponseDto.setAmount(dataInfo.getAmount());
        searchResponseDto.setVat(dataInfo.getVat());
    }
}
