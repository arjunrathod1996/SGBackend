package com.arni.geolocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arni.Business.Business;

@RestController
@RequestMapping("/api")
public class LocationController {
	
	private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    LocationService locationService;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    RegionRepository regionRepository;

    @PostMapping("/location/country")
    public ResponseEntity<Country> saveCountry(@RequestBody Country country, @RequestParam(value = "id", required = false) Long id) {
        logger.info("Received request to save country with ID: {}", id);
        if (country == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
            country.setId(id);
            logger.info("Received country ID as a parameter: {}", id);
        }

        Country savedCountry = locationService.saveCountry(country);
        if (savedCountry != null) {
            logger.info("Country saved successfully with ID: {}", savedCountry.getId());
            return new ResponseEntity<>(savedCountry, HttpStatus.CREATED);
        } else {
            logger.error("Failed to save country.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
//    @PostMapping("/location/region")
//    public ResponseEntity<Region> saveRegion(@RequestBody Region region, @RequestParam(value = "id", required = false) Long id,
//    		@RequestParam(value = "countryID", required = false) Long countryID) {
//        logger.info("Received request to save region with ID: {}", id);
//        if (region == null) {
//            logger.error("Invalid input received. Request body is null.");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        if (id != null) {
//            region.setId(id);
//            logger.info("Received region ID as a parameter: {}", id);
//        }
//
//        Region savedRegion = locationService.saveRegion(region,countryID);
//        if (savedRegion != null) {
//            logger.info("Region saved successfully with ID: {}", savedRegion.getId());
//            return new ResponseEntity<>(savedRegion, HttpStatus.CREATED);
//        } else {
//            logger.error("Failed to save region.");
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    
    @PostMapping("/location/region")
    public ResponseEntity<Region> saveRegion(
            @RequestBody Region region,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "countryID", required = false) Long countryID) {

        logger.info("Received request to save region with ID: {}", id);
        logger.info("Received request to save Country with ID: {}", countryID);
        
        if (region == null) {
            logger.error("Invalid input received. Request body is null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id != null) {
        	System.out.println(">>>>>>>>>>>>>>>>>>>>> : upated");
            region.setId(id);
            logger.info("Received region ID as a parameter: {}", id);
        }
        
        System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ");

        Region savedRegion = locationService.saveRegion(region, countryID);
        if (savedRegion != null) {
            logger.info("Region saved successfully with ID: {}", savedRegion.getId());
            return new ResponseEntity<>(savedRegion, HttpStatus.CREATED);
        } else {
            logger.error("Failed to save region.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @RequestMapping(value = "/location/countryPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getCountryPageWise(Pageable pageable,
                                           @RequestParam(value = "callingCode", required = false) String callingCode,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {
        logger.info("Received request to fetch country page-wise.");
        logger.debug("Received parameters: name={}, callingCode={}, startDate={}, endDate={}", name, callingCode, startDate, endDate);
        return locationService.getCountry(pageable);
    }
    
    @RequestMapping(value = "/location/regionPageWise", method = RequestMethod.GET)
    @ResponseBody
    public Page<?> getRegionPageWise(Pageable pageable,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "startDate", required = false) String startDate,
                                           @RequestParam(value = "endDate", required = false) String endDate) {
        logger.info("Received request to fetch country page-wise.");
        logger.debug("Received parameters: name={}, startDate={}, endDate={}", name, startDate, endDate);
        return locationService.getRegion(pageable);
    }
    
    
    @RequestMapping(value = "/location/search", method = RequestMethod.GET)
    public ResponseEntity<?> searchRegion(@RequestParam(value = "city", required = false) String city) {
        logger.info("Searching for regions with name: {}", city);

        List<Region> regions;
        try {
            if (city == null || city.isEmpty()) {
                // Handle case where name is null or empty
                regions = (List<Region>) regionRepository.findAll();
            } else {
                regions = regionRepository.findByCity(city);
            }
            
        } catch (Exception e) {
            logger.error("Error occurred while searching for regions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }

        if (regions == null || regions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found."); // Return 404 Not Found with a custom message
        }

        return ResponseEntity.ok(regions); // Return 200 OK with the list of businesses
    }
    
//    @RequestMapping(value="/country/search", method = RequestMethod.GET)
//	public List<Country> searchCountry(@RequestParam(value = "name") String name){
//		List<Country> country = null;
//		country = countryRepository.findByName(name);
//		return country;
//	}
    
    
//    @RequestMapping(value = "/country/search", method = RequestMethod.GET)
//    public ResponseEntity<List<Country>> searchCountry(@RequestParam(value = "name") String name) {
//        List<Country> countries = countryRepository.findByName(name);
//        if (countries != null && !countries.isEmpty()) {
//            return new ResponseEntity<>(countries, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
    
    
//    @GetMapping("/country/search")
//    public ResponseEntity<?> searchCountry(@RequestParam(value = "name", required = false) String name) {
//        logger.info("Searching for countries with name: {}", name);
//
//        List<Country> countries;
//        try {
//            if (name == null || name.isEmpty()) {
//                // Handle case where name is null or empty
//                countries = (List<Country>) countryRepository.findAll();
//            } else {
//                countries = countryRepository.findByName(name);
//            }
//           
//            
//        } catch (Exception e) {
//            logger.error("Error occurred while searching for countries", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
//        }
//
//        if (countries == null || countries.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found."); // Return 404 Not Found with a custom message
//        }
//
//        return ResponseEntity.ok(countries); // Return 200 OK with the list of countries
//    }
    
    @GetMapping("/country/search")
    public ResponseEntity<?> searchCountry(@RequestParam(value = "name", required = false) String name) {
        logger.info("Searching for countries with name: {}", name);

        List<Country> countries;
        try {
            if (name == null || name.isEmpty()) {
                // Return all countries if name is null or empty
                countries = (List<Country>) countryRepository.findAll();
            } else {
                // Search for countries by name
                countries = countryRepository.findByName(name);
            }
        } catch (Exception e) {
            logger.error("Error occurred while searching for countries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }

        if (countries == null || countries.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found."); // Return 404 Not Found with a custom message
        }

        return ResponseEntity.ok(countries); // Return 200 OK with the list of countries
    }

    
    @RequestMapping(value="/location/city/search", method = RequestMethod.GET)
	public List<Region> searchCity(@RequestParam(value = "name") String name){
		List<Region> city = null;
		city = regionRepository.findByCity(name);
		return city;
	}
    
    @GetMapping("/location/mock-live-location")
    @ResponseBody
    public Map<String, String> mockLiveLocation(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        // Check if the location is in Bengaluru or Mumbai based on coordinates
        boolean isInBengaluru = isLocationInBengaluru(latitude, longitude);

        // If user is in Bengaluru, return static Bengaluru location details
        if (isInBengaluru) {
            return getBengaluruLocationDetails();
        } else {
            // If user is not in Bengaluru, return static Mumbai location details
            return getMumbaiLocationDetails();
        }
    }

    // Method to check if the location is in Bengaluru
    private boolean isLocationInBengaluru(double latitude, double longitude) {
        // Define coordinates for Bengaluru and Mumbai
        //double bengaluruLatitude = 12.9716;
       // double bengaluruLongitude = 77.5946;

        // Calculate distance between current location and Bengaluru location
        double distanceToBengaluru = Math.sqrt(Math.pow(latitude - latitude, 2) + Math.pow(longitude - longitude, 2));

        // If distance is within a certain threshold, consider it as Bengaluru
        double bengaluruThreshold = 0.1;
       // return distanceToBengaluru < bengaluruThreshold;
        
        return false;
    }

    // Method to return static Bengaluru location details
    private Map<String, String> getBengaluruLocationDetails() {
        Map<String, String> result = new HashMap<>();
        result.put("country", "India");
        result.put("state", "Karnataka");
        result.put("city", "Bengaluru");
        result.put("area", "Koramangala");
        return result;
    }

    // Method to return static Mumbai location details
    private Map<String, String> getMumbaiLocationDetails() {
        Map<String, String> result = new HashMap<>();
        result.put("country", "India");
        result.put("state", "Maharashtra");
        result.put("city", "Mumbai");
        result.put("area", "Bandra");
        return result;
    }

}
