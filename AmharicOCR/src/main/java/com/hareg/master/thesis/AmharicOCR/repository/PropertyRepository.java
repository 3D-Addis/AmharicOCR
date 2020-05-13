package com.hareg.master.thesis.AmharicOCR.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.hareg.master.thesis.AmharicOCR.entity.Property;

@CrossOrigin("http://localhost:4200")
public interface PropertyRepository extends JpaRepository<Property, Long>{

}