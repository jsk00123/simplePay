package org.jjolab.simplepay.domain.search;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.jjolab.simplepay.domain.common.PaymentType;
import org.jjolab.simplepay.domain.common.ResponseDto;

@Getter
@Setter
@NoArgsConstructor
public class SearchResponseDto extends ResponseDto {
    private String cdno;
    private String expiredDate;
    private String cvc;

    private PaymentType paymentType;

    private Long amount;
    private Long vat;

    @Builder
    public SearchResponseDto(@Length(max = 20) String uuid,
                             String cdno,
                             String expiredDate,
                             String cvc,
                             PaymentType paymentType,
                             Long amount,
                             Long vat) {
        this.uuid = uuid;
        this.cdno = cdno;
        this.expiredDate = expiredDate;
        this.cvc = cvc;
        this.paymentType = paymentType;
        this.amount = amount;
        this.vat = vat;
    }
}
