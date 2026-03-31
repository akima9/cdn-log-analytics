package com.giyong.cdnloganalytics.repository;

import com.giyong.cdnloganalytics.entity.RawLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawLogRepository extends JpaRepository<RawLog, Long> {
}
