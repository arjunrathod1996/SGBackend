package com.arni.geolocation;

import java.util.Date;

import com.arni.Utils.BigBaseEntity;
import com.arni.Utils.Utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "country")
public class Country extends BigBaseEntity{
	
	String name;
	
	@Column(name = "calling_code")
	String callingCode;
	
	 @PrePersist
     protected void onCreate() {
	     Date now = Utils.now();
	     if (this.getCreationTime() == null)
	         this.setCreationTime(now);
	     if (this.getUpdateTime() == null)
	         this.setUpdateTime(now);
     }

     @PreUpdate
     protected void onUpdate() {
         this.setUpdateTime(Utils.now());
     }
	
	public String getCallingCode() {
		return callingCode;
	}

	public void setCallingCode(String callingCode) {
		this.callingCode = callingCode;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	Long countryID;

	public Long getCountryID() {
		return countryID;
	}

	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}
	
}
