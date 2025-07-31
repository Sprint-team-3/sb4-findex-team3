package com.codeit.findex.data;

import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.repository.IndexInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DummyData implements CommandLineRunner {

    private final IndexInfoRepository indexInfoRepository;

    @Override
    public void run(String... args) throws Exception {
        if(indexInfoRepository.count() == 0) {
            IndexInfo info = new IndexInfo();
            info.setIndexClassification("테스트");
            info.setIndexName("지수이름");
            info.setEmployedItemsCount(100);
            info.setBasepointInTime(LocalDate.of(2024,1,1));
            info.setBaseIndex(1000.0);
            info.setSourceType("user");
            info.setFavorite(false);
            info.setEnabled(true);

            info = indexInfoRepository.save(info);

            System.out.println("더미 indexinfo 저장됨. UUID : " + info.getId());
        }
    }
}
