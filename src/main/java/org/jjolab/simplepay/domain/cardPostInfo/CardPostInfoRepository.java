package org.jjolab.simplepay.domain.cardPostInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardPostInfoRepository extends JpaRepository<CardPostInfo, String> {
    CardPostInfo findByUuid(String uuid);

    List<CardPostInfo> findAllByOriginUuid(String uuid);
}
