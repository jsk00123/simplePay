package org.jjolab.simplepay.domain.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SearchRequestDto {
    @NotNull
    @Length(max = 20)
    private String uuid;

    public SearchRequestDto(@Length(max = 20) String uuid) {
        this.uuid = uuid;
    }
}
