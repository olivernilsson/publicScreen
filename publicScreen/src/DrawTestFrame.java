import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


//Vi vill hï¿½mta kordinaterna frï¿½n databasen och rita ut dem pï¿½ skï¿½rmen.
public class DrawTestFrame extends JFrame implements KeyEventDispatcher {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tempurl = "";
	private boolean legitWin = false;

	//används för att göra en by rad i chatten
	String splitter= "\n";
	//håller den genererade URLen för att gå ner i databasen
	String prevtempurl ="";
	String line = "---------------------------------------------------" ;
	
	private JPanel contentPane;
	
	String firebaseURL = "https://brilliant-fire-8250.firebaseio.com/";
	
	Firebase firebase = new Firebase(firebaseURL + "draw/");
	Firebase firebasechat = new Firebase(firebaseURL + "chat/");
	Firebase firebasedraw = new Firebase(firebaseURL);
	
	JTextPane chat = new JTextPane();
	StyledDocument doc = chat.getStyledDocument();
	
	SimpleAttributeSet chatAuthor = new SimpleAttributeSet();
	SimpleAttributeSet chatMsg = new SimpleAttributeSet();
	SimpleAttributeSet chatGame = new SimpleAttributeSet();
	
	
	
	
	DefaultCaret caret = (DefaultCaret)chat.getCaret();
	TextField field = new TextField();
	final JScrollPane scrolll = new JScrollPane(chat);
	String roundWinner ="";
	String selectedWord ="";
	JLabel label = new JLabel("", SwingConstants.CENTER);
	JLabel label2 = new JLabel("", SwingConstants.CENTER);
	JLabel labelGameOver = new JLabel("", SwingConstants.CENTER);
	JLabel winnerLabel = new JLabel();
	JLabel wordLabel = new JLabel();
	JLabel timerFrame = new JLabel("", SwingConstants.CENTER);
	JLabel star1 = new JLabel();
	JLabel star2 = new JLabel();
	JLabel star3 = new JLabel();
	JPanel panel = new JPanel();
	JLabel gameOverSkull = new JLabel();
	
	JLabel frameTop = new JLabel();
	
	boolean chickenChecker;
	Color colorBlue = new Color(4, 154, 149);
	Color colorOrange = new Color(221, 141, 2);
	Color colorLightOrange = new Color(255, 215, 146);
	Color chatTextColor = new Color(100, 95, 88);
	
	Color drawColorRed = new Color(192, 69, 69);
	Color drawColorTransparent = new Color(0, 0, 0, 0); //transparent color for fix to broken lines
	Color drawColorYellow = new Color(192, 185, 69);
	Color drawColorGreen = new Color(127, 192, 69);
	Color drawColorBlue = new Color(69, 159, 192);
	Color drawColorPurple = new Color(150, 69, 192);
	Color drawColorPink = new Color(192, 69, 150);
	String currentDraw ="";
	boolean drawTimedOut = false;
	boolean startUp = true;
	int prevX;
	int prevY;
	private int PrevX = 100 ,PrevY = 100 ,PrevWidth = 480,PrevHeight = 640;
	
	private boolean inFullScreenMode = false;
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	//change to vector
   Vector <String> author = new Vector <String> ();
   Vector<String> msg = new Vector<String>();
   
