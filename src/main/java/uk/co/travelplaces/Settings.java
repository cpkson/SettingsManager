package uk.co.travelplaces;

import java.util.HashMap;

/**
 * A class which holds all the settings retrieved from a server
 * @author CraigP
 * @version 0.1
 * @since 3/8/2015
 */
public class Settings 
{
	private HashMap<String,String> settings;
	
	/**
	 * 
	 */
	public Settings()
	{
		this.settings = new HashMap<String,String>();
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void addSetting(String key, String value)
	{
		this.settings.put(key, value);
	}
	
	/**
	 * 
	 * @param key
	 * @return value
	 */
	public String getSetting(String key)
	{
		return this.settings.get(key);
	}
}