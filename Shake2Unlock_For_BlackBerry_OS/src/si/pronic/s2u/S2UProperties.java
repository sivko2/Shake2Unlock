package si.pronic.s2u;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.ContentProtectedVector;

public class S2UProperties
{
	public static final long PERS_ID = 999911118823L;

	public static final int SHAKES[] = {2, 3, 4, 5};
	public static final int LEVELS[] = {1000, 1400, 1800, 2200, 2600, 3000, 3400};
	public static final String LEVELS_TEXT[] = {"Extremely high", "Very high", "High", "Medium", "Low", "Very low", "Extremely low"};
	public static final int DELAYS[] = {0, 1000, 2000, 3000, 5000, 10000, 30000};
	public static final int SLEEPS[] = {100, 500, 1000, 2000};

	public static boolean running = true;
	public static int nrOfShakes = 1;
	public static int detectionLevel = 2;
	public static int delay = 0;
	public static boolean holsterOutForUnlock = false;	
	public static boolean holsterInForLock = false;
	public static int sleep = 2;
	
	public static void store()
	{
		PersistentObject p = PersistentStore.getPersistentObject(PERS_ID);
		ContentProtectedVector vector = new ContentProtectedVector();
		vector.addElement(new Boolean(running));
		vector.addElement(new Integer(nrOfShakes));
		vector.addElement(new Integer(detectionLevel));
		vector.addElement(new Boolean(holsterOutForUnlock));
		vector.addElement(new Boolean(holsterInForLock));
		vector.addElement(new Integer(delay));
		vector.addElement(new Integer(sleep));
		p.setContents(vector);
		p.commit();
	}
	
	public static void load()
	{
 		PersistentObject p = PersistentStore.getPersistentObject(PERS_ID);
		if (p.getContents() == null || ((ContentProtectedVector)p.getContents()).size() < 7)
		{
			store();
		}
		ContentProtectedVector vector = (ContentProtectedVector)p.getContents();
		running = ((Boolean)vector.elementAt(0)).booleanValue();
		nrOfShakes = ((Integer)vector.elementAt(1)).intValue();
		detectionLevel = ((Integer)vector.elementAt(2)).intValue();
		holsterOutForUnlock = ((Boolean)vector.elementAt(3)).booleanValue();
		holsterInForLock = ((Boolean)vector.elementAt(4)).booleanValue();
		delay = ((Integer)vector.elementAt(5)).intValue();
		sleep = ((Integer)vector.elementAt(6)).intValue();
		p.setContents(vector);
		p.commit();
	}
}
