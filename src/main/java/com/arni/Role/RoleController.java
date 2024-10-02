package com.arni.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class RoleController {
	
	@RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, String>>> getRoles() {
		System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        List<Map<String, String>> roles = new ArrayList<>();
        for (Role.RoleType role : Arrays.asList(Role.RoleType.DEPARTMENT_ADMIN, Role.RoleType.DEPARTMENT_ADMIN)) {
            Map<String, String> roleMap = new HashMap<>();
            roleMap.put("name", role.getName());
            roleMap.put("tag", role.getTag());
            roles.add(roleMap);
        }
        return ResponseEntity.ok(roles);
    }
}
