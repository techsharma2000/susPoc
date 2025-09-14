package com.sushil.poc.repository.jpa;

import com.sushil.poc.model.MongoSyncFailure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MongoSyncFailureRepository extends JpaRepository<MongoSyncFailure, Long> {
}
