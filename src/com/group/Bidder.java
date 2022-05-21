package com.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.group.model.Client;
import com.group.model.Item;

public class Bidder implements Client{

	private static final int MAX_ITEMS = 100;
	private String name;
	private int initialCash;
	private int cash;
    private int mostItemsAvailable;
	
	public int getMostItemsAvailable() {
		return mostItemsAvailable;
	}

	private int cycles;
	private int maxSleepTimeMs;
	private Random rand;
	private AuctionServer server;
	
	

	public Bidder(AuctionServer server, String name, int cash, int cycles, int maxSleepTimeMs, Long randomSeed) {
		this.name = name;
		this.cash = cash;
		this.initialCash = cash;
		this.cycles = cycles;
		this.maxSleepTimeMs = maxSleepTimeMs;
		this.rand = new Random(randomSeed);
		this.server = server;
		this.mostItemsAvailable = 0;
	}

	public int cashSpent() {
		return (this.initialCash - this.cash);
	}
	
	public int getInitialCash() {
		return initialCash;
	}

	public int getCash() {
		return cash;
	}

	public int getCycles() {
		return cycles;
	}

	public int getMaxSleepTimeMs() {
		return maxSleepTimeMs;
	}

	public Random getRand() {
		return rand;
	}

	public AuctionServer getServer() {
		return server;
	}

	@Override
	public void run() {
		var activeBids = new ArrayList<Item>();
		var activeBidPrices = new HashMap<Item, Integer>();
		int sumActiveBids = 0;
		
		for (int i = 0; (i < cycles && cash > 0) || activeBids.size() > 0; ++i) {
            var items = server.getItems();
            if (items.size() > this.getMostItemsAvailable()) this.setMostItemsAvailable(items.size());
            
            while (items.size() > 0) {
                int index = rand.nextInt(items.size());

                Item item = items.get(index);
                items.remove(index);

                int price = server.itemPrice(item.getListingID());
                if (price < this.cash - sumActiveBids) {
                    // The server should ensure thread safety: if the price
                    // has already increased, then this bid should be invalid.
                    boolean success = server.submitBid(this.name(), item.getListingID(), price + 1);
                    
                    if(success) {
                    	  if (!activeBidPrices.containsKey(item))
                              activeBids.add(item);
                    	  else
                    		  sumActiveBids -= activeBidPrices.get(item);
                    	  sumActiveBids += price + 1;
                          activeBidPrices.put(item, price + 1);
                    }
                    
                    break;
        		}
            }
		}
		
		var currActiveBids = new ArrayList<Item>();
		var currActiveBidPrices = new HashMap<Item, Integer>();
				
		for(Item item : activeBids) {
			switch (server.checkBidStatus(this.name(), item.getListingID())){
				case 1 -> {	//bid won
					int finalPrice = activeBidPrices.get(item);
					this.cash -= finalPrice;
					sumActiveBids -= finalPrice;
					break;
				}
				case 2 -> {	//bid open
					currActiveBids.add(item);
					currActiveBidPrices.put(item, activeBidPrices.get(item));
					break;
				}
				case 3 -> {	//bid lost or invalid
					sumActiveBids -= activeBidPrices.get(item);
				}
				default -> { //something went wrong
					break;
				}
			}
		}
		
		activeBids = currActiveBids;
		activeBidPrices = currActiveBidPrices;
		
		try {
            Thread.sleep((long)rand.nextInt(this.maxSleepTimeMs));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
	}

	private void setMostItemsAvailable(int itemsSize) {
		this.mostItemsAvailable = itemsSize;
	}

	@Override
	public String name() {
		return this.name;
	}

}
