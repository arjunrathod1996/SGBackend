package com.arni.geolocation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends CrudRepository<Region, Long>{
	
	@Query(value = "SELECT r FROM Region r ")
	public Page<Region> findRegionPageWise(Pageable pageable);

	public List<Region> findByCity(String name);

}
