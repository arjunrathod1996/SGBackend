package com.arni.merchant;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arni.Business.Business;
import com.arni.geolocation.LocationDetails;
import com.arni.service.CommonService;
import com.arni.user.User;
import com.arni.user.UserRepository;

@RestController
@RequestMapping("/api")
public class MerchantController {
	
	@Autowired
	MerchantRepository merchantRepository;
	@Autowired
	MerchantService merchantService;
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	CommonService commonService;
	
	
	private static final Logger logger = LoggerFactory.getLogger(MerchantController.class);
	
	@RequestMapping(value = "/merchantPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getMerchantPageWise(Pageable pageable,
                                           @RequestParam(value = "address", required = false) String address,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {
        logger.info("Received request to fetch merchant page-wise.");
        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
        
        User user = commonService.getLoggedInUser();
        
        return merchantService.getMerchant(pageable,user);
    }
	  
	@RequestMapping(value = "/merchantUserPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getMerchantUserPageWise(Pageable pageable,
                                           @RequestParam(value = "address", required = false) String address,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {
        logger.info("Received request to fetch merchant user page-wise.");
        logger.debug("Received parameters: name={}, address={}, startDate={}, endDate={}", name, address, startDate, endDate);
        
        User user = commonService.getLoggedInUser();
               
        return merchantService.getMerchantUser(user,pageable);
    }
	
	@PostMapping("/merchant")
    public ResponseEntity<Merchant> saveMerchant(@RequestBody Merchant merchant, @RequestParam(value = "id", required = false) Long id,
    		@RequestParam(value = "businessID", required = false) Long businessID,
    		@RequestParam(value = "regionID", required = false) Long regionID) {
		
		
		
        logger.info("Received request to save merchant with ID , businessID, regionID : {},{},{} ", id,businessID,regionID);
        if (merchant == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
            merchant.setId(id);
            logger.info("Received merchant ID as a parameter: {}", id);
        }

        Merchant savedMerchant = merchantService.saveMerchant(merchant,businessID,regionID);
        if (savedMerchant != null) {
            logger.info("Merchant saved successfully with ID: {}", savedMerchant.getId());
            return new ResponseEntity<>(savedMerchant, HttpStatus.CREATED);
        } else {
            logger.error("Failed to save business.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	@PostMapping("/merchant/user")
	public ResponseEntity<?> saveMerchantUser(@RequestBody User user,
	        @RequestParam(value = "id", required = false) Long id,
	        @RequestParam(value = "businessID", required = false) Long businessID,
	        @RequestParam(value = "merchantID", required = false) Long merchantID) {

	    logger.info("Received request to save merchant user with ID: {}", id);
	    
	    System.out.println("  >>>>>>>>>>>>>> email : " + user.getEmail());
	    System.out.println("  >>>>>>>>>>>>>> password : " + user.getPassword());
	    System.out.println("  >>>>>>>>>>>>>> role name : " + user.getRoleName());

	    logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> = id : {}, businessID : {}, merchantID : {}, user {} ", id, businessID, merchantID, user.getEmail());

	    if (user == null) {
	        logger.error("Invalid input received. Request body is null.");
	        return ResponseEntity.badRequest().body("Invalid input received. Request body is null.");
	    }

	    if (userRepository.existsByEmail(user.getEmail())) {
	        logger.error("Email {} already exists.", user.getEmail());
	        return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
	    }

	    if (id != null) {
	        user.setId(id);
	        logger.info("Received user ID as a parameter: {}", id);
	    }

	    User savedUser = merchantService.saveMerchantUser(user, businessID, merchantID);
	    if (savedUser != null) {
	        logger.info("User saved successfully with ID: {}", savedUser.getId());
	        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	    } else {
	        logger.error("Failed to save user.");
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
//	@RequestMapping(value="/merchant/search", method = RequestMethod.GET)
//   	public List<Merchant> searchMerchant(@RequestParam(value = "name") String name){
//   		List<Merchant> merchant = null;
//   		merchant = merchantRepository.findByName(name);
//   		return merchant;
//   	}
	
	 @RequestMapping(value = "/merchant/search", method = RequestMethod.GET)
	    public ResponseEntity<List<Merchant>> searchMerchant(@RequestParam(value = "name", required = false) String name) {
	        logger.info("Searching for merchant with name: {}", name);

	        List<Merchant> merchant;
	        try {
	            if (name == null || name.isEmpty()) {
	                // Handle case where name is null or empty
	                merchant = (List<Merchant>) merchantRepository.findAll();
	            } else {
	                merchant = merchantRepository.findByName(name);
	            }
	        } catch (Exception e) {
	            logger.error("Error occurred while searching for merchant", e);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }

	        if (merchant == null || merchant.isEmpty()) {
	            return ResponseEntity.noContent().build(); // Return 204 No Content if no businesses are found
	        }

	        return ResponseEntity.ok(merchant); // Return 200 OK with the list of businesses
	    }

	
	@PostMapping("/merchant/search/store")
    public ResponseEntity<List<Merchant>> searchMerchantsByLocation(@RequestBody LocationDetails locationDetails) {
        // Process the received location details
        System.out.println("Received location details:");
        System.out.println("Country: " + locationDetails.getCountry());
        System.out.println("State: " + locationDetails.getState());
        System.out.println("City: " + locationDetails.getCity());

     // Search for merchants based on the provided location details
        List<Merchant> merchants = merchantRepository.findMerchantsByLocation(locationDetails.getCountry(),
        		locationDetails.getState(),
        		locationDetails.getCity());
        
        
        merchants.stream().forEach((itr) -> System.out.println(itr.getShortLink()));
        
        return ResponseEntity.ok().body(merchants);
    }
	
	
	 @RequestMapping(value = "/merchant", method = RequestMethod.GET)
	    public ResponseEntity<?> getMerchantById(@RequestParam Long id) {
	        logger.info("Received request to fetch business with ID: {}", id);
	        try {
	            Optional<Merchant> merchant = merchantService.findByMerchantId(id);
	            if (merchant.isPresent()) {
	                logger.info("Merchant found with ID: {}", id);
	                return ResponseEntity.ok(merchant.get());
	            } else {
	                logger.warn("No merchant found with ID: {}", id);
	                return ResponseEntity.notFound().build();
	            }
	        } catch (Exception e) {
	            logger.error("Error fetching merchant data: {}", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching merchant data");
	        }
	    }

}