	//vector som håller det som ska ritas ut
	private Vector<Drawing> users = new Vector<Drawing>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DrawTestFrame frame = new DrawTestFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}

	/**
	 * Create the frame.
	 */
	public DrawTestFrame() {
		chat.setText("Retrieving chat....");
		//chat.setBounds((int) (getSize().width*0.75), 0, (int) (getSize().width*0.25), screenSize.height);
		
		StyleConstants.setForeground(chatAuthor, colorBlue);
		StyleConstants.setForeground(chatGame, Color.BLACK);
		StyleConstants.setForeground(chatMsg, Color.DARK_GRAY);
		//StyleConstants.setBold(chatAuthor, true);
		//TextArea chat = new TextArea();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, screenSize.width, screenSize.height);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(null);
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		try {
			loadFont();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager(); //Listen to keyboard
	    manager.addKeyEventDispatcher(this);
		setFullscreen(true);
		chatSettings();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		setupFrame();
		//Top
		frameTop.setVisible(true);
		timerFrame.setSize(400,20);
		frameTop.setBounds(70, 0, (int) (getSize().width*0.75-94), 72);
		frameTop.setOpaque(true);
		frameTop.setBackground(colorBlue);
		// Winner / Word listeners
		
		
		Firebase selectedword = new Firebase(firebaseURL).child("selectedword");
		selectedword.addValueEventListener(new ValueEventListener(){

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				selectedWord = dataSnapshot.getValue().toString();
				
			}
			
		});

	    //coordinates = new ArrayList<Drawing>();
		//Firebase chicken identifier
		Firebase isChicken = new Firebase(firebaseURL).child("quit");
		isChicken.addValueEventListener(new ValueEventListener(){

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.getValue().toString().equals("true")){
					chickenChecker = true;
				}
				
				if(dataSnapshot.getValue().toString().equals("false")){
					chickenChecker = false;
				}
				
				
			}
		
		});
		
		// Firebase win listener
		Firebase gameIsWon = new Firebase(firebaseURL).child("gameInProgress");
		gameIsWon.addValueEventListener(new ValueEventListener(){
			
			JLabel winnerScreen = new JLabel();
			JLabel gameOverScreen = new JLabel();
			JLabel waitingScreen = new JLabel();
			
			
			
			
			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("false")){
                	
                	//labels used to print out who won and the word they guessed
                	label.setBounds(0, (int) (getSize().height/2.8), (int) (getSize().width*0.75), 100);
                	label2.setBounds(0, (int) (getSize().height/1.45), (int) (getSize().width*0.75), 100);
                	
                	//logic for the startup waiting screen
                	if(startUp){
                	waitingScreen.setIcon(new ImageIcon("libs/WaitinScreen600.png"));
                	waitingScreen.setBounds((int) ((getSize().width*0.75)/2-300), getSize().height/2-300, 600, 600);
                	contentPane.add(waitingScreen);
                	contentPane.remove(winnerScreen);
                	contentPane.remove(gameOverScreen);
                	contentPane.repaint();
                	}
                	
                	//logic for when someone backs out of drawing
                	if(chickenChecker && !startUp){
                    	waitingScreen.setIcon(new ImageIcon("libs/WaitinScreen600.png"));
                    	waitingScreen.setBounds((int) ((getSize().width*0.75)/2-300), getSize().height/2-300 , 600, 600);
                    	contentPane.add(waitingScreen);
                    	contentPane.remove(winnerScreen);
                    	contentPane.remove(gameOverScreen);
                    	contentPane.remove(label);
                    	contentPane.remove(label2);
                    	contentPane.repaint();
                	}
                	
                	
                	//Winner screen
                	

                	
                		//logic for the winstate
                		if(chickenChecker == false && drawTimedOut == false && startUp == false){
                			winnerScreen.setIcon(new ImageIcon("libs/WinnerScreen.png"));
                        	winnerScreen.setBounds((int) ((getSize().width*0.75)/2-300), getSize().height/2-300, 600, 600);
                        	
                            selectedWord = selectedWord.substring(0, 1).toUpperCase() + selectedWord.substring(1);
                            label2.setText(selectedWord);
                            label.setForeground(colorBlue);
                            label2.setForeground(colorBlue);
                            contentPane.add(label);
                        	contentPane.add(label2);
                        	contentPane.add(winnerScreen);
                        	contentPane.remove(waitingScreen);
                        	contentPane.remove(gameOverScreen);
                        	
                        	
                        	
                        	Firebase winner = new Firebase(firebaseURL).child("roundWinner");
                    		winner.addValueEventListener(new ValueEventListener(){

                    			@Override
                    			public void onCancelled(FirebaseError arg0) {
                    				// TODO Auto-generated method stub
                    				
                    			}

                    			@Override
                    			public void onDataChange(DataSnapshot dataSnapshot) {
                    				label.setText(dataSnapshot.getValue().toString());
                    				
                    				
                    			}
                    			
                    		});
                        	
                        	
                        	
                    		
                        	contentPane.repaint();
                        	
                		}
                	
                		//logic for drawing the game over screen
                		if(drawTimedOut && !startUp){
                			gameOverScreen.setIcon(new ImageIcon("libs/gameOverScreen600.png"));
                        	gameOverScreen.setBounds((int) ((getSize().width*0.75)/2-300), getSize().height/2-300, 600, 600);
                        	contentPane.add(gameOverScreen);
                        	contentPane.remove(waitingScreen);
                        	contentPane.remove(winnerScreen);
                        	contentPane.remove(label);
                        	contentPane.remove(label2);
                        	contentPane.repaint();
                        	startUp = false;
                			
                		}
                	
                        
                       
                }
                //logic for when the game should be ongoing
                if(dataSnapshot.getValue().toString().equals("true")){
                	contentPane.remove(waitingScreen);
                	contentPane.remove(winnerScreen);
                	contentPane.remove(gameOverScreen);
                	contentPane.remove(label);
                	contentPane.remove(label2);
                	contentPane.repaint();
                	startUp = false;
                	timer();
                	
      
                }
                repaint();

               
        }
       
});

		
	    firebase.addChildEventListener(new ChildEventListener() {
	        

	        @Override
	        public void onChildAdded(DataSnapshot snapshot, String arg1) {
	        	
	        	Iterable<DataSnapshot> dsList= snapshot.getChildren();
				tempurl = snapshot.getKey();
				
				
	        	for (DataSnapshot dataSnapshot : dsList) {
	        		
	        	}
	        	 
	        	//URL used to get the coordinates from the database
	        	Firebase firebasetemp = new Firebase(firebaseURL + "draw/" + tempurl + "/points/");
	        	
	        	firebasetemp.addChildEventListener(new ChildEventListener() {

					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onChildAdded(DataSnapshot snapshot, String arg1) {
						
						//Hämtar innehållet i databasen
						Iterable<DataSnapshot> dsList= snapshot.getChildren();
						
						prevtempurl = snapshot.getKey();
						if (prevtempurl == "0"){
							System.out.println("\n\n-------- New line -------- \n");
							
						
						}
						System.out.println("\nDir: " +  snapshot.getKey());
						Random r = new Random();
						int x = r.nextInt(getSize().width);
						int y = r.nextInt(getSize().height);
						Color currentColor = null;
						
					
						
						final Drawing user = new Drawing(currentColor,x,y);
						
						
						Firebase firebasetempColor = new Firebase(firebaseURL).child("currentColor");
						firebasetempColor.addValueEventListener(new ValueEventListener(){

							@Override
							public void onCancelled(FirebaseError arg0) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								
								if(dataSnapshot.getValue().equals("red")){
									
									user.setColor(drawColorRed);
								}
								if(dataSnapshot.getValue().equals("black")){
									user.setColor(Color.BLACK);
								}
								
								if(dataSnapshot.getValue().equals("yellow")){
									user.setColor(drawColorYellow);
								}
								
								if(dataSnapshot.getValue().equals("green")){
									user.setColor(drawColorGreen);
								}
								
								if(dataSnapshot.getValue().equals("blue")){
									user.setColor(drawColorBlue);
								}
								
								if(dataSnapshot.getValue().equals("purple")){
									user.setColor(drawColorPurple);
								}
								
								if(dataSnapshot.getValue().equals("pink")){
									user.setColor(drawColorPink);
								}
								
								if(dataSnapshot.getValue().equals("white")){
									user.setColor(Color.WHITE);
								}
								
								
							}
							
						});
						
						for (DataSnapshot dataSnapshot : dsList) {
							
							
							System.out.print("   Key: "+dataSnapshot.getKey() + " = " + dataSnapshot.getValue());

							
							//letar upp och lägger till x kordinaterna i objektet
							if(dataSnapshot.getKey().equals("x")){
								
								String tempX = dataSnapshot.getValue().toString();
								int intX = Integer.parseInt(tempX);
								user.setX(intX+10);
								
								
							}
							
							//letar upp och lägger till y kordinaterna i objektet
							if(dataSnapshot.getKey().equals("y")){
								String tempY = dataSnapshot.getValue().toString();
								int intY = Integer.parseInt(tempY);
								user.setY(intY+5);
								
							}
							//lägger till objekten i vectorn
							users.add(user);
							
 
						}
						//målar om skärmen
						
						repaint();
						
						
					}

					@Override
					public void onChildChanged(DataSnapshot arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onChildMoved(DataSnapshot arg0, String arg1) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onChildRemoved(DataSnapshot arg0) {
						// TODO Auto-generated method stub
						
					}
					
	        	});
	        	
	        		
				 
			}
	        //... ChildEventListener also defines onChildChanged, onChildRemoved,
	        //    onChildMoved and onCanceled, covered in later sections.
	       
	        public void onCancelled(FirebaseError firebaseError) {
	            System.out.println("The read failed: " + firebaseError.getMessage());
	        }
			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onChildRemoved(DataSnapshot arg0) {
				// TODO Auto-generated method stub
				
			}
	    
	    });
	    
	    //Reads the chat from firebase and adds it to the chat vector
        firebasechat.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onCancelled(FirebaseError arg0) {
                            // TODO Auto-generated method stub
                           
                    }

                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String arg1) {
                           
                            Iterable<DataSnapshot> dsList= snapshot.getChildren();
                           
                                    for (DataSnapshot dataSnapshot : dsList) {
                                            // Hï¿½mtar och sorterar data frï¿½n firebase
                                            if(dataSnapshot.getKey().equals("author")){
                                                    author.add(dataSnapshot.getValue().toString());
                                                   
                                         
                                            } else {
                                                    msg.add(dataSnapshot.getValue().toString());
                                            }
                           
                                    }
                                    // Lï¿½gger till data frï¿½n arraylist "author" och "msg" till TextArea chat
                                    chat.setText(null);
                                    
                                    for (int i = 0; i < author.size(); i++){
                                    	//gör så att den sista raden inte 
                                    		if(i==(author.size()-1) && i != 0){
                                    			splitter="";
                                    			line="";
                                    		}
                                    		
                                    		else{
                                    			
                                    			splitter="\n";
                                    			line="---------------------------------------------------";
                                    		}
                                            //chat.append(author.get(i) + ":  " + msg.get(i) + splitter + line + splitter);
                                           //String str =(author.get(i) + ":  " + msg.get(i));
                                            //String str2 = wrapString(str, 26);
                                            //System.out.println(str2);
                                    		String str = author.get(i);
                                    		String str2 = wrapString(str, 26);
                                    		String strC = msg.get(i);
                                    		String strC2 = wrapString(strC, 19);
                                            
                                            //chat.append(str2 + splitter + line + splitter);
                                            try {
                                            	if(author.get(i).equals("@DrawStuff")){
                                            		
                                            		
                                            		doc.insertString(doc.getLength(), str2+": \n", chatGame);
                                            	} else {
                                            		
												doc.insertString(doc.getLength(), str2+": \n", chatAuthor);
                                            	}
                                            	
                                        		
												doc.insertString(doc.getLength(), strC2 + splitter + line +splitter, chatMsg);
											} catch (BadLocationException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}

                                            
                                           
                                    }
                                    //gör att chatten inte går att ta bort med backspace
                           chat.setEditable(false);
                           //repaint();
                           
                    }

                    @Override
                    public void onChildChanged(DataSnapshot arg0, String arg1) {
                            // TODO Auto-generated method stub
                           
                    }

                    @Override
                    public void onChildMoved(DataSnapshot arg0, String arg1) {
                            // TODO Auto-generated method stub
                           
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot arg0) {
                            // TODO Auto-generated method stub
                           
                    }
                    
           
           
        });
        
        //Empties the drawing vector
        firebasedraw.addChildEventListener(new ChildEventListener() {

            @Override
            public void onCancelled(FirebaseError arg0) {
                    // TODO Auto-generated method stub
                   
            }

            @Override
            public void onChildAdded(DataSnapshot snapshot, String arg1) {
                           
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String arg1) {
                 
            }

            @Override
            public void onChildMoved(DataSnapshot arg0, String arg1) {
                    // TODO Auto-generated method stub
                   
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                    // TODO Auto-generated method stub
            		users.clear();
                   repaint();
                   try {
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                   
            }
   
   
});


	}
	
	
	//function that specifies 
	@Override
	public void paint(Graphics g) {
		super.paint(g); 
		Graphics2D g2= (Graphics2D) g;
		
		//g2.setColor(Color.WHITE);
		//g2.fillRect(0, 0, getSize().width-(scrolll.getWidth()), getSize().height);
		//g2.setColor(Color.BLACK);
		g2.scale(5, 3.5);
		
		//g.drawString("ScreenNbr: "+Constants.screenNbr, 10,  20);
		//Test
		
		prevX = 0;
		prevY = 0;
		
		for (Drawing user : users) {
			
			
			int x = (user.getX());
			int y = (user.getY());
			
			if(prevX == 0){
				
				prevX = x;
			}
			
			if(prevY == 0){
				
				prevY = y;
			}
			
			g2.setStroke(new BasicStroke(2));
			g2.setColor(user.getColor());
			
			if(prevX - x > 6 || prevX - x < - 6 || prevY - y > 6 || prevY - y < -6){
                g2.setColor(drawColorTransparent);
        }
       
        g2.drawLine(prevX, prevY, x, y);
        //g2.setColor(Color.BLACK);

       
       
        prevX = user.getX();
        prevY = user.getY();
			//g2.fillOval(x,y, 4, 4);
			//g2.setColor(Color.BLACK);
			
			
			
			//g.drawString(drawing.getId(),x+15,y+15);
		}

			chatSettings();
	}
	
