import java.io.File;
//import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

/**
 * The class <code>MovePhotosTest</code> contains tests for the class <code>{@link MovePhotos}</code>.
 *
 * @generatedBy CodePro at 20/05/15 11:54
 * @author gfarnan
 * @version $Revision: 1.0 $
 */


public class MovePhotosTest {
	
	@Rule
	public TemporaryFolder folder= new TemporaryFolder();		
	File inFolder;
	File outFolder;		
	File monthFolder;	
	String inDir;
	String outDir;
	
	

	/**
	 * Run the MovePhotos() constructor test.
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	@Test
	public void testMovePhotos_AssertObjectNotNull()
		throws Exception {
		MovePhotos result = new MovePhotos();
		assertNotNull(result);
		// add additional test code here
	}
	

	/**
	 * Run the void move(String,String) method test.
	 * Test for Empty Input Folder
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	@Test
	public void testMove_BlankDirectories() throws Exception {
		MovePhotos fixture = new MovePhotos();
		fixture.move(inDir, outDir);
		
		assertTrue(monthFolder.listFiles().length==0);

		// add additional test code here
	}

	/**
	 * Run the void move(String,String) method test.
	 * One File but not a picture
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	@Test
	public void testMove_NoPicutureFiles() throws Exception {
		MovePhotos fixture = new MovePhotos();
		File.createTempFile( "Picture", "txt", inFolder);
		fixture.move(inDir, outDir);
		assertTrue(monthFolder.listFiles().length==0);
	}

	/**
	 * Run the void move(String,String) method test.
	 * 
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	@Test
	public void testMove_OnePicture() throws Exception {
		MovePhotos fixture = new MovePhotos();
		File picture = File.createTempFile( "Picture", "jpg", inFolder);
		picture.setLastModified(System.currentTimeMillis());
		fixture.move(inDir, outDir);
		assertTrue(monthFolder.listFiles().length==1);
		
	}

	/**
	 * Run the void move(String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	@Test
	public void testMove_4()
		throws Exception {
		MovePhotos fixture = new MovePhotos();
		String inDir = "";
		String outDir = "";

		fixture.move(inDir, outDir);

	}


	/**
	 * Perform pre-test initialization.
	 * Set up Temporary Files and Folders for Testing
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	@Before
	public void setUp() throws Exception {
		inFolder = folder.newFolder("in");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		String month = new SimpleDateFormat("MMM").format(cal.getTime());
		String year = new SimpleDateFormat("YYYY").format(cal.getTime());
		
		outFolder = folder.newFolder("out");
		monthFolder = folder.newFolder("out",year,month);
		
		inDir = inFolder.getPath();
		outDir = outFolder.getPath();

		
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 20/05/15 11:54
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(MovePhotosTest.class);
	}
}