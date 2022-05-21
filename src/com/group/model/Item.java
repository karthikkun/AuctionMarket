package com.group.model;

import java.util.Date;

public class Item {
	private String seller;
    private String name;
    private int listingID;
    private int lowestBiddingPrice;
    private int biddingDurationMs;
    private Date biddingStart;
    
	public Item() {
	}
	
	public Item(String seller, String name, int listingID, int lowestBiddingPrice, int biddingDurationMs) {
		this.seller = seller;
		this.name = name;
		this.listingID = listingID;
		this.lowestBiddingPrice = lowestBiddingPrice;
		this.biddingDurationMs = biddingDurationMs;
		this.biddingStart = new Date();
	}

	/**
	 * Returns true if the bidding is open (active) for the current item.
	 * 
	 * In other words, if the time elapsed from the moment the bidding 
	 * started for this item is less than the bidding duration, it will
	 * return true. Otherwise it returns false. 
	 */
	public boolean biddingOpen() {
	    Date now = new Date();
	    return (now.getTime() - this.biddingStart.getTime()) / 1 < this.biddingDurationMs;
	}
	
	public String getSeller() {
		return seller;
	}

	public String getName() {
		return name;
	}

	public int getListingID() {
		return listingID;
	}

	public int getLowestBiddingPrice() {
		return lowestBiddingPrice;
	}

	public int getBiddingDurationMs() {
		return biddingDurationMs;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!obj.getClass().equals(this.getClass()))
	        return false;
	    return ((Item)obj).listingID == this.listingID;
	}

	@Override
	public int hashCode() {
		return this.listingID;
	}

	@Override
	public String toString() {
		return ((Integer)this.listingID).toString();
	}
}
