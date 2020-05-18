package com.hareg.master.thesis.AmharicOCR.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.hareg.master.thesis.AmharicOCR.entity.Property;

@CrossOrigin("http://localhost:4200")
public interface PropertyRepository extends JpaRepository<Property, Long>{
	
	@RestResource(path = "categoryid")
	Page<Property> findByCategoryId(@Param("id") Long id, Pageable pageable);
	
}