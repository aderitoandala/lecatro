package com.dery.lecatro.repository;

import com.dery.lecatro.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

	List<History> findByRequestIdOrderByOccurredAtDesc(Long requestId);
}