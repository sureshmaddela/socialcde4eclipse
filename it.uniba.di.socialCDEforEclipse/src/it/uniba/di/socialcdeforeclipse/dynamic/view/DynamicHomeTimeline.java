package it.uniba.di.socialcdeforeclipse.dynamic.view;

import java.awt.font.TextMeasurer;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import it.uniba.di.socialcdeforeclipse.action.ActionDynamicUserTimeline;
import it.uniba.di.socialcdeforeclipse.action.ActionGeneral;
import it.uniba.di.socialcdeforeclipse.action.ActionHomeTimeline;
import it.uniba.di.socialcdeforeclipse.controller.Controller;
import it.uniba.di.socialcdeforeclipse.shared.library.WPost;
import it.uniba.di.socialcdeforeclipse.views.Panel;

public class DynamicHomeTimeline implements Panel{

	private WPost[] posts; 
	private ArrayList<Control> controlli;
	private Listener azioni; 
	private ScrolledComposite superUserPostMaster; 
	private Composite otherPostWarning; 
	private Link otherPostAvailable; 
	private static Composite	userPostMaster; 
	private Text textMessage; 
	private Label labelDownloadPost;
	private ProgressBar pbar;

	
	private Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getCurrent(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0,
		image.getBounds().width, image.getBounds().height,
		0, 0, width, height);
		gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
		}
	
	public Image get_ImageStream(InputStream stream)
	{
		return  new Image(Controller.getWindow().getDisplay(),stream); 
	}
	
	@Override
	public void inizialize(Composite panel) {
		// TODO Auto-generated method stub
		System.out.println("Home timeline lanciato");
		
		
		GridData gridData; 
		controlli = new ArrayList<Control>();
		azioni = new ActionGeneral();
		panel.setLayout(new GridLayout(1, false)); 
	
		
		//Controller.setScrollHeight(Controller.getWindowHeight() + 100); 
	    //((ScrolledComposite)	Controller.getWindow().getParent()).setMinSize(Controller.getWindowWidth(), SWT.DEFAULT);

	   
	    
	   
		
		superUserPostMaster = new ScrolledComposite(panel, SWT.V_SCROLL); 
		gridData = new GridData(); 
		gridData.grabExcessHorizontalSpace = true; 
		gridData.horizontalAlignment = GridData.FILL; 
		gridData.heightHint = 350;
		superUserPostMaster.setLayoutData(gridData); 
		controlli.add(superUserPostMaster); 
		
		
		userPostMaster = new Composite(superUserPostMaster, SWT.None);
		
		superUserPostMaster.setContent(userPostMaster);
		superUserPostMaster.setExpandVertical(true);
		superUserPostMaster.setExpandHorizontal(true);
		superUserPostMaster.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE)); 
		
		controlli.add(userPostMaster); 
		
		superUserPostMaster.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				
				superUserPostMaster.setMinSize(Controller.getWindowWidth()-10, (120 * posts.length)); 
				
			}} ); 
	    
		
		
		userPostMaster.setLayout(new GridLayout(2,false)); 
	
		userPostMaster.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE)); 
		
		
		
		posts = Controller.getProxy().GetHomeTimeline(Controller.getCurrentUser().Username, Controller.getCurrentUserPassword()); 
		
		System.out.println("numero di post " + posts.length); 

		ActionHomeTimeline.setLastId(0); 
		
		for(int i=0;i< posts.length; i++)
		{
			
			final Composite userPostComposite = new Composite(userPostMaster, SWT.None);
			final int j = i; 
			
			Display.getCurrent().syncExec(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					userPostComposite.setLayout(new GridLayout(2, false)); 
					userPostComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
					userPostComposite.setBackgroundMode(SWT.INHERIT_DEFAULT); 
					GridData  gridData = new GridData(); 
					gridData.grabExcessHorizontalSpace = true; 
					gridData.horizontalAlignment = GridData.FILL; 
					userPostComposite.setLayoutData(gridData); 
					
					controlli.add(userPostComposite);
					
					Label labelUserAvatar = new Label(userPostComposite,SWT.NONE); 
					gridData = new GridData();
					gridData.verticalSpan = 3; 
					labelUserAvatar.setLayoutData(gridData); 
					labelUserAvatar.setData("ID_action", "labelAvatar");
					 
					
						try {
							labelUserAvatar.setImage(get_ImageStream(new URL(posts[j].getUser().Avatar).openStream()));
							labelUserAvatar.setImage(resize(labelUserAvatar.getImage(), 75, 75)); 
							 
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Eccezione lanciata"); 
							labelUserAvatar.setImage(resize(get_ImageStream(this.getClass().getClassLoader().getResourceAsStream("images/DefaultAvatar.png")),75,75));
							
							//e.printStackTrace();
						} 
						catch (NullPointerException e) {
							// TODO: handle exception
							System.out.println("Eccezione lanciata 2");
							labelUserAvatar.setImage(get_ImageStream(this.getClass().getClassLoader().getResourceAsStream("images/DefaultAvatar.png"))); 
							labelUserAvatar.setImage(resize(labelUserAvatar.getImage(), 75, 75));
						}
						System.out.println("Fuori");
						
					Label username = new Label(userPostComposite, SWT.None); 
					username.setText(posts[j].getUser().Username); 
					username.setFont(new Font(Controller.getWindow().getDisplay(),"Calibri", 15, SWT.BOLD ));  
					username.setForeground(new Color(Display.getCurrent(), 97,91,91));
					gridData = new GridData(); 
					gridData.grabExcessHorizontalSpace = false; 
					gridData.horizontalAlignment = GridData.BEGINNING;
					username.setLayoutData(gridData); 
					
					Label message = new Label(userPostComposite, SWT.None | SWT.WRAP); 
					message.setText(posts[j].getMessage()); 
					gridData = new GridData(); 
					gridData.grabExcessHorizontalSpace = true; 
					gridData.horizontalAlignment = GridData.FILL; 
					gridData.widthHint = 350; 
					message.setLayoutData(gridData); 
					
					Calendar nowDate = Calendar.getInstance(); 
					Calendar dateSelected = posts[j].getCreateAt(); 
					long millisDiff = nowDate.getTime().getTime() - dateSelected.getTime().getTime();
					
					int seconds = (int) (millisDiff / 1000 % 60);
					int minutes = (int) (millisDiff / 60000 % 60);
					int hours = (int) (millisDiff / 3600000 % 24);
					int days = (int) (millisDiff / 86400000);
					
					Label messageDate = new Label(userPostComposite, SWT.None); 
					messageDate.setForeground(new Color(Display.getCurrent(), 140,140,140));
					
					if(days > 1 && days < 30)
					{
						messageDate.setText("About " + days + " days ago from " + posts[j].getService().getName());
					}
					else if(days > 30)
					{
						messageDate.setText("More than one month ago from " + posts[j].getService().getName());
					}
					else if(days == 1)
					{
						messageDate.setText("About " + days + " day ago from " + posts[j].getService().getName());
					}
					else
					{
						if( hours > 1)
						{
							messageDate.setText("About " + hours + " hours ago from " + posts[j].getService().getName());
						}
						else if(hours == 1)
						{
							messageDate.setText("About " + hours + " hour ago from " + posts[j].getService().getName());
						}
						else
						{

							if( minutes > 1)
							{
								messageDate.setText("About " + minutes + " minutes ago from " + posts[j].getService().getName());
							}
							else if(minutes == 1)
							{
								messageDate.setText("About " + minutes + " minute ago from " + posts[j].getService().getName());
							}
							else
							{

								if( seconds > 1)
								{
									messageDate.setText("About " + seconds + " seconds ago from " + posts[j].getService().getName());
								}
								else if(seconds == 1)
								{
									messageDate.setText("About " + seconds + " second ago from " + posts[j].getService().getName());
								}
								else
								{
									messageDate.setText("Few seconds ago from " + posts[j].getService().getName());
								}
							}
						}
					}
					 
					messageDate.setFont(new Font(Controller.getWindow().getDisplay(),"Calibri", 8, SWT.ITALIC ));
					gridData = new GridData(); 
					gridData.horizontalAlignment = GridData.BEGINNING; 
					messageDate.setLayoutData(gridData); 
					
					Label labelhidden = new Label(userPostMaster, SWT.None); 
					labelhidden.setText(""); 
					labelhidden.setVisible(false); 
					
					Label barSeparator = new Label(userPostComposite, SWT.BORDER);
					gridData = new GridData();
					gridData.widthHint = 100;
					gridData.heightHint = 1;
					gridData.horizontalSpan = 2; 
					gridData.horizontalAlignment = GridData.CENTER;
					barSeparator.setLayoutData(gridData);
				}
			});
			
			
			
			
			
			
			
			ActionHomeTimeline.setLastId(posts[i].Id);
			
			
			
		}
		
		System.out.println("step1"); 
		otherPostWarning = new Composite(userPostMaster, SWT.None); 
		otherPostWarning.setLayout(new GridLayout(1,false)); 
		otherPostWarning.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		gridData = new GridData(); 
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true; 
		gridData.horizontalAlignment = GridData.FILL; 
		otherPostWarning.setLayoutData(gridData);
		
		WPost[] newPost = Controller.getProxy().GetHomeTimeline(Controller.getCurrentUser().Username, Controller.getCurrentUserPassword(), ActionDynamicUserTimeline.getLastId(),0);
		
		if(newPost == null)
		{
			newPost = new WPost[0]; 
		}
	
		
		
		if(newPost.length > 0)
		{
			otherPostAvailable = new Link(otherPostWarning, SWT.NONE); 
			otherPostAvailable.setCursor( new Cursor(panel.getDisplay(), SWT.CURSOR_HAND));
			otherPostAvailable.setFont(new Font(Controller.getWindow().getDisplay(),"Calibri", 10, SWT.UNDERLINE_LINK));
			otherPostAvailable.setText("<a>Click to view older posts</a>"); 
			otherPostAvailable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE)); 
			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true; 
			gridData.horizontalAlignment = GridData.CENTER;
			otherPostAvailable.setLayoutData(gridData); 
			
			otherPostAvailable.addListener(SWT.Selection, azioni); 
			otherPostAvailable.setData("ID_action", "otherPostAvailable");
			
			
			
		}
		else
		{
			Label noPostAvailable = new Label(otherPostWarning,SWT.NONE); 
			noPostAvailable.setText("There are no older post available.");
			noPostAvailable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE)); 
			noPostAvailable.setFont(new Font(Controller.getWindow().getDisplay(),"Calibri", 10, SWT.None));
			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true; 
			gridData.horizontalAlignment = GridData.CENTER;
			noPostAvailable.setLayoutData(gridData); 
		}
		

		System.out.println("Step2"); 

		
	
		
		superUserPostMaster.setMinSize(Controller.getWindowWidth()-10, (120 * posts.length));
		  
		
		controlli.add(userPostMaster);
		
		 labelDownloadPost = new Label(panel, SWT.None); 
		    labelDownloadPost.setText("Download older posts.."); 
		    gridData = new GridData(); 
		    gridData.horizontalAlignment = GridData.CENTER; 
		    gridData.widthHint = 130; 
		    labelDownloadPost.setLayoutData(gridData); 
		    labelDownloadPost.setVisible(false);
		    pbar = new ProgressBar(panel, SWT.None); 
		    pbar.setVisible(false); 
		    pbar.setLayoutData(gridData); 
		
		Label labelHidden = new Label(panel, SWT.None);
		labelHidden.setText(""); 
		labelHidden.setVisible(false); 
		
		
	    Composite controlToPost = new Composite(panel, SWT.None);
	    controlToPost.setLayout(new GridLayout(2,false)); 
		gridData = new GridData(); 
		gridData.grabExcessHorizontalSpace = true; 
		gridData.horizontalAlignment = GridData.FILL; 
		controlToPost.setLayoutData(gridData); 
		controlli.add(controlToPost);
		
		
		
		
	    textMessage = new Text(controlToPost, SWT.WRAP); 
		gridData = new GridData(); 
		gridData.heightHint = 75;
		gridData.widthHint = Controller.getWindowWidth() - 100; 
		//textMessage.setBackgroundImage( resize(get_ImageStream(this.getClass().getClassLoader().getResourceAsStream("images/baloon.png")), Controller.getWindowWidth() - 95, 75));
		textMessage.setBackgroundImage(get_ImageStream(this.getClass().getClassLoader().getResourceAsStream("images/baloon.png")));
		textMessage.setLayoutData(gridData); 
		
		
		
		Label btnSendMessage = new Label(controlToPost, SWT.None); 
		btnSendMessage.setImage(resize(get_ImageStream(this.getClass().getClassLoader().getResourceAsStream("images/send_message.png")),48,48)); 
		btnSendMessage.setCursor( new Cursor(panel.getDisplay(), SWT.CURSOR_HAND)); 
		btnSendMessage.setToolTipText("Send message"); 
		btnSendMessage.setData("ID_action", "btnSendMessage");
		btnSendMessage.addListener(SWT.MouseDown, azioni); 
		
		System.out.println("Step3"); 
		
	panel.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				// TODO Auto-generated method stub
				//superUserPostMaster.setBounds(Controller.getWindow().computeTrim(0, 0, Controller.getWindowWidth()-10, 400));
				
				superUserPostMaster.setSize(Controller.getWindow().computeSize(Controller.getWindowWidth()-20, 350));
				userPostMaster.setSize(Controller.getWindow().computeSize(Controller.getWindowWidth()-70, 350));
				 
				//textMessage.setBackgroundMode(SWT.INHERIT_DEFAULT); 
				//try1.setBounds(Controller.getWindow().computeTrim(5, superUserPostMaster.getClientArea().height + 150, 542, 20)); 
			 
			
			//System.out.println("Dimen post " + controlToPost.getBounds()); 
			//System.out.println("Dimensioni scritta " + try1.getBounds()); 
			
			 
			}
		}); 
	
	Controller.setWindowName("homeTimeline"); 
	}

	@Override
	public void dispose(Composite panel) {
		// TODO Auto-generated method stub
		for(int i=0; i < controlli.size();i++)
		{
			final int j=i;
			
			Display.getCurrent().syncExec(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					controlli.get(j).dispose(); 
				}
			}); 
			
		 
			
		}
	}

	

	@Override
	public HashMap<String, Object> getData() {
		// TODO Auto-generated method stub
		HashMap<String, Object> uiData = new HashMap<String, Object>();
		
		uiData.put("superUserPostMaster", superUserPostMaster);
		uiData.put("userPostMaster", userPostMaster); 
		uiData.put("otherPostWarning", otherPostWarning); 
		uiData.put("userPostMaster", userPostMaster); 
		uiData.put("textMessage", textMessage);
		uiData.put("labelDownloadPost", labelDownloadPost); 
		uiData.put("pbar", pbar);   
		uiData.put("action",azioni); 
		
		
		
		return uiData;
	}

}
