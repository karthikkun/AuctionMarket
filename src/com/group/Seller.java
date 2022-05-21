package com.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.group.model.Client;

public class Seller implements Client{

	private static final int MAX_ITEMS = 100;
	private String name;
	private List<String> items;
	
	private int cycles;
	private int maxSleepTimeMs;
	private Random rand;
	private AuctionServer server;
	
	public Seller(AuctionServer server, String name, int cycles, int maxSleepTimeMs, Long randomSeed) {
		this.server = server;
		this.name = name;
		this.cycles = cycles;
		this.maxSleepTimeMs = maxSleepTimeMs;
		this.rand = new Random(randomSeed);
		
		int itemCount = MAX_ITEMS;
        this.items = new ArrayList<String>();
        
        for (int i = 1; i <= itemCount; ++i)
            items.add(this.name() + "-" + i);
	}
	
	@Override
	public void run() {
		
		for (int i = 0; i < this.getCycles() && this.getItems().size() > 0; ++i) {
	    	int index = this.getRand().nextInt(this.getItems().size());
	    	String item = this.getItems().get(index);
	    	
	    	int listingID = server.submitItem(this.name(), item, this.rand.nextInt(100), this.rand.nextInt(100) + 100);
	    	if (listingID != -1) this.items.remove(index);
	    	
    		try {
                Thread.sleep(this.getRand().nextInt(this.getMaxSleepTimeMs()));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
		}
		
	}

	public List<String> getItems() {
		return items;
	}

	public String getName() {
		return name;
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
	public String name() {
		return this.name;
	}

}
