package com.codeit.findex.repository.autosync;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.entity.IndexInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoSyncRepository
    extends JpaRepository<IndexInfo, Long> {

}
