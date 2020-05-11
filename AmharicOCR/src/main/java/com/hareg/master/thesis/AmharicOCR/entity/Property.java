package com.hareg.master.thesis.AmharicOCR.entity;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="tbl_properties")
@Setter
@Getter
@ToString
public class Property {

		@Id
		@JsonProperty("Id")
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private Long id;
		
		@JsonProperty("file_number")
		private String fileNumber;

		@JsonProperty("seller")
		private String seller;
		
		@JsonProperty("discription")
		private String discription;
		
		@JsonProperty("address")
		private String address;

		@JsonProperty("action_date")
		private Date actionDate;

		@JsonProperty("action_time")
		private String actionTime;
		
		private BigDecimal estimate;

		@JsonProperty("date_created")
		private Date dateCreated;

		@JsonProperty("last_updated")
		private Date lastUpdated;
		
		@ManyToOne
		@JoinColumn(name="category_id", nullable=false)
		private PropertyCategory category;
		
}
