package uk.co.travelplaces;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import uk.co.parknet.httpConnect.HttpConnector;
import uk.co.travelplaces.exception.ResponseException;

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
public class SettingsManager
{
	private static final Logger LOGGER = LogManager.getLogger("co.uk.travelplaces");
	private static final String SETTINGSFILE = "/settings.properties";
	private String propertiesFile;
	private FileInputStream is;
	private Properties props;
	private Settings settings;
	private String settings_server = "";
	private String settings_db = "";
	private String application_name = "";
	private String settings_view = "";
	private static SettingsManager instance = null;
	
	/**
	 * Constructor
	 */
	private SettingsManager()
	{
		this(SettingsManager.SETTINGSFILE);
	}
	
	/**
	 * load initial settings from properties file
	 * @param propertiesFile file to Settings server settings from
	 */
	private SettingsManager(String propertiesFile)
	{
		setSettings(Settings.getInstance());
		setPropertiesFile(propertiesFile);
		loadProperties(getPropertiesFile());
		retrieveSettingsFromServer();
	}
	
	/**
	 * 
	 * @return Settings Manager
	 */
	public static SettingsManager getInstance()
	{
		if(instance == null)
		{
			instance = new SettingsManager();
		}
		
		return instance;
	}
	
	/**
	 * Set the container for the settings
	 * 
	 * @param settings2
	 */
	private void setSettings(Settings settings2)
	{
		this.settings = settings2;
	}

	/**
	 * Obtain the settings values from the server for
	 * the application
	 */
	private void retrieveSettingsFromServer()
	{
		/*
		 * Retrieve the settings for connecting to the server from
		 * a properties file
		 */
		this.settings_server = this.props.getProperty("settings_server");
		this.settings_db = this.props.getProperty("settings_db");
		this.application_name = this.props.getProperty("application_name");
		this.settings_view = this.props.getProperty("settings_view");
		
		String message = "";
		
		//If none of the settings can be loaded then we quit the application and log the error
		if(this.settings_server.isEmpty() || this.settings_db.isEmpty() || this.application_name.isEmpty() || this.settings_view.isEmpty())
		{
			LOGGER.error("Check the configuration file for the following settings :- settings_server settings_db application_name settings_view");
			System.exit(1);
		}
		else
		{
			try 
			{
				//create a connection to the server by building the URL
				HttpConnector connection = new HttpConnector(this.settings_server+this.settings_db+this.settings_view, false);
				
				//add the application name as a parameter
				connection.addGetParameter("key", this.application_name);
				
				try 
				{
					message = connection.sendGET();
				} 
				catch (IOException e)
				{
					LOGGER.error(e.getMessage());
				} 
				catch (ResponseException e) 
				{
					LOGGER.error(e.getMessage());
				}
			}
			catch (MalformedURLException e)
			{
				LOGGER.error("Invalid Url when retrieving settings :- " + this.settings_server+this.settings_db+this.settings_view);
			}
		}
		
		if(!message.isEmpty())
		{
			JSONObject obj = new JSONObject(message);
			JSONArray array = obj.getJSONArray("rows");
			JSONObject row = null;
			
			for(int i = 0; i<array.length(); i++)
			{
				row = array.getJSONObject(i);
				JSONArray values = row.getJSONArray("value");
				this.settings.addSetting(values.getString(0), values.getString(1));
			}
		}
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
		this.props = new Properties();
		try 
		{
			try 
			{
				this.is = new FileInputStream(SettingsManager.class.getResource(propertiesFile2).toURI().getPath());
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
			this.props.load(is);
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
	private void setPropertiesFile(String propertiesFile2) 
	{
		this.propertiesFile = propertiesFile2;
	}

	/**
	 * return the properties file path
	 * @return file path
	 */
	private String getPropertiesFile()
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
		return this.settings.getSetting(key);
	}
}