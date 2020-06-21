package org.jjolab.simplepay.domain.cancel;

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
public class CancelRequestDto {
    @NotNull
    @Length(max = 20, min = 20)
    private String uuid;
    @NotNull
    @Min(value = 100)
    @Max(value = 1000000000)
    private Long cancelAmount;
    @Min(value = 0)
    private Long vat;
}