//loads the different versions of the roboto font
public void loadFont() throws Exception{
		
	File f = new File("libs/Roboto-Regular.ttf");
	FileInputStream in = new FileInputStream(f);
	Font roboto = Font.createFont(Font.TRUETYPE_FONT, in);
	Font roboto20Pt = roboto.deriveFont(20f);
	chat.setFont(roboto20Pt);
	
	File f2 = new File("libs/Roboto-Regular.ttf");
	FileInputStream in2 = new FileInputStream(f2);
	Font roboto2 = Font.createFont(Font.TRUETYPE_FONT, in2);
	Font roboto80Pt = roboto2.deriveFont(50f);
	label.setFont(roboto80Pt);
	label2.setFont(roboto80Pt);
	labelGameOver.setFont(roboto80Pt);
	
	File f3 = new File("libs/Roboto-Regular.ttf");
	FileInputStream in3 = new FileInputStream(f3);
	Font roboto3 = Font.createFont(Font.TRUETYPE_FONT, in3);
	Font robotoTimer = roboto2.deriveFont(55f);
	timerFrame.setFont(robotoTimer);
	
	}

//sets the layout for the chat-window
public void chatSettings(){
	
	
	panel.setBounds((int) (getSize().width*0.75), 0, (int) (getSize().width*0.25), screenSize.height);
	panel.setBorder(null);
	contentPane.add(panel);
	panel.setLayout(new BorderLayout(0, 0));
	scrolll.setBorder(null);
	scrolll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
	scrolll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
	panel.add(scrolll);
	//scrolll.setBounds((int) (getSize().width*0.75), 0, (int) (getSize().width*0.25), screenSize.height);
	//chat.setLineWrap(true);
	chat.setBackground(colorLightOrange);  //Lägger till bakgrundsfärg
	//chat.setFont(roboto20Pt); // Ändrar Font och storlek
	chat.setForeground(chatTextColor); //Ändrar färg på texten
	caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
	panel.repaint();
}

