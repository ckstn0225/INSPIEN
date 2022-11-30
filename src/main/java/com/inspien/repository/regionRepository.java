package com.inspien.repository;


import com.inspien.entity.region;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface regionRepository extends CrudRepository<region, Long> {

    region findFirstByCityAndRegion(String city,String region);

    region findFirstByXisAndYis(int x, int y);
}
