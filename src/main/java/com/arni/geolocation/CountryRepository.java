package com.arni.geolocation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long>{

//	@Query(value = "SELECT c FROM Country c ")
//	public Page<Country> findCountryPageWise(Pageable pageable);

	public List<Country> findByName(String name);

	public Country findByCallingCode(String defaultCountryCallingCode);
	
	@Query("SELECT c FROM Country c")
    Page<Country> findCountryPageWise(Pageable pageable);

    Page<Country> findByCallingCodeContaining(String callingCode, Pageable pageable);

    Page<Country> findByNameContaining(String name, Pageable pageable);

}

