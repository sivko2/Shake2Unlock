package si.pronic.s2u;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

public final class S2UScreen extends MainScreen
{
	private CheckboxField runningBox;
	private ObjectChoiceField nrOfShakesChoiceBox;
	private ObjectChoiceField levelsChoiceBox;
	private ObjectChoiceField delayChoiceBox;
	private ObjectChoiceField sleepChoiceBox;
	private CheckboxField inBox;
	private CheckboxField outBox;
	
    public S2UScreen()
    {        
        setTitle("Shake 2 Unlock Settings");
        
        add(new LabelField(""));
        
        runningBox = new CheckboxField(" Shake 2 Unlock activation", S2UProperties.running);
        runningBox.setChangeListener(new FieldChangeListener()
		{
			public void fieldChanged(Field field, int context)
			{
				if (((CheckboxField)field).getChecked())
				{
					nrOfShakesChoiceBox.setEnabled(true);
					levelsChoiceBox.setEnabled(true);
					delayChoiceBox.setEnabled(true);
					sleepChoiceBox.setEnabled(true);
					inBox.setEnabled(true);
					outBox.setEnabled(true);
				}
				else
				{
					nrOfShakesChoiceBox.setEnabled(false);
					levelsChoiceBox.setEnabled(false);
					delayChoiceBox.setEnabled(false);
					sleepChoiceBox.setEnabled(false);
					inBox.setEnabled(false);
					outBox.setEnabled(false);
				}
			}
		});
        add(runningBox);
        
        add(new LabelField(""));
        
        String shakeChoices[] = new String[S2UProperties.SHAKES.length];
        for (int i = 0; i < shakeChoices.length; i++)
        {
        	shakeChoices[i] = "" + S2UProperties.SHAKES[i];
        }
        nrOfShakesChoiceBox = new ObjectChoiceField("Number of shakes:", shakeChoices, S2UProperties.nrOfShakes);
        add(nrOfShakesChoiceBox);

        add(new LabelField(""));
        
        String sensChoices[] = new String[S2UProperties.LEVELS.length];
        for (int i = 0; i < sensChoices.length; i++)
        {
        	sensChoices[i] = S2UProperties.LEVELS_TEXT[i];
        }
        levelsChoiceBox = new ObjectChoiceField("Sensitivity:", sensChoices, S2UProperties.detectionLevel);
        add(levelsChoiceBox);

        add(new LabelField(""));
        
        String delayChoices[] = new String[S2UProperties.DELAYS.length];
        for (int i = 0; i < delayChoices.length; i++)
        {
        	delayChoices[i] = "" + S2UProperties.DELAYS[i];
        }
        delayChoiceBox = new ObjectChoiceField("Responsiveness [ms]:", delayChoices, S2UProperties.delay);
        add(delayChoiceBox);

        add(new LabelField(""));
        
        String sleepChoices[] = new String[S2UProperties.SLEEPS.length];
        for (int i = 0; i < sleepChoices.length; i++)
        {
        	sleepChoices[i] = "" + S2UProperties.SLEEPS[i];
        }
        sleepChoiceBox = new ObjectChoiceField("Time between reads [ms]:", sleepChoices, S2UProperties.sleep);
        add(sleepChoiceBox);

        add(new LabelField(""));
        
        outBox = new CheckboxField(" Slide keyboard out to unlock", S2UProperties.holsterOutForUnlock);
        add(outBox);
        
        add(new LabelField(""));
        
        inBox = new CheckboxField(" Slide keyboard in to lock", S2UProperties.holsterInForLock);
        add(inBox);
     }
    
    protected void makeMenu(Menu menu, int instance)
    {
    	super.makeMenu(menu, instance);
    	if (instance != Menu.INSTANCE_CONTEXT)
    	{
//    		menu.add(inviteDLMenuItem);
    		menu.add(helpMenuItem);
    		menu.add(aboutMenuItem);
    	}
    }
    
    private MenuItem aboutMenuItem = new MenuItem(new StringProvider("About"), 110, 10)
    {
    	public void run()
    	{
    		String text = "SHAKE 2 UNLOCK v1.2.2\n\nLock your screen without even glancing at your smartphone\n\n" +
    				"(c)2012, Pronic\n";
    		Dialog.alert(text);
    	}
    }; 
    
    private MenuItem helpMenuItem = new MenuItem(new StringProvider("Help"), 110, 10)
    {
    	public void run()
    	{
    		String text = "Shake your smartphone few times to lock or unlock it. Another way is to press " +
    				"Lock key to lock or SHIFT + SPACE to unlock.\n\n" +
    				"Settings options:\n" +
    				"- Activation/deactivation of the application\n" +
    				"- Number of shakes\n" +
    				"- Sensitivity\n" +
    				"- Responsiveness\n" +
    				"- Time between reads\n" +
    				"- Slide keyboard out/in to (un)lock";
    		Dialog.alert(text);
    	}
    }; 
    
    protected boolean onSavePrompt()
    {
   		String options[] = {"Yes", "No"};
    	int pos = Dialog.ask("Do you want to save settings?", options, 0);
    	if (pos == 0)
    	{
	    	S2UProperties.running = runningBox.getChecked();
	    	S2UProperties.nrOfShakes = nrOfShakesChoiceBox.getSelectedIndex();
	    	S2UProperties.detectionLevel = levelsChoiceBox.getSelectedIndex();
	    	S2UProperties.delay = delayChoiceBox.getSelectedIndex();
	    	S2UProperties.sleep = sleepChoiceBox.getSelectedIndex();
	    	S2UProperties.holsterOutForUnlock = outBox.getChecked();
	    	S2UProperties.holsterInForLock = inBox.getChecked();
	    	S2UProperties.store();
    	}
    	return true;
    }
    
   	public void close()
    {
    	UiApplication.getUiApplication().requestBackground();
    }
}
