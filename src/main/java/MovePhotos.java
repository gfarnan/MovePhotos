import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import java.io.File;
import java.io.FilenameFilter;
 

/**
 * @author gfarnan
 * @version $Revision: 1.0 $
 */
public class MovePhotos {
	private static final String JPG = "jpg";	
	private static final String PNG = "png";	
	private static final String MOV = "mov";
	private static final String DOTMOV = ".MOV";
	private static final Logger logger = Logger.getLogger( MovePhotos.class.getName() );
	private static FileHandler fh = null;
	private String logfile;
	private String logLevel;	
	private Boolean test;
	private String inDir;
	private String outDir;
	private String videoDir;	
	
	
	
	public MovePhotos(){
	
		logfile = MovephotosProperties.getInstance().getProperty("logFile","logDetails.log");
		logLevel = MovephotosProperties.getInstance().getProperty("logLevel","CONFIG");
		test = Boolean.parseBoolean(MovephotosProperties.getInstance().getProperty("test","no"));
		inDir = MovephotosProperties.getInstance().getProperty("From_Folder","temp");
		outDir = MovephotosProperties.getInstance().getProperty("To_Folder","temp");
		videoDir = MovephotosProperties.getInstance().getProperty("Video_Folder","temp");		
		
		System.out.println ("Moving photos from "+inDir+" to "+outDir);
		
		
		try {
			fh=new FileHandler(logfile, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
	    fh.setFormatter(new Formatter() {
	    	public String format(LogRecord rec) {
	            StringBuffer buf = new StringBuffer(1000);
	            buf.append(new java.util.Date());
	            buf.append(' ');
	            buf.append(rec.getLevel());
	            buf.append(' ');
	            buf.append(formatMessage(rec));
	            buf.append('\n');
	            return buf.toString();
	    	}
	    });
		l.addHandler(fh);
		l.setLevel(Level.parse(logLevel));		
	}
	
	/**
	 * Get List of Photographs from folder
	
	
	 * @param dir String
	 * @return photographs */
	private Media[] getPhotos(String dir) {
		FilenameFilter filter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(JPG)) || (name.toLowerCase().endsWith(PNG));
			}
		};
		return listFiles(dir, filter);
	}

	/**
	 * Method getVideos.
	 * @param dir String
	 * @return Media[]
	 */
	private Media[] getVideos(String dir) {
		FilenameFilter filter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(MOV));
			}
		};
		return listFiles(dir, filter);
	}
	
	
	/**
	 * Filter a list of Files to return only Photographs
	
	 * @param filter
	
	 * @param dir String
	 * @return photographs */
	private Media[] listFiles(String dir, FilenameFilter filter) {
		File[] files = new File(dir).listFiles(filter);
		Media[] photographs = new Media[0];
		if (files != null) {
			logger.log(Level.CONFIG,"Number of photographs found:"+ files.length);
			photographs = new Media[files.length];
			for (int i = 0; i < files.length; i++) {
				photographs[i] = new Media(files[i]);
			}
		}
		return photographs;
	}


	/**
	 * Method makeMissingDir.
	 * @param inDir String
	
	 * @return File */
	private File makeMissingDir(String inDir){
		logger.log(Level.CONFIG,"Make Folder:"+ inDir);		
		File newFolder = new File(inDir);
		logger.log(Level.CONFIG,"Make Folder? "+newFolder.mkdir());					
		return newFolder;
	}
	
	/**
	 *  Return a list of Folders in a directory
	
	 * @param inDir String
	 * @param photograph Photograph
	 * @return folders */
	private File getFolders(String inDir, Media photograph) {
		String dir = inDir + "\\" + photograph.getYear();
		File[] files = new File(dir).listFiles();
		logger.log(Level.CONFIG,"Returning folders from " + dir);		
		File folder = null;
		if (files != null) {
			if (files.length>0){
				for (int i = 0; i < files.length; i++) {
					if ((files[i].isDirectory()) && correctFolder(files[i], photograph)){
						folder = files[i];
					}
				}
				if (folder==null){
					//No Folder so need to make it
					folder = makeMissingDir(dir+"\\"+photograph.getMonth());
				}
			}
			else {
				//No Folder so need to make it
				folder = makeMissingDir(dir+"\\"+photograph.getMonth());				
			}
		}
		else {
			//No Folder so need to make it
			makeMissingDir(dir);			
			folder = makeMissingDir(dir+"\\"+photograph.getMonth());				
		}
		return folder;
	}

	/**
	 * Check if a photograph should be placed in a folder
	 * @param folder
	 * @param photograph
	
	 * @return boolean */
	private boolean correctFolder(File folder, Media photograph) {
		return (folder.getName().toLowerCase().contains(photograph.getMonth().toLowerCase()));
	}

	/**
	 * Default Entrance Class
	 */
	public void move() {
		move(this.inDir, this.outDir);
	}


	/**
	 * Move Photographs from one folder to another
	 * @param inDir String
	 * @param outDir String
	 */
	public void move(String inDir, String outDir) {

		// Cycle through list
		Media[] photos = getPhotos(inDir);
		Media[] videos = getVideos(inDir);		
		logger.log(Level.CONFIG,"Checking Directory " + inDir);

		for (Media photograph : photos) {
			logger.log(Level.CONFIG,"File " + photograph.toString());
			// Get Date/Month from Picture
			File folder = getFolders(outDir, photograph);
			// Find matching month in folder
			logger.log(Level.CONFIG,"Looking in folder:" + folder);
			if (correctFolder(folder, photograph)) {
				// Move picture to folder
				logger.log(Level.INFO, "Moving "+photograph.getName() +" to "+folder.getAbsolutePath() );
				if (!test){
					if (photograph.renameTo(new File(folder.getAbsolutePath()+ "\\" + photograph.getName()))) {
						logger.log(Level.INFO,"File is moved successful!");
					} else {
						logger.log(Level.INFO,"File is failed to move!");
					}
				}
			}
		}
		
		for (Media video : videos) {
			logger.log(Level.CONFIG,"File " + video.toString());
			logger.log(Level.INFO, "Moving "+ video.getName() +" to "+videoDir );
			if (!test){
				if (video.renameTo(new File(videoDir+"\\" + video.getName().replaceAll(DOTMOV, "")+"-"+System.currentTimeMillis()+DOTMOV))) {
					logger.log(Level.INFO,"File is moved successful!");
				} else {
					logger.log(Level.INFO,"File is failed to move!");
				}
			}
		}
	}

	/**
	 * Inner Class Extends the File object for Photographs
	 * @author gfarnan
	 *
	 * @version $Revision: 1.0 $
	 */
	public class Media extends File {
		private String name;
		private String month;
		private String year;

		/**
		 * Constructor for Photograph.
		
		 * @param media File
		 */
		public Media(File media) {
			super(media.getPath());
			try {

				this.name = media.getName();
				BasicFileAttributes attributes = Files.readAttributes(media.toPath(), BasicFileAttributes.class);
				Date creationDate = new Date(attributes.lastModifiedTime().to(TimeUnit.MILLISECONDS));
				Calendar cal = Calendar.getInstance();
				cal.setTime(creationDate);
				this.month = new SimpleDateFormat("MMM").format(cal.getTime());
				this.year = new SimpleDateFormat("YYYY").format(cal.getTime());

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		/**
		 * Method toString.
		
		 * @return String */
		public String toString() {
			return this.name + ": Created on " + this.month + " " + this.year;
		}

		/**
		 * Method getName.
		
		 * @return String */
		public String getName() {
			return this.name;
		}

		/**
		 * Method getMonth.
		
		 * @return String */
		public String getMonth() {
			return this.month;
		}

		/**
		 * Method getYear.
		
		 * @return String */
		public String getYear() {
			return this.year;
		}

	}

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
			new MovePhotos().move();
	}

}
