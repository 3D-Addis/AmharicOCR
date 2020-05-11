package com.hareg.master.thesis.AmharicOCR.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.hareg.master.thesis.AmharicOCR.entity.PropertyCategory;

@RepositoryRestResource(collectionResourceRel = "propertyCategories", path = "property-categories")
public interface PropertCategoryRepository extends JpaRepository<PropertyCategory, Long>{

}
