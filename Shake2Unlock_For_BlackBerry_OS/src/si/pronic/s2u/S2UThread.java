package si.pronic.s2u;

import net.rim.device.api.system.AccelerometerChannelConfig;
import net.rim.device.api.system.AccelerometerData;
import net.rim.device.api.system.AccelerometerSensor;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.AccelerometerSensor.Channel;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.UiApplication;

public class S2UThread extends Thread
{
	
	private boolean run;
	private long lastTime;
	private S2UApp app;
	public static LockScreen screen;
	
	public S2UThread(S2UApp app)
	{
		run = true;
		this.app = app;
   		if (screen == null)
   		{
   			screen = new LockScreen(app);
   		}
	}
	
	public void stop()
	{
		run = false;
	}
	
	public void run()
	{
        AccelerometerChannelConfig conf = new AccelerometerChannelConfig(AccelerometerChannelConfig.TYPE_RAW);
        lastTime = System.currentTimeMillis();
        
        if (conf != null)
        {
            conf.setBackgroundMode(true);
            conf.setSamplesCount(500);
        	Channel ch = AccelerometerSensor.openChannel(Application.getApplication(), conf);
        	
        	if (ch != null)
        	{
 	        	while (run)
	        	{
 	        		if (S2UProperties.running)
 	        		{
 		        		try
	 	        		{
		 	        		if (app.getActiveScreen() != null && app.getActiveScreen() instanceof LockScreen)
		 	        		{
		 	        			if (Backlight.isEnabled() && !S2UApp.usb)
		 	        			{
		 	        				Backlight.enable(false);
		 	        			}
		 	        		}
		 	        		
			        		AccelerometerData data = ch.getAccelerometerData();			        		
			        		process(data);
	 	        		}
		        		catch (Error ex)
		        		{
		        	   		EventLogger.logEvent(S2UApp.guid, ex.getMessage().getBytes(), EventLogger.ERROR);
		        		}
 	        		}
 	        		else if (app.getActiveScreen() instanceof LockScreen)
 	        		{
	 	       			app.invokeLater(new Runnable(){public void run(){
	 	   	    			while (app.getActiveScreen() != null)
	 	   	    			{
	 	   	    				app.popScreen(app.getActiveScreen());
	 	   	    			}
	 	    				return;}}); 			
	 	    			app.requestBackground();
	 	       			Backlight.enable(true);
 	        		}
	        		
	        		try 
	        		{
	    				Thread.sleep(S2UProperties.SLEEPS[S2UProperties.sleep]);
	    			} 
	        		catch (InterruptedException ex) 
	    			{
	        	   		EventLogger.logEvent(S2UApp.guid, ex.getMessage().getBytes(), EventLogger.ERROR);
	    			}
	        		catch (Error ex) 
	    			{
	        	   		EventLogger.logEvent(S2UApp.guid, ex.getMessage().getBytes(), EventLogger.ERROR);
	    			}
	        	}
	        	ch.close();  
        	}
        }
		EventLogger.logEvent(S2UApp.guid, "QUITING".getBytes(), EventLogger.ERROR);
    }
		
    private void process(AccelerometerData accData) throws Error
    {
    	short x[] = accData.getXAccHistory();
    	short y[] = accData.getYAccHistory();
    	short z[] = accData.getZAccHistory();
    	long times[] = accData.getSampleTsHistory();
    	
    	long currTime = System.currentTimeMillis();

    	boolean goingUp = true;
    	int max = 0;
    	int counter = 0;
    	
    	String str = "";
   	    	
   		for (int j = 0; j < x.length; j++)
    	{
    		if (times[j] - lastTime > S2UProperties.DELAYS[S2UProperties.delay] && currTime - times[j] < 1000 * S2UProperties.SHAKES[S2UProperties.nrOfShakes])
    		{
	    		int sqForce = (int)Math.sqrt(x[j] * x[j] + y[j] * y[j] + z[j] * z[j] - 
			   		AccelerometerSensor.G_FORCE_VALUE * AccelerometerSensor.G_FORCE_VALUE);
	    		str+= sqForce + " ";
	    		if (goingUp)
	    		{
		    		if (sqForce > max)
		    		{
		    			max = sqForce;
		    		}
		    		else
		    		{
		    			goingUp = false;
		    			if (max > S2UProperties.LEVELS[S2UProperties.detectionLevel])
		    			{
		    				counter++;
		    			}
		    			max = sqForce;
		    		}
	    		}
	    		else
	    		{
		    		if (sqForce > max)
		    		{
		    			goingUp = true;
		    		}
	    			max = sqForce;
	     		}
    		}
    	}
   	
    	if (counter >= S2UProperties.SHAKES[S2UProperties.nrOfShakes])
    	{
    		lastTime = System.currentTimeMillis();
    		EventLogger.logEvent(S2UApp.guid, ("SCORE " + counter + " " + 
    				Backlight.isEnabled()).getBytes(), EventLogger.ERROR);
    		if (Backlight.isEnabled())
    		{
    			app.invokeLater(new Runnable(){public void run(){
   	    			while (app.getActiveScreen() != null)
   	    			{
   	    				app.popScreen(app.getActiveScreen());
   	    			}
        			app.pushScreen(screen);
    				return;}});
    			app.requestForeground();
    			Backlight.enable(false);
    			Alert.startVibrate(200);
    		}
    		else
    		{
    			app.invokeLater(new Runnable(){public void run(){
   	    			while (app.getActiveScreen() != null)
   	    			{
   	    				app.popScreen(app.getActiveScreen());
   	    			}
    				return;}}); 			
    			app.requestBackground();
       			Backlight.enable(true);
    			Alert.startVibrate(200);
       		}
    	}
 
     }
}
