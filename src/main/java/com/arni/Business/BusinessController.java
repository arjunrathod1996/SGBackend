package com.arni.Business;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.arni.Role.Role.RoleType;
import com.arni.service.CommonService;
import com.arni.user.User;



@RestController
@RequestMapping("/api")
public class BusinessController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

    @Autowired
    BusinessService businessService;
    @Autowired
    BusinessRepository businessRepository;
    @Autowired
    CommonService commonService;

    @PostMapping("/business")
    public ResponseEntity<Business> saveBusiness(@RequestBody Business business, @RequestParam(value = "id", required = false) Long id) {
        logger.info("Received request to save business with ID: {}", id);
        if (business == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
            business.setId(id);
            logger.info("Received business ID as a parameter: {}", id);
        }

        Business savedBusiness = businessService.saveBusiness(business);
        if (savedBusiness != null) {
            logger.info("Business saved successfully with ID: {}", savedBusiness.getId());
            return new ResponseEntity<>(savedBusiness, HttpStatus.CREATED);
        } else {
            logger.error("Failed to save business.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @RequestMapping(value = "/businessPageWise", method = RequestMethod.GET)
//    @ResponseBody
//    public Page<?> getBusinessPageWise(Pageable pageable,
//                                           @RequestParam(value = "address", required = false) String address,
//                                           @RequestParam(value = "name", required = false) String name,
//                                           @RequestParam(value = "startDate", required = false) String startDate,
//                                           @RequestParam(value = "endDate", required = false) String endDate) {
//        logger.info("Received request to fetch businesses page-wise.");
//        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
//        return businessService.getBusiness(pageable);
//    }
    
//    @RequestMapping(value = "/businessPageWise", method = RequestMethod.GET)
//    @ResponseBody
//    public Page<Business> getBusinessPageWise(
//            Pageable pageable,
//            @RequestParam(value = "name", required = false) String name,
//            @RequestParam(value = "fullName", required = false) String fullName,
//            @RequestParam(value = "startDate", required = false) String startDate,
//            @RequestParam(value = "endDate", required = false) String endDate,
//            @RequestParam(value = "category", required = false) Category category) {
//        return businessService.getBusiness(pageable, name, fullName, startDate, endDate, category);
//    }

    
    @RequestMapping(value = "/businessPageWise", method = RequestMethod.GET)
    public ResponseEntity<Page<Business>> getBusinessPageWise(
            Pageable pageable,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "category", required = false) Category category) {
        
        try {
            Page<Business> businesses = businessService.getBusiness(pageable, name, fullName, startDate, endDate, category);
            return ResponseEntity.ok(businesses); // Return 200 OK with the page data
        } catch (Exception e) {
            // Handle exceptions and return appropriate status code and message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }
    
    
//    @GetMapping("/businesses/search")
//    @ResponseBody
//    public Page<Business> searchBusinesses(@RequestParam(required = false) String name,
//                                           @RequestParam(required = false) String fullName,
//                                           @RequestParam(required = false) Category category,
//                                           @RequestParam(value = "startDate",required = false) String startDate,
//                               			@RequestParam(value = "endDate",required = false) String endDate,
//                                           Pageable pageable) {
//        return businessService.searchBusinesses(name, fullName, category, startDate, endDate, pageable);
//    }
    
    @RequestMapping(value = "/business/search", method = RequestMethod.GET)
    public ResponseEntity<List<Business>> searchBusiness(@RequestParam(value = "name", required = false) String name) {
        logger.info("Searching for business with name: {}", name);

        List<Business> businesses;
        try {
            if (name == null || name.isEmpty()) {
                // Handle case where name is null or empty
                businesses = (List<Business>) businessRepository.findAll();
            } else {
                businesses = businessRepository.findByName(name);
            }
        } catch (Exception e) {
            logger.error("Error occurred while searching for businesses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (businesses == null || businesses.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no businesses are found
        }

        return ResponseEntity.ok(businesses); // Return 200 OK with the list of businesses
    }
    
//    @GetMapping("/business/delete")
//	@ResponseBody
//	public ResponseEntity<?> deleteBusiness(@RequestParam(value = "id")Long id){
//    	businessService.deleteBusiness(id);
//    	logger.debug("Deleted Business : {} ",id);
//		return new ResponseEntity<String>("{}",HttpStatus.OK);
//		
//	}

    @RequestMapping(value = "/business", method = RequestMethod.GET)
    public ResponseEntity<?> getBusinessById(@RequestParam Long id) {
        logger.info("Received request to fetch business with ID: {}", id);
        try {
            Optional<Business> business = businessService.findByUserId(id);
            if (business.isPresent()) {
                logger.info("Business found with ID: {}", id);
                return ResponseEntity.ok(business.get());
            } else {
                logger.warn("No business found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching business data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching business data");
        }
    }
    
//    @RequestMapping(value="/business/search", method = RequestMethod.GET)
//   	public List<Business> searchCity(@RequestParam(value = "name") String name){
//   		List<Business> business = null;
//   		business = businessRepository.findByName(name);
//   		return business;
//   	}
    
    @RequestMapping(value = "/business/config", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateConfig(@RequestBody Config changedConfig, @RequestParam(value = "businessID") Long businessID) {
        try {
            User user = commonService.getLoggedInUser();

            if (!user.getRole().getName().equals(RoleType.ROLE_ADMIN)) {
                logger.warn("role {} attempted to update config without permission", user.getRole().getName());
                return new ResponseEntity<>("Not Allowed", HttpStatus.BAD_REQUEST);
            }

            Business business = businessRepository.findById(businessID).orElse(null);

            if (business == null) {
                logger.warn("Business with ID {} not found", businessID);
                return new ResponseEntity<>("Business not found", HttpStatus.NOT_FOUND);
            }

            Config originalConfig = business.getConfig();
            if (originalConfig == null) {
                originalConfig = new Config();
            }

            if (changedConfig != null) {
                originalConfig.setEnableI18nInputSupport(changedConfig.getEnableI18nInputSupport());
                originalConfig.setEnableQRCode(changedConfig.getEnableQRCode());
                originalConfig.setEnableAppointment(changedConfig.getEnableAppointment());
                originalConfig.setEnableBillSumbission(changedConfig.getEnableBillSumbission());
            }

            business.setConfig(originalConfig);
            businessRepository.save(business);

            logger.info("Config updated successfully for business ID {}", businessID);
            return new ResponseEntity<>("Config updated successfully", HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error updating config for business ID " + businessID, e);
            return new ResponseEntity<>("An error occurred while updating the config", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/businesses")
    public ResponseEntity<List<Business>> getAllBusinesses() {
        logger.info("Received request to fetch all businesses.");
        List<Business> businesses = businessService.findAll();
        return new ResponseEntity<>(businesses, HttpStatus.OK);
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> categories = Arrays.asList(Category.values());
        return ResponseEntity.ok(categories);
    }
    
    @PutMapping("/business/{id}")
    public ResponseEntity<Business> updateBusiness(@PathVariable Long id, @RequestBody Business business) {
        try {
            business.setId(id); // Ensure the ID in the path matches the ID in the body
            Business updatedBusiness = businessService.updateBusiness(business);
            return ResponseEntity.ok(updatedBusiness);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/business/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        try {
            businessService.deleteBusiness(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    

}

