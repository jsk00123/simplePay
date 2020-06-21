package org.jjolab.simplepay.domain.cardPostInfo;

import lombok.*;
import org.jjolab.simplepay.domain.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "card_post_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class CardPostInfo extends BaseEntity {
    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "originUuid")
    private String originUuid;

    @Column(name = "postInfo", length = 450)
    private String postInfo;

    @Builder
    public CardPostInfo(String uuid, String originUuid, String postInfo) {
        this.uuid = uuid;
        this.originUuid = originUuid;
        this.postInfo = postInfo;
    }
}