//controls the switch between windowed and fullscreen
public void setFullscreen(boolean fullscreen) {
	 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	     GraphicsDevice[] gd = ge.getScreenDevices();    
	     if(fullscreen){
				PrevX = getX();
				PrevY = getY();
				PrevWidth = getWidth();
				PrevHeight = getHeight();
				dispose(); 
				//Always on last screen!
				setUndecorated(true);
				gd[gd.length-1].setFullScreenWindow(this);
				setVisible(true);
				this.inFullScreenMode = true;
			}
			else{
				setVisible(true);
				setBounds(PrevX, PrevY, PrevWidth, PrevHeight);
				dispose();
				setUndecorated(false);
				setVisible(true);
				this.inFullScreenMode = false;
			}
}

//sets the keybind for switching between fullscreen and windowed
//change f in (e.getKeyChar()=='f') for a different keybind
@Override
public boolean dispatchKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_TYPED) {
    	 if(e.getKeyChar()=='f'){     		 
          	setFullscreen(!inFullScreenMode);	
  		}
     }
     return false;
	}

//Puts correct linebreaks in the chat
public static String wrapString(String string, int charWrap) {
    int lastBreak = 0;
    int nextBreak = charWrap;
    if (string.length() > charWrap) {
        String setString = "";
        do {
            while (string.charAt(nextBreak) != ' ' && nextBreak > lastBreak) {
                nextBreak--;
            }
            if (nextBreak == lastBreak) {
                nextBreak = lastBreak + charWrap;
            }
            setString += string.substring(lastBreak, nextBreak).trim() + "\n";
            lastBreak = nextBreak;
            nextBreak += charWrap;

        } while (nextBreak < string.length());
        setString += string.substring(lastBreak).trim();
        return setString;
    } else {
        return string;
    }
}

