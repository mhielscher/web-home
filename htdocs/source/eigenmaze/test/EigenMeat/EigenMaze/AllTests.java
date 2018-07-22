package EigenMeat.EigenMaze;

import junit.framework.*;

public class AllTests 
{
    public static void main( String[] args ) 
    {       
        if (args.length > 0 && args[0].startsWith( "-g" ) ) 
        {
            // Graphical
            String[] arguments = new String[]{ AllTests.class.getName() };
            junit.swingui.TestRunner.main( arguments );
        }      
        else 
        {
            // Textual
            TestResult testResult = junit.textui.TestRunner.run( suite() );
            System.exit( testResult.wasSuccessful() ? 0 : 1 );
        }
    }
	
	public static Test suite() 
    {
		TestSuite suite = new TestSuite( "EigenMaze Tests" );
		suite.addTestSuite( Vect3dTest.class );
		suite.addTestSuite( EntityTest.class );
		suite.addTestSuite( MazeTest.class );
		suite.addTestSuite( MeshTest.class );
		suite.addTestSuite( MeshLoaderTest.class );
		suite.addTestSuite( PhysicsTest.class );
		suite.addTestSuite( TriangleTest.class );
		//suite.addTestSuite( PlayerTest.class );
		suite.addTestSuite( PlaneTest.class );
		suite.addTestSuite( CameraTest.class );
		suite.addTestSuite( ProjectileTest.class );
		suite.addTestSuite( ParticleTest.class );
		suite.addTestSuite( Math3DTest.class );
		
		return suite;
	}
}
