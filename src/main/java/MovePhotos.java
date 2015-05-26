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
	private static final Logger logger = Logger.getLogger( MovePhotos.class.getName() );
	private static FileHandler fh = null;
	private String logfile;
	private String logLevel;	
	private Boolean test;
	private String inDir;
	private String outDir;
	
	
	
	public MovePhotos(){
	
		logfile = MovephotosProperties.getInstance().getProperty("logFile","logDetails.log");
		logLevel = MovephotosProperties.getInstance().getProperty("logLevel","CONFIG");
		test = Boolean.parseBoolean(MovephotosProperties.getInstance().getProperty("test","no"));
		inDir = MovephotosProperties.getInstance().getProperty("From_Folder","temp");
		outDir = MovephotosProperties.getInstance().getProperty("To_Folder","temp");
		
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
	private Photograph[] getFiles(String dir) {
		GenericExtFilter filter = new GenericExtFilter(JPG);
		return listFiles(dir, filter);
	}

	/**
	 * Filter a list of Files to return only Photographs
	
	 * @param filter
	
	 * @param dir String
	 * @return photographs */
	private Photograph[] listFiles(String dir, FilenameFilter filter) {
		File[] files = new File(dir).listFiles(filter);
		Photograph[] photographs = new Photograph[0];
		if (files != null) {
			logger.log(Level.CONFIG,"Number of photographs found:"+ files.length);
			photographs = new Photograph[files.length];
			for (int i = 0; i < files.length; i++) {
				photographs[i] = new Photograph(files[i]);
			}
		}
		return photographs;
	}

	/**
	 *  Return a list of Folders in a directory
	
	
	 * @param dir String
	 * @return folders */
	private File[] getFolders(String dir) {
		File[] files = new File(dir).listFiles();
		logger.log(Level.CONFIG,"Returning folders from " + dir);		
		File[] folders = new File[0];
		if (files != null) {
			folders = new File[files.length];
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					folders[i] = files[i];
				}
			}
		}
		return folders;
	}

	/**
	 * Check if a photograph should be placed in a folder
	 * @param folder
	 * @param photograph
	
	 * @return boolean */
	private boolean correctFolder(File folder, Photograph photograph) {
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
		Photograph[] photos = getFiles(inDir);
		logger.log(Level.CONFIG,"Checking Directory " + inDir);

		for (Photograph photograph : photos) {
			logger.log(Level.CONFIG,"File " + photograph.toString());
			// Get Date/Month from Picture
			File[] folders = getFolders(outDir + "\\" + photograph.getYear());
			logger.log(Level.CONFIG,"Returning "+folders.length+" folders from " + new File(outDir + "\\" + photograph.getYear()));

			for (File folder : folders) {
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
				else {
					logger.log(Level.INFO, "No Folder for "+photograph );
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
	public class Photograph extends File {
		private String name;
		private String month;
		private String year;

		/**
		 * Constructor for Photograph.
		 * @param photo File
		 */
		public Photograph(File photo) {
			super(photo.getPath());
			try {

				this.name = photo.getName();
				BasicFileAttributes attributes = Files.readAttributes(photo.toPath(), BasicFileAttributes.class);
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
	 * inner class, generic extension filter
	 * @author gfarnan
	 *
	 * @version $Revision: 1.0 $
	 */
	public class GenericExtFilter implements FilenameFilter {

		private String ext;

		/**
		 * Constructor for GenericExtFilter.
		 * @param ext String
		 */
		public GenericExtFilter(String ext) {
			this.ext = ext;
		}

		/**
		 * Method accept.
		 * @param dir File
		 * @param name String
		
		
		
		
		 * @return boolean * @see java.io.FilenameFilter#accept(File, String) * @see java.io.FilenameFilter#accept(File, String) * @see java.io.FilenameFilter#accept(File, String) * @see java.io.FilenameFilter#accept(File, String)
		 */
		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
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