//Logic for the timer, tells firebase when the timer runs out
public void timer(){
	
contentPane.remove(frameTop);
contentPane.add(timerFrame);
timerFrame.setVisible(true);
//timerFrame.setSize(400,20);
timerFrame.setBounds(70, 0, (int) (getSize().width*0.75-94), 72);
timerFrame.setOpaque(true);
timerFrame.setBackground(colorBlue);
timerFrame.setForeground(Color.WHITE);


legitWin = false;
drawTimedOut = false;

Firebase currentDrawer = new Firebase(firebaseURL).child("currentDrawer");
currentDrawer.addValueEventListener(new ValueEventListener(){

	@Override
	public void onCancelled(FirebaseError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		currentDraw = dataSnapshot.getValue().toString();
		
	}
	
});

final Firebase gameIsWon = new Firebase(firebaseURL).child("gameInProgress");
final Firebase gameIsOver = new Firebase(firebaseURL).child("chicken");
final Firebase timeOut = new Firebase(firebaseURL).child("timedOut");
final Firebase drawing = new Firebase(firebaseURL).child("draw");
new Timer().schedule(new TimerTask(){

	int second = 70;
    
    

    @Override
    public void run() {
    	
        timerFrame.setText(currentDraw +"    :   "+ second-- + " s.");
        
        
        gameIsWon.addValueEventListener(new ValueEventListener(){

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				
				if(dataSnapshot.getValue().toString().equals("false")){
					legitWin = true;
					second = 0;
		        	timerFrame.setText("");
		        	
				}
				if(dataSnapshot.getValue().toString().equals("true")){
					if(second<0){
						gameIsWon.setValue(false);
						
					}
					
				}
				
			}
        	
        });
        if(second<0){
        	this.cancel();
        	timerFrame.setText("");
        	//contentPane.remove(timerFrame);
        	drawing.removeValue();
        	if(!legitWin){
        	timeOut.setValue("true");
        	drawTimedOut = true;
        	}
        	legitWin = false;

        }
      
    }   
},0, 1000);

}

