package si.pronic.s2u;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;

public class LockScreen extends MainScreen
{
	public S2UApp app;

    public LockScreen(S2UApp app)
    {        
    	this.app = app;
    	final S2UApp _app = app;
    	setBackground(BackgroundFactory.createSolidBackground(0x000000));
		addKeyListener(new KeyListener()
		{
			public boolean keyUp(int keycode, int time)
			{
				return false;
			}
			
			public boolean keyStatus(int keycode, int time)
			{
				return false;
			}
			
			public boolean keyRepeat(int keycode, int time)
			{
				return false;
			}
			
			public boolean keyDown(int keycode, int time)
			{
				return false;
			}
			
			public boolean keyChar(char key, int status, int time)
			{
				if (!S2UProperties.running)
				{
					return false;
				}
				if (key == Characters.SPACE)
				{
						
					if (((status & KeyListener.STATUS_SHIFT_LEFT) != 0) || ((status & KeyListener.STATUS_SHIFT_RIGHT) != 0))
					{
	  	    			while (_app.getActiveScreen() != null)
	   	    			{
	  	    				_app.popScreen(_app.getActiveScreen());
	   	    			}
	  	    			_app.requestBackground();
						Backlight.enable(true);
					}
				}
				return true;
			}
		});
    }
    
    protected boolean touchEvent(TouchEvent message)
    {
		app.requestForeground();
		Backlight.enable(false);
    	return true;
    }
    
    public void paint(Graphics g)
    {
    	super.paint(g);
    	g.setColor(0);
    	g.fillRect(0, 0, 800, 800);
    }
    
    public void close()
    {
    }
}
