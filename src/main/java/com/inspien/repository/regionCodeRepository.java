package com.inspien.repository;

import com.inspien.entity.regionCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface regionCodeRepository extends CrudRepository<regionCode, Long> {

    regionCode findByRegion(String region);
}