//sets up the borders of the drawing window and the padding of the chat
public void setupFrame(){
	JLabel frameRight = new JLabel();
	JLabel frameBottom = new JLabel();
	JLabel frameLeft = new JLabel();
	JLabel framePadding = new JLabel();
	JLabel frameLogo = new JLabel();
	
	contentPane.add(frameLogo);
	contentPane.add(framePadding);
	contentPane.add(frameRight);
	contentPane.add(frameBottom);
	contentPane.add(frameLeft);
	contentPane.add(frameTop);
	//Padding
	framePadding.setVisible(true);
	
	framePadding.setBounds((int) (getSize().width*0.75-10), 0, 10, (int) (getSize().height));
	framePadding.setOpaque(true);
	framePadding.setBackground(colorLightOrange);
	
	//Right
	frameRight.setVisible(true);
	
	frameRight.setBounds((int) (getSize().width*0.75-30), 0, 20, (int) (getSize().height));
	frameRight.setOpaque(true);
	frameRight.setBackground(colorBlue);
	
	
	// Bot
	frameBottom.setVisible(true);
	
	frameBottom.setBounds(0, (int) (getSize().height-20), (int) (getSize().width*0.75-10), 20);
	frameBottom.setOpaque(true);
	frameBottom.setBackground(colorBlue);
	
	//Left
	frameLeft.setVisible(true);
	
	frameLeft.setBounds(0, 0, 20, (int) (getSize().height));
	frameLeft.setOpaque(true);
	frameLeft.setBackground(colorBlue);
	
	//Top left corner icon
	frameLogo.setVisible(true);
	frameLogo.setIcon(new ImageIcon("libs/ds_icon_72.png"));
	frameLogo.setBounds(0, 0, 72, 72);
	
	
	
}
}


