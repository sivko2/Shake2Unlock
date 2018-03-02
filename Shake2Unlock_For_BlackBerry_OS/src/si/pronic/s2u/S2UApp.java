package si.pronic.s2u;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.system.Sensor;
import net.rim.device.api.system.SensorListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;

public class S2UApp extends UiApplication implements SensorListener
{
	public static final long PERSISTANCE_ID = 1234567890123459L;

	public static final long guid = 0x3f20c496bfc15ffaL;

	public static S2UThread thread;

	public static boolean transition = false;
	
	public static boolean usb = false;
	
	public static boolean backlight = false;
	
	public static void main(String[] args)
    {
        S2UProperties.load();
   		RuntimeStore appReg = RuntimeStore.getRuntimeStore();
	    S2UApp theApp = null;   
	    
    	if (args != null && args.length > 0 && args[0].equals("gui"))
    	{
   	    	final S2UApp finalApp = (S2UApp)appReg.waitFor(guid);

     		EventLogger.logEvent(S2UApp.guid, "STARTED AS GUI - REQ FOREGROUND".getBytes(), EventLogger.ERROR);
   	    	if (finalApp != null)
   	    	{
   	     		finalApp.invokeLater(new Runnable(){public void run(){
   	    			while (finalApp.getActiveScreen() != null)
   	    			{
   	    				finalApp.popScreen(finalApp.getActiveScreen());
   	    			}
   	     			finalApp.pushScreen(new S2UScreen());;
   	    	        PersistentObject persist = PersistentStore.getPersistentObject(PERSISTANCE_ID);
   	    	    	if (persist == null || persist.getContents() == null)
   	    	        {

   	    	        	EulaPopup popup = new EulaPopup("EULA", EULA_TEXT, "Accept", PERSISTANCE_ID);
   	    	        	finalApp.pushScreen(popup);
   	    	        }
    				return;}}); 				    		
   	     		finalApp.requestForeground();
   	    	}
   		}
    	else if (args != null && args.length > 0 && args[0].equals("lock"))
    	{
	     	EventLogger.logEvent(S2UApp.guid, "LOCKING".getBytes(), EventLogger.ERROR);
  	    	final S2UApp finalApp = (S2UApp)appReg.waitFor(guid);

   	    	if (finalApp != null)
   	    	{
	    		if (S2UThread.screen == null)
	    		{
	    			S2UThread.screen = new LockScreen(finalApp);
	    		}
	    		finalApp.invokeLater(new Runnable(){public void run(){
	    			while (finalApp.getActiveScreen() != null)
	    			{
	    				finalApp.popScreen(finalApp.getActiveScreen());
	    			}
	    			finalApp.pushScreen(S2UThread.screen);;
					return;}}); 				    		
	    		finalApp.requestForeground();
				Backlight.enable(false);
    		}
   		}
    	else
    	{
       		EventLogger.logEvent(S2UApp.guid, "STARTED AS DAEMON".getBytes(), EventLogger.ERROR);
			theApp = new S2UApp();
			final S2UApp _theApp = theApp;
			theApp.addKeyListener(new KeyListener()
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
 					if (Keypad.key(keycode) == Keypad.KEY_LOCK && S2UProperties.running && Backlight.isEnabled())
					{
 						_theApp.invokeLater(new Runnable(){public void run(){
 		   	    			while (_theApp.getActiveScreen() != null)
 		   	    			{
 		   	    				_theApp.popScreen(_theApp.getActiveScreen());
 		   	    			}
 		   	    			_theApp.pushScreen(S2UThread.screen);
 		    				return;}});
 						_theApp.requestForeground();
 		    			Backlight.enable(false);
 		    			return true;
					}
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
 		  	    			while (_theApp.getActiveScreen() != null)
 		   	    			{
 		  	    				_theApp.popScreen(_theApp.getActiveScreen());
 		   	    			}
 		  	    			_theApp.requestBackground();
 							Backlight.enable(true);
 						}
 					}
 					return true;
				}
			});
    		thread = new S2UThread(theApp);
    		thread.start();
	   	    synchronized(appReg)
    		{
    			appReg.put(guid, theApp);
    		}
	   	    theApp.enterEventDispatcher();			
    	}
    }
    
    protected void onExit()      
    {           
    	thread.stop();      
    } 
    
    public S2UApp()
    {   
    	addSystemListener(new SystemListener2()
		{
			
			public void powerUp()
			{
			}
			
			public void powerOff()
			{
			}
			
			public void batteryStatusChange(int status)
			{
			}
			
			public void batteryLow()
			{
			}
			
			public void batteryGood()
			{
			}
			
			public void usbConnectionStateChange(int state)
			{
				if (state == SystemListener2.USB_STATE_CABLE_CONNECTED)
				{
					usb = true;
				}
				else if (state == SystemListener2.USB_STATE_CABLE_DISCONNECTED)
				{
					usb = false;
				}
			}
			
			public void powerOffRequested(int reason)
			{
			}
			
			public void fastReset()
			{
			}
			
			public void cradleMismatch(boolean mismatch)
			{
				if (mismatch)
				{
					usb = true;
				}
				else
				{
					usb = false;
				}
			}
			
			public void backlightStateChange(boolean on)
			{
				if (!on)
				{
		       		EventLogger.logEvent(S2UApp.guid, "BACKLIGHT OFF ".getBytes(), EventLogger.ERROR);
		    		invokeLater(new Runnable(){public void run(){
		   	    		while (getActiveScreen() != null)
		   	    		{
		   	    			popScreen(getActiveScreen());
		   	    		}
		        		pushScreen(S2UThread.screen);
		    			return;}});
	    			requestForeground();
		   			Backlight.enable(false);
				}
			}
		});
		Sensor.addListener(this, this, Sensor.SLIDE); 
    }    

    public void onSensorUpdate(int sensorId, int update)   
    {      
    	switch(sensorId)      
    	{         
    		case Sensor.SLIDE:             
    			switch(update)
    			{               
    				case Sensor.STATE_SLIDE_IN_TRANSITION: 
    					transition = true;
    					break;
    				case Sensor.STATE_SLIDE_CLOSED:
    					if (transition)
    					{
    						transition = false;
    	   					if (S2UProperties.holsterInForLock && S2UProperties.running)
        					{
       			    			invokeLater(new Runnable(){public void run(){
    			   	    			while (getActiveScreen() != null)
    			   	    			{
    			   	    				popScreen(getActiveScreen());
    			   	    			}
    			        			pushScreen(S2UThread.screen);
    			    				return;}});
    			    			requestForeground();
    			    			Backlight.enable(false);
        					}
    					}
    					break;
    				case Sensor.STATE_SLIDE_OPEN:
    					if (transition)
    					{
    						transition = false;
         					if (S2UProperties.holsterOutForUnlock && S2UProperties.running)
        					{
	       				   			invokeLater(new Runnable(){public void run(){
	       			   	    			while (getActiveScreen() != null)
	       			   	    			{
	       			   	    				popScreen(getActiveScreen());
	       			   	    			}
        			    				return;}}); 			
           							requestBackground();
        							Backlight.enable(true);
        					}
    					}
    					break;
    			}
    			break;
    	}
    }

    public static final String EULA_TEXT =
		"SHAKE 2 UNLOCK - END USER AGREEMENT AND LICENSE\n\n" +
		"You may use this software product only on the condition that you agree to abide by the following terms.\n\n" +
		"BY INSTALLING OR USING THIS SOFTWARE, YOU ARE AGREEING ELECTRONICALLY TO THE TERMS OF THIS SOFTWARE END USER AGREEMENT (THE 'AGREEMENT' or 'LICENSE'). If you do not agree to the terms of this License, do not install, copy or use the Software. Also, you agree that any claim or dispute that you may have regarding this Agreement or the Software resides in the Courts of the Republic of Slovenia.\n\n" +
		"1.  SOFTWARE.  This Agreement and the supplemental terms below apply to the software product and any updates for SHAKE 2 UNLOCK (hereinafter referred to as the 'Software').  In this Agreement, the term 'you' or 'your' means you as an individual or such entity in whose behalf you act, if any.\n\n" +
		"2.  OWNERSHIP.  This is a license of the Software and not a sale. The Software is protected by copyright and other intellectual property laws and by international treaties. The Author of the Software and suppliers own all rights in the Software. Your rights to use the Software are specified in this Agreement and we retain and reserve all rights not expressly granted to you.\n\n" +
		"3.  LICENSE.  Provided that you comply with the terms of this Agreement, we grant you a personal, limited, non-exclusive and non-transferable license to install and use the Software on a single, authorized BlackBerry® device for personal and internal business purposes. This license does not entitle you to receive from us hard-copy documentation, support, telephone assistance, or enhancements or updates to the Software.\n\n" +
		"4.  RESTRICTIONS.  You may not: (i) make any copies of the Software other than an archival copy, (ii) modify or create any derivative works of the Software or documentation; (iii) decompile, disassemble, reverse engineer, or otherwise attempt to derive the source code, underlying ideas, or algorithms of the Software, or in any way ascertain, decipher, or obtain the communications protocols for accessing our networks; (iv) copy, reproduce, reuse in another product or service, modify, alter, or display in any manner any files, or parts thereof, included in the Software;\n\n" +
		"The Software is offered in the United States. You understand and agree that (a) the Software is not designed or customized for distribution for any specific country or jurisdiction ('Territory') and (b) the Software is not intended for distribution to, or use by, any person or entity in any Territory where such distribution or use would be contrary to local law or regulation. You are solely responsible for compliance with local laws as applicable when you use the Software.\n\n" +
		"5. CONTENT.  Content, information ('Content') that may be accessed through the use of the Software is the property of its respective owner. You may only use such Content for personal, noncommercial purposes and subject to the terms and conditions that accompany such Content. We make no representations or warranties regarding the accuracy or reliability of the information included in such Content.\n\n" +
		"6. CONTENT DISCLAIMER. All of the content and information within SHAKE 2 UNLOCK, (the 'Content') is provided 'AS IS' and for your convenience only.  RIM, ITS AFFILIATES, ITS INFORMATION PROVIDERS, AIRTIME SERVICE PROVIDERS/TELECOMMUNICATIONS CARRIERS, AND ANY MoR MAKING THE SOFTWARE AVAILABLE THROUGH ITS KIOSK MAKE NO EXPRESS OR IMPLIED WARRANTIES (INCLUDING, WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABLILITY, ACCURACY OR FITNESS FOR A PARTICULAR PURPOSE OR USE) REGARDING ANY CONTENT.\n\n" +
		"RIM, any telecommunications carriers, its information providers, and any MoR shall not be liable for any decisions you make based upon use of the Content. In addition, RIM, including its affiliates and information or content providers, any telecommunications carriers, and any MoR, will not be liable to anyone for any interruption, inaccuracy, error or omission, regardless of cause, or for any resulting damages (whether direct or indirect, consequential, punitive or exemplary).\n\n" +
		"7.  DISCLAIMER OF WARRANTY.\n\n" +
		"WE LICENSE THE SOFTWARE 'AS IS' AND WITH ALL FAULTS. WE DO NOT WARRANT THAT THIS SOFTWARE WILL MEET YOUR REQUIREMENTS OR THAT ITS OPERATION WILL BE UNINTERRUPTED OR ERROR-FREE. THE ENTIRE RISK AS TO SATISFACTORY QUALITY, PERFORMANCE, ACCURACY, EFFORT AND COST OF ANY SERVICE AND REPAIR IS WITH YOU.\n\n" +
		"WE, OUR SUPPLIERS AND DISTRIBUTORS DISCLAIM ALL EXPRESS WARRANTIES AND ALL IMPLIED WARRANTIES, INCLUDING ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, NON-INTERFERENCE, NON-INFRINGEMENT OR ACCURACY, UNLESS SUCH IMPLIED WARRANTIES ARE LEGALLY INCAPABLE OF EXCLUSION.\n\n" +
		"NO ORAL OR WRITTEN INFORMATION OR ADVICE GIVEN BY US SHALL CREATE A WARRANTY OR IN ANY WAY INCREASE THE SCOPE OF ANY WARRANTY THAT CANNOT BE DISCLAIMED UNDER APPLICABLE LAW. WE, OUR SUPPLIERS AND DISTRIBUTORS HAVE NO LIABILITY WITH RESPECT TO YOUR USE OF THE SOFTWARE.\n\n" +
		"IF ANY IMPLIED WARRANTY MAY NOT BE DISCLAIMED UNDER APPLICABLE LAW, THEN SUCH IMPLIED WARRANTY IS LIMITED TO 30 DAYS FROM THE DATE YOU ACQUIRED THE SOFTWARE FROM US OR OUR AUTHORIZED DISTRIBUTOR.\n\n" +
		"8.  LIMITATION OF LIABILITY.\n\n" +
		"TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, IN NO EVENT WILL WE, OUR DISTRIBUTORS, CHANNEL PARTNERS, AND ASSOCIATED SERVICE PROVIDERS, BE LIABLE FOR ANY INDIRECT, SPECIAL, INCIDENTAL, CONSEQUENTIAL, OR EXEMPLARY DAMAGES ARISING OUT OF OR IN ANY WAY RELATING TO THIS AGREEMENT OR THE USE OF OR INABILITY TO USE THE SOFTWARE, INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS OF GOODWILL, WORK STOPPAGE, LOST PROFITS, LOSS OF DATA, COMPUTER OR DEVICE FAILURE OR MALFUNCTION, OR ANY AND ALL OTHER COMMERCIAL DAMAGES OR LOSSES, EVEN IF ADVISED OF THE POSSIBILITY THEREOF, AND REGARDLESS OF THE LEGAL OR EQUITABLE THEORY (CONTRACT, TORT OR OTHERWISE) UPON WHICH THE CLAIM IS BASED.\n\n" +
		"9.  NO SUPPORT OR UPGRADE OBLIGATIONS.  We, our suppliers and distributors are not obligated to create or provide any support, corrections, updates, upgrades, bug fixes and/or enhancements of the Software.\n\n" +
		"10.  IMPORT/EXPORT CONTROL.  The Software is subject to export and import laws, regulations, rules and orders of the United States and foreign nations. You must comply with these laws that apply to the Software. You may not directly or indirectly export, re-export, transfer, or release the Software, any other commodities, software or technology received from us, or any direct product thereof, for any proscribed end-use, or to any proscribed country, entity or person (wherever located), without proper authorization from the U.S. and/or foreign government.\n\n" +
		"11.  U.S. GOVERNMENT END-USERS.  The Software is a 'commercial item,' as that term is defined in 48 C.F.R. 2.101, consisting of 'commercial computer software' and 'commercial computer software documentation,' as such terms are used in 48 C.F.R. 12.212 (Sept. 1995) and 48 C.F.R. 227.7202 (June 1995). Consistent with 48 C.F.R. 12.212, 48 C.F.R. 27.405(b) (2) (June 1998) and 48 C.F.R. 227.7202, all U.S. Government End Users acquire the Software with only those rights as described in this License.\n\n" +
		"12.  ELECTRONIC NOTICES.  YOU AGREE TO THIS LICENSE ELECTRONICALLY. YOU AUTHORIZE US TO PROVIDE YOU ANY INFORMATION AND NOTICES REGARDING THE SOFTWARE ('NOTICES') IN ELECTRONIC FORM. WE MAY PROVIDE NOTICES TO YOU (1) VIA E-MAIL IF YOU HAVE PROVIDED US WITH A VALID EMAIL ADDRESS OR (2) BY POSTING THE NOTICE ON A WEB OR MOBILE PAGE DESIGNATED BY US FOR THIS PURPOSE. The delivery of any Notice is effective when sent or posted by us, regardless of whether you read the Notice or actually receive the delivery. You can withdraw your consent to receive Notices electronically by discontinuing your use of the Software.\n\n" +
		"13.  INDEMNIFICATION.  Upon a request by us, you agree to defend, indemnify, and hold harmless us and other affiliated companies, and our respective employees, contractors, officers, directors, suppliers and agents and distributors from all liabilities, claims, and expenses, including attorney's fees that arise from your use or misuse of the Software. We reserve the right, at our own expense, to assume the exclusive defense and control of any matter otherwise subject to indemnification by you, in which event you will cooperate with us in asserting any available defenses.\n\n" +
		"14. CHOICE OF LAW AND LOCATION FOR RESOLVING DISPUTES. YOU EXPRESSLY AGREE THAT EXCLUSIVE JURISDICTION FOR ANY CLAIM OR DISPUTE RELATING IN ANY WAY TO YOUR USE OF THE SOFTWARE RESIDES IN THE FEDERAL OR STATE COURTS LOCATED IN THE COMMONWEALTH OF CALIFORNIA AND YOU FURTHER AGREE AND EXPRESSLY CONSENT TO THE EXERCISE OF PERSONAL JURISDICTION IN SUCH COURTS IN CONNECTION WITH ANY SUCH DISPUTE INCLUDING ANY CLAIM INVOLVING the software. PLEASE NOTE THAT BY AGREEING TO THESE TERMS OF USE, YOU ARE WAIVING CLAIMS THAT YOU MIGHT OTHERWISE HAVE AGAINST US BASED ON THE LAWS OF OTHER JURISDICTIONS, INCLUDING YOUR OWN.\n\n" +
		"15.  ENTIRE AGREEMENT.  This Agreement and any supplemental terms constitute the entire agreement between you and us concerning the subject matter of this Agreement, which may only be modified by us.\n\n" +
		"16.  GENERAL TERMS.  (a) This Agreement shall not be governed by the United Nations Convention on Contracts for the International Sale of Goods. (b) If any part of this Agreement is held invalid or unenforceable, that part shall be construed to reflect the parties' original intent, and the remaining portions remain in full force and effect, or we may at our option terminate this Agreement. (c) The controlling language of this Agreement is English. If you have received a translation into another language, it has been provided for your convenience only. (d) A waiver by either party of any term or condition of this Agreement or any breach thereof, in any one instance, shall not waive such term or condition or any subsequent breach thereof. (e) You may not assign or otherwise transfer by operation of law or otherwise this Agreement or any rights or obligations herein. We may assign this Agreement to any entity at its sole discretion and without notice to you. (f) This Agreement shall be binding upon and shall inure to the benefit of the parties, their successors and permitted assigns. (g) Neither party shall be in default or be liable for any delay, failure in performance or interruption of service resulting directly or indirectly from any cause beyond its reasonable control.\n\n" +
		"17.  USER OUTSIDE THE U.S.  If you are using the Software outside the U.S., then the provisions of this Section shall apply: (i) Les parties aux prA©sentA©s confirment leur volontA© que cette convention de mAame que tous les documents y compris tout avis qui s'y rattachA©, soient redigA©s en langue anglaise. (translation: 'The parties confirm that this Agreement and all related documentation is and will be in the English language.'); (ii) you are responsible for complying with any local laws in your jurisdiction which might impact your right to import, export or use the Software, and you represent that you have complied with any regulations or registration procedures required by applicable law to make this license enforceable; and (iii) if the laws applicable to your use of the Software would prohibit the enforceability of this Agreement, or confer any rights to you that are materially different from the terms and conditions of this Agreement, then you are not authorized to use the Software and you agree to remove it from your device.\n\n" +
		"Supplemental Terms for BLACKBERRY®, airtime service providers, and MoRs.\n\n" +
		"These terms supplement and are in addition to the terms of the Agreement for users who install the Software on hardware products provided by Research In Motion, Limited ('RIM'), airtime service providers, and any MoRs.\n\n" +
		"a. You understand and agree that RIM, airtime service providers, and any MoRs have no obligation whatsoever to furnish any maintenance and support services regarding the Software.\n\n" +
		"b. RIM, airtime service providers, and any MoRs shall not be responsible for any claims by you or any third relating to your possession and/or use of the Software, including but not limited to (i) product liability claims, (ii) any claim that the Software fails to conform to any applicable legal or regulatory requirement, (iii) claims arising under consumer protection laws or similar legislation, and (iv) claims by any third party that the Software or your possession and use of the Software infringes the intellectual property rights of the third party.\n\n" +
		"c. You agree that RIM, RIM's subsidiaries, airtime service providers, and any MoRs are third party beneficiaries of this Agreement, and that upon your acceptance of the terms and conditions of this License, RIM will have the right (and will be deemed to have accepted the right) to enforce this Agreement against you as a third party beneficiary thereof.";
}
