package com.hareg.master.thesis.AmharicOCR.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hareg.master.thesis.AmharicOCR.entity.ImageModel;

public interface ImageRepository extends JpaRepository<ImageModel, Long> {

}