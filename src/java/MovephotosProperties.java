import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
 


public class MovephotosProperties {
	private final Properties configProp = new Properties();
	private static final Logger logger = Logger.getLogger( MovephotosProperties.class.getName() );	
	private MovephotosProperties()
	{
		//Private constructor to restrict new instances
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("movephotos.properties");
		logger.log(Level.CONFIG,"Read all properties from file");
		try {
			configProp.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Bill Pugh Solution for singleton pattern
	private static class LazyHolder
	{
		private static final MovephotosProperties INSTANCE = new MovephotosProperties();
	}

	public static MovephotosProperties getInstance()
	{
		return LazyHolder.INSTANCE;
	}
	public String getProperty(String key){
		return configProp.getProperty(key);
	}
	
	public Set<String> getAllPropertyNames(){
		return configProp.stringPropertyNames();
	}

	public boolean containsKey(String key){
		return configProp.containsKey(key);
	}	

}
