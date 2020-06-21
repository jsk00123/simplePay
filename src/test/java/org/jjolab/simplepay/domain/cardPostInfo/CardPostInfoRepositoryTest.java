package org.jjolab.simplepay.domain.cardPostInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CardPostInfoRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private CardPostInfoRepository cardPostInfoRepository;

    @Test
    public void findOne() {
        // given
        CardPostInfo cardPostInfo = new CardPostInfo();
        cardPostInfo.setUuid("eb28b5c6f3ff4288afe1");
        CardPostInfo saveCardPostInfo = testEntityManager.persist(cardPostInfo);

        // when
        Optional<CardPostInfo> findAuth = cardPostInfoRepository.findById(saveCardPostInfo.getUuid());

        // then
        assertThat(findAuth).isPresent();
    }
}