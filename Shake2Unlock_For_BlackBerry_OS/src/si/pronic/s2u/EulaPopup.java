package si.pronic.s2u;

import javax.microedition.lcdui.Font;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class EulaPopup extends PopupScreen 
{
	public EulaPopup(String titleValue, String textValue, String buttonName, final long pid)
	{
		super(new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR));
		
		LabelField title = new LabelField(titleValue);
		title.setFont(title.getFont().derive(Font.STYLE_BOLD));
		add(title);
		
		EditField eula = new EditField(READONLY | FOCUSABLE);
		eula.setText(textValue);
		add(eula);
		
		ButtonField acceptButton = new ButtonField(buttonName, ButtonField.CONSUME_CLICK | Field.FIELD_HCENTER);
		acceptButton.setChangeListener(new FieldChangeListener() 
		{
			public void fieldChanged(Field field, int context) 
			{
    	        PersistentObject persist = PersistentStore.getPersistentObject(pid);
	            persist.setContents(new Integer(1));
	            persist.commit();
				close();
			}
		});
		add(acceptButton);
	}

}
