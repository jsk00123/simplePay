package org.jjolab.simplepay.domain.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRequestDto {
    @NotNull
    @Length(min = 10, max = 16)
    private String cdno;
    @NotNull
    @Length(min = 4, max = 4)
    private String expiredDate;
    @NotNull
    @Length(min = 3, max = 3)
    private String cvc;
    @NotNull
    @Min(value = 0)
    @Max(value = 12)
    private Integer divisionPayDate = 0;
    @NotNull
    @Min(value = 100)
    @Max(value = 1000000000)
    private Long paymentAmount = 0L;
    @Min(value = 0)
    private Long vat;
}
