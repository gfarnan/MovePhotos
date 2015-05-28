import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
 


/**
 */
public class MovephotosProperties {
	private final Properties configProp = new Properties();
	private static final Logger logger = Logger.getLogger( MovephotosProperties.class.getName() );	
	private MovephotosProperties()
	{
		//Private constructor to restrict new instances
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("movephotos.properties");
		if (in!=null){		
			logger.log(Level.CONFIG,"Read all properties from file");
			try {
				configProp.load(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//Bill Pugh Solution for singleton pattern
	/**
	 */
	private static class LazyHolder
	{
		private static final MovephotosProperties INSTANCE = new MovephotosProperties();
	}

	/**
	 * Method getInstance.
	 * @return MovephotosProperties
	 */
	public static MovephotosProperties getInstance()
	{
		return LazyHolder.INSTANCE;
	}
	
	/**
	 * Method getProperty.
	 * @param key String
	 * @param defaultValue String
	 * @return String
	 */
	public String getProperty(String key, String defaultValue){
		if (!configProp.isEmpty() && configProp.getProperty(key)!=null){
			return configProp.getProperty(key);
		}
		return defaultValue;
	}
	
	/**
	 * Method getProperty.
	 * @param key String
	 * @return String
	 */
	public String getProperty(String key){
		return configProp.getProperty(key);
	}
	
	/**
	 * Method getAllPropertyNames.
	 * @return Set<String>
	 */
	public Set<String> getAllPropertyNames(){
		return configProp.stringPropertyNames();
	}

	/**
	 * Method containsKey.
	 * @param key String
	 * @return boolean
	 */
	public boolean containsKey(String key){
		return configProp.containsKey(key);
	}	

}
