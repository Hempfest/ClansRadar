package com.github.sanctum.clansradar;

import com.github.sanctum.clansradar.listeners.Controller;
import com.github.sanctum.link.EventCycle;

public final class ClansRadar extends EventCycle {

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String getName() {
		return "Radar";
	}

	@Override
	public String getDescription() {
		return "An enhanced map view addition.";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String[] getAuthors() {
		return new String[]{"Hempfest"};
	}

	@Override
	public void onLoad() {
		register(new Controller());
	}



	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}
}
