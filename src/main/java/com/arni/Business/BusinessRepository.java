package com.arni.Business;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BusinessRepository extends CrudRepository<Business,Long>{

//     @Query(value = "SELECT b FROM Business b "
//     		+ "Where b.deleted = false ")
//	public Page<Business> findBusinessPageWisee(Pageable pageable);
	
	@Query("SELECT b FROM Business b " +
		       "WHERE b.deleted = false " +
		       "AND (:name IS NULL OR LOWER(b.name) = LOWER(:name)) " +
		       "AND (:fullName IS NULL OR LOWER(b.fullName) = LOWER(:fullName)) " +
		       "AND (:category IS NULL OR b.category = :category) " +
		       "AND ((:startDate IS NULL AND :endDate IS NULL) OR (DATE(b.creationTime) " + 
		       "BETWEEN DATE(STR_TO_DATE(:startDate, '%Y-%m-%d')) AND DATE(STR_TO_DATE(:endDate, '%Y-%m-%d'))))")
		public Page<Business> findBusinessPageWise(
		        @Param("name") String name,
		        @Param("fullName") String fullName,
		        @Param("category") Category category,
		        @Param("startDate") String startDate,
		        @Param("endDate") String endDate,
		        Pageable pageable);


	
	
	
	@Query("SELECT b FROM Business b " +
	           "WHERE b.deleted = false " +
	           "AND (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
	           "AND (:fullName IS NULL OR LOWER(b.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) " +
	           "AND (:category IS NULL OR b.category = :category) " +
	           "AND ( (:startDate IS NULL AND :endDate IS NULL ) OR (DATE(b.creationTime) " + 
			   "BETWEEN DATE(STR_TO_DATE(:startDate,'%Y-%m-%d') ) AND DATE(STR_TO_DATE(:endDate,'%Y-%m-%d') ) ) ) ")
	    Page<Business> findBusinesses(@Param("name") String name,
	                                  @Param("fullName") String fullName,
	                                  @Param("category") Category category,
	                                  @Param("startDate") String startDate,
	                                  @Param("endDate") String endDate,
	                                  Pageable pageable);

	public List<Business> findByName(String name);  
	
	boolean existsByName(String name);

}
