package uk.co.travelplaces;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Properties Manager class is designed
 * to load a properties file (based upon
 * a relative path) and allow the user
 * to retrieve information from it using
 * the get method
 * 
 * @author CraigP
 * @version 0.0.1
 * @since 9/6/2015
 */
public class PropertiesManager
{
	private static final Logger LOGGER = LogManager.getLogger("co.uk.travelplaces");
	private String propertiesFile;
	private FileInputStream is;
	private Properties props;
	
	/**
	 * Constructor
	 */
	public PropertiesManager()
	{
		this("");
	}
	
	/**
	 * load settings from properties file
	 * @param propertiesFile file to load settings from
	 */
	public PropertiesManager(String propertiesFile)
	{
		setPropertiesFile(propertiesFile);
		loadProperties(getPropertiesFile());
	}
	
	/**
	 * Load the properties file defined
	 * by the parameter
	 * 
	 * @param propertiesFile2 file to load
	 */
	private void loadProperties(String propertiesFile2) 
	{
		/*
		 * to load the file we have to create and input stream
		 * which is passed to the properties object
		 */
		props = new Properties();
		try 
		{
			try 
			{
				this.is = new FileInputStream(PropertiesManager.class.getResource(propertiesFile2).toURI().getPath());
			}
			catch (URISyntaxException e) 
			{
				LOGGER.fatal("Invalid URI " + e.getMessage());
			}
		}
		catch (FileNotFoundException e)
		{
			LOGGER.fatal(getPropertiesFile() + e.getMessage());
			System.exit(1);
		}
		
		try 
		{
			props.load(is);
		} 
		catch (IOException e)
		{
			LOGGER.error("Unable to load settings " + e.getMessage());
		}
	}

	/**
	 * set the properties file to load
	 * @param propertiesFile2 file path
	 */
	public void setPropertiesFile(String propertiesFile2) 
	{
		this.propertiesFile = propertiesFile2;
	}

	/**
	 * return the properties file path
	 * @return file path
	 */
	public String getPropertiesFile()
	{
		return this.propertiesFile;
	}
	
	/**
	 * return the value of the property id
	 * @param key property id to return
	 * @return value of property
	 */
	public String getProperty(String key)
	{
		return this.props.getProperty(key);
	}
}