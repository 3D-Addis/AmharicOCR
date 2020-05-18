package com.hareg.master.thesis.AmharicOCR.entity;

import java.util.Date;

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

@ToString
public class Property {

		@Id
		@JsonProperty("Id")
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		private int id;
		
		@JsonProperty("file_number")
		private String fileNumber;

		@JsonProperty("seller")
		private String seller;
		
		@JsonProperty("discription")
		private String discription;
		
		@JsonProperty("address")
		private String address;

		@JsonProperty("auction_date")
		private String auctionDate;

		@JsonProperty("auction_time")
		private String auctionTime;
		
		@JsonProperty("estimate")
		private String estimate;

		@JsonProperty("date_created")
		private Date dateCreated;

		@JsonProperty("last_updated")
		private Date lastUpdated;
		
		@ManyToOne
		@JoinColumn(name="category_id", nullable=false)
		private PropertyCategory category;
		
		
		public Property() {
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getFileNumber() {
			return fileNumber;
		}

		public void setFileNumber(String fileNumber) {
			this.fileNumber = fileNumber;
		}

		public String getSeller() {
			return seller;
		}

		public void setSeller(String seller) {
			this.seller = seller;
		}

		public String getDiscription() {
			return discription;
		}

		public void setDiscription(String discription) {
			this.discription = discription;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getAuctionDate() {
			return auctionDate;
		}

		public void setAuctionDate(String auctionDate) {
			this.auctionDate = auctionDate;
		}

		public String getAuctionTime() {
			return auctionTime;
		}

		public void setAuctionTime(String auctionTime) {
			this.auctionTime = auctionTime;
		}

		public String getEstimate() {
			return estimate;
		}

		public void setEstimate(String estimate) {
			this.estimate = estimate;
		}
		
		
}
