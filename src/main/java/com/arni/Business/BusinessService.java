package com.arni.Business;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arni.Utils.Utils;

import jakarta.transaction.Transactional;

@Service
public class BusinessService {

    @Autowired
    BusinessRepository businessRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessService.class);
    
    
    @Transactional
    public Business saveBusiness(Business business) {
        Long businessId = business.getId();
        logger.info("Attempting to save or update business with ID: {}", businessId);

        try {
            if (businessId == null || !businessRepository.existsById(businessId)) {
                // Business doesn't exist in the repository, so save it
                logger.info("Saving a new business.");
                if (businessRepository.existsByName(business.getName())) {
                    logger.error("Business with name {} already exists. Cannot save new business.", business.getName());
                    throw new DataIntegrityViolationException("Business with the same name already exists.");
                }
                return businessRepository.save(business);
            } else {
                // Business already exists, update its fields and save
                logger.info("Updating an existing business with ID: {}", businessId);
                return updateExistingBusiness(business);
            }
        } catch (DataIntegrityViolationException e) {
            logger.error("Error saving or updating business: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error saving or updating business: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred.", e);
        }
    }

    @Transactional
    public Business updateExistingBusiness(Business business) {
        Long businessId = business.getId();
        Business existingBusiness = businessRepository.findById(businessId).orElse(null);

        if (existingBusiness != null) {
            logger.info("Business found with ID {}. Updating its fields.", businessId);
            if (!existingBusiness.getName().equals(business.getName()) && businessRepository.existsByName(business.getName())) {
                logger.error("Business with name {} already exists. Cannot update business.", business.getName());
                throw new DataIntegrityViolationException("Business with the same name already exists.");
            }
            existingBusiness.setName(business.getName());
            existingBusiness.setFullName(business.getFullName());
            existingBusiness.setAddress(business.getAddress());
            existingBusiness.setDescription(business.getDescription());
            existingBusiness.setCategory(business.getCategory());
            return businessRepository.save(existingBusiness);
        } else {
            // Handle the case where business with the given ID doesn't exist
            logger.error("Business with ID {} not found for update.", businessId);
            throw new IllegalArgumentException("Business with given ID not found.");
        }
    }

//    public Page<Business> getBusiness(Pageable pageable) {
//        logger.info("Fetching businesses with pageable: {}", pageable);
//
//        Sort sort = pageable.getSort();
//        if (sort == null || !sort.isSorted()) {
//            logger.debug("No sort criteria found. Applying default sort by 'creationTime' descending.");
//            sort = Sort.by(Sort.Direction.DESC, "creationTime");
//        }
//
//        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//        logger.debug("Page request created with page number: {}, page size: {}, sort: {}",
//                pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort());
//
//        try {
//            Page<Business> businessPage = businessRepository.findBusinessPageWisee(pageRequest);
//            logger.info("Businesses fetched successfully with total elements: {}", businessPage.getTotalElements());
//            return businessPage;
//        } catch (Exception e) {
//            logger.error("Error fetching businesses with pageable: {}", pageable, e);
//            throw e;
//        }
//    }
    
    public Page<Business> getBusiness(Pageable pageable, String name, String fullName, String startDate, String endDate, Category category) {
    	
    	Sort sort = pageable.getSort();

		if (sort == null) {
			sort = Sort.by(Sort.Direction.DESC, "creationTime");
		}
		
		if(endDate != null && startDate == null)
			startDate = "1970-01-01";
		if(startDate != null && endDate == null)
			endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
		
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    	
        return businessRepository.findBusinessPageWise(name, fullName, category, startDate, endDate, pageRequest);
    }

    
    public Page<Business> searchBusinesses(String name, String fullName, Category category, String startDate, String endDate, Pageable pageable) {
    	
    	Sort sort = pageable.getSort();

		if (sort == null) {
			sort = Sort.by(Sort.Direction.DESC, "creationTime");
		}
		
		if(endDate != null && startDate == null)
			startDate = "1970-01-01";
		if(startDate != null && endDate == null)
			endDate = Utils.dateToString(Utils.now(), "yyyy-MM-dd");
		
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		
		
    	
    	
        return businessRepository.findBusinesses(name, fullName, category, startDate, endDate, pageRequest);
    }
    
    
    public Optional<Business> findByUserId(Long userId) {
        logger.info("Fetching business with userId: {}", userId);

        try {
            Optional<Business> business = businessRepository.findById(userId);
            if (business.isPresent()) {
                logger.info("Business found with userId: {}", userId);
            } else {
                logger.warn("No business found with userId: {}", userId);
            }
            return business;
        } catch (Exception e) {
            logger.error("Error fetching business with userId: {}", userId, e);
            throw e;
        }
    }

//    public void deleteBusiness(Long id) {
//        logger.info("Deleting business with id: {}", id);
//
//        try {
//            Optional<Business> optionalBusiness = businessRepository.findById(id);
//            if (optionalBusiness.isPresent()) {
//                Business business = optionalBusiness.get();
//                business.setDeleted(true);
//                businessRepository.save(business);
//                logger.info("Business marked as deleted with id: {}", id);
//            } else {
//                logger.warn("No business found with id: {}", id);
//            }
//        } catch (Exception e) {
//            logger.error("Error deleting business with id: {}", id, e);
//            throw e;
//        }
//    }
    
    public List<Business> findAll() {
        return (List<Business>) businessRepository.findAll();
    }
    
    public Business updateBusiness(Business business) throws Exception {
        logger.info("Starting update for business with ID: {}", business.getId());
        try {
            if (!businessRepository.existsById(business.getId())) {
                logger.error("Business with ID {} not found for update", business.getId());
                throw new Exception("Business not found");
            }
            Business updatedBusiness = businessRepository.save(business);
            logger.info("Successfully updated business with ID: {}", business.getId());
            return updatedBusiness;
        } catch (Exception e) {
            logger.error("Error updating business with ID: {}", business.getId(), e);
            throw e; // Re-throwing exception to be handled by controller or higher layers
        }
    }
    
    public void deleteBusiness(Long id) throws Exception {
        logger.info("Starting deletion for business with ID: {}", id);
        try {
            if (!businessRepository.existsById(id)) {
                logger.error("Business with ID {} not found for deletion", id);
                throw new Exception("Business not found");
            }
            businessRepository.deleteById(id);
            logger.info("Successfully deleted business with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting business with ID: {}", id, e);
            throw e; // Re-throwing exception to be handled by controller or higher layers
        }
    }
}
