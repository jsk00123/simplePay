package org.jjolab.simplepay.domain.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class ResponseDto {
    @Length(max = 20)
    protected String uuid;
    private List<String> errorMessages = new ArrayList<>();
}
