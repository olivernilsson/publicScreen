import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Path;


//Vi vill hï¿½mta kordinaterna frï¿½n databasen och rita ut dem pï¿½ skï¿½rmen.
public class DrawTestFrame extends JFrame implements KeyEventDispatcher {
	private String tempurl = "";
	private int dir;

	//används för att göra en by rad i chatten
	String splitter= "\n";
	//håller den genererade URLen för att gå ner i databasen
	String prevtempurl ="";
	String line = "---------------------------------------------------" ;
	
	private JPanel contentPane;
	
	Firebase firebase = new Firebase("https://brilliant-fire-8250.firebaseio.com/draw/");
	Firebase firebasechat = new Firebase("https://brilliant-fire-8250.firebaseio.com/chat/");
	Firebase firebasedraw = new Firebase("https://brilliant-fire-8250.firebaseio.com/");
	
	JTextArea chat = new JTextArea();
	DefaultCaret caret = (DefaultCaret)chat.getCaret();
	TextField field = new TextField();
	final JScrollPane scrolll = new JScrollPane(chat);
	String roundWinner ="";
	String selectedWord ="";
	JLabel label = new JLabel("The Label", SwingConstants.CENTER);
	JLabel label2 = new JLabel("The Label", SwingConstants.CENTER);
	JLabel winnerLabel = new JLabel();
	JLabel wordLabel = new JLabel();
	JLabel star1 = new JLabel();
	JLabel star2 = new JLabel();
	JLabel star3 = new JLabel();
	JPanel panel = new JPanel();
	boolean chickenChecker;
	Color colorBlue = new Color(4, 154, 149);
	Color colorOrange = new Color(221, 141, 2);
	Color colorLightOrange = new Color(255, 195, 126);
	Color chatTextColor = new Color(100, 95, 88);


	private int PrevX = 100 ,PrevY = 100 ,PrevWidth = 480,PrevHeight = 640;
	
	private boolean inFullScreenMode = false;
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	//change to vector
   Vector <String> author = new Vector <String> ();
   Vector<String> msg = new Vector<String>();
	
	private Graphics g;
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

		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		
		// Winner / Word listeners
		Firebase winner = new Firebase("https://brilliant-fire-8250.firebaseio.com/").child("roundWinner");
		winner.addValueEventListener(new ValueEventListener(){

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				roundWinner = dataSnapshot.getValue().toString();
				
				
			}
			
		});
		
		Firebase selectedword = new Firebase("https://brilliant-fire-8250.firebaseio.com/").child("selectedword");
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
		// Firebase chicken identifier
		Firebase isChicken = new Firebase("https://brilliant-fire-8250.firebaseio.com/").child("chicken");
		isChicken.addValueEventListener(new ValueEventListener(){

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.getValue().toString().equals("beep")){
					chickenChecker = true;
				}
				
				if(dataSnapshot.getValue().toString().equals("null")){
					chickenChecker = false;
				}
				
				
			}
		
		});
		
		// Firebase win listener
		Firebase gameIsWon = new Firebase("https://brilliant-fire-8250.firebaseio.com/").child("gameInProgress");
		gameIsWon.addValueEventListener(new ValueEventListener(){

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals("false")){
                        contentPane.add(label);
                        contentPane.add(label2);
                        contentPane.add(winnerLabel);
                        contentPane.add(wordLabel);
                        contentPane.add(star1);
                        contentPane.add(star2);
                        contentPane.add(star3);
                        if(roundWinner == ""){
                            label.setBounds(115, 120, 1000, 200 );
                            label2.setBounds(115, 300, 1000, 200 );
                                label.setText("Waiting");
                                label2.setText("for drawer");
                                label.setForeground(colorBlue);
                                label2.setForeground(colorBlue);
                               
                        } else {
                        label.setText(roundWinner);
                        selectedWord = selectedWord.substring(0, 1).toUpperCase() + selectedWord.substring(1);
                        label2.setText(selectedWord);
                        //label2.setText(selectedWord);
                        winnerLabel.setIcon(new ImageIcon("winner.png"));
                        wordLabel.setIcon(new ImageIcon("word.png"));
                        star1.setIcon(new ImageIcon("litenstar.png"));
                        star2.setIcon(new ImageIcon("star.png"));
                        star3.setIcon(new ImageIcon("starr.png"));
                        label2.setForeground(colorOrange);
                        label.setForeground(colorOrange);
                        
                        }
                		winnerLabel.setBounds(150, 10, 500, 150);
                		wordLabel.setBounds(270, 350, 500, 150);
                		star1.setBounds(590, 201, 116, 71);
                		star2.setBounds(609, 305, 138, 76);
                		star3.setBounds(40, 196, 138, 76);
                        label.setBounds(0, 160, (int) (getSize().width*0.75-17), 200 );
                        label2.setBounds(0, 460, (int) (getSize().width*0.75-17), 200 );

                        //label2.setForeground(Color.ORANGE);
                        label2.setBackground(Color.BLUE);
                        //label.setForeground(Color.ORANGE);
                        label.setBackground(Color.BLUE);
                        setContentPane(contentPane);
                       
                }
                if(dataSnapshot.getValue().toString().equals("true")){
                        contentPane.remove(label);
                        contentPane.remove(label2);
                        contentPane.remove(winnerLabel);
                        contentPane.remove(wordLabel);
                        contentPane.remove(star1);
                        contentPane.remove(star2);
                        contentPane.remove(star3);
                        contentPane.setBackground(Color.WHITE);
                        panel.add(scrolll);
                        setContentPane(contentPane);
                        //SwingUtilities.updateComponentTreeUI(contentPane);
                        contentPane.invalidate();
                        contentPane.revalidate();      
                        contentPane.repaint();
                        timer();
                }
                

               
        }
       
});

		
		//ï¿½r det rï¿½tt att anvï¿½nda addchildeventlistener nï¿½r vi ska konstant avlyssna kordinater
	    firebase.addChildEventListener(new ChildEventListener() {
	        

	        @Override
	        public void onChildAdded(DataSnapshot snapshot, String arg1) {
	        	
	        	Iterable<DataSnapshot> dsList= snapshot.getChildren();
				tempurl = snapshot.getKey();
				
				
				
				
				
				
				//kan vi anvï¿½nda place pï¿½ samma sï¿½tt som du gï¿½r nï¿½r vi ska hï¿½mta mï¿½nga kordinater?
				
	        	for (DataSnapshot dataSnapshot : dsList) {
	        	
	   
	        		
	        	}
	        	 
	        	//URL för att hämta punkterna som ska ritas ut från databasen
	        	Firebase firebasetemp = new Firebase("https://brilliant-fire-8250.firebaseio.com/draw/" + tempurl + "/points/");
	        	
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
						Drawing user = new Drawing(snapshot.getKey(),x,y);
						for (DataSnapshot dataSnapshot : dsList) {
							
							
							System.out.print("   Key: "+dataSnapshot.getKey() + " = " + dataSnapshot.getValue());

							
							//letar upp och lägger till x kordinaterna i objektet
							if(dataSnapshot.getKey().equals("x")){
								
								String tempX = dataSnapshot.getValue().toString();
								int intX = Integer.parseInt(tempX);
								user.setX(intX+20);
								
								
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
                                            String str =(author.get(i) + ":  " + msg.get(i));
                                            String str2 = wrapString(str, 27);
                                            System.out.println(str2);
                                            chat.append(str2 + splitter + line + splitter);

                                            
                                           
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
                  // repaint();
                   try {
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                   
            }
   
   
});


	}
	
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g); 
		Graphics2D g2= (Graphics2D) g;
		//g2.setColor(Color.WHITE);
		//g2.fillRect(0, 0, getSize().width-(scrolll.getWidth()), getSize().height);
		g2.setColor(Color.BLACK);
		g2.scale(5, 3.5);
		
		//g.drawString("ScreenNbr: "+Constants.screenNbr, 10,  20);
		//Test
		for (Drawing user : users) {
			int x = (user.getX());
			int y = (user.getY());
			//g2.setColor(user.getColor());
			g2.fillOval(x,y, 4, 4);
			g2.setColor(Color.BLACK);
			
			
			
			//g.drawString(drawing.getId(),x+15,y+15);
		}
			chatSettings();
	}
	
public void loadFont() throws Exception{
		
	File f = new File("Roboto-Regular.ttf");
	FileInputStream in = new FileInputStream(f);
	Font roboto = Font.createFont(Font.TRUETYPE_FONT, in);
	Font roboto20Pt = roboto.deriveFont(20f);
	chat.setFont(roboto20Pt);
		
	File f2 = new File("Roboto-Regular.ttf");
	FileInputStream in2 = new FileInputStream(f2);
	Font roboto2 = Font.createFont(Font.TRUETYPE_FONT, in2);
	Font roboto80Pt = roboto2.deriveFont(80f);
	label.setFont(roboto80Pt);
	label2.setFont(roboto80Pt);
	}


public void chatSettings(){

	panel.setBounds((int) (getSize().width*0.75), 0, (int) (getSize().width*0.25), screenSize.height);
	panel.setBorder(null);
	contentPane.add(panel);
	panel.setLayout(new BorderLayout(0, 0));
	scrolll.setBorder(null);
	scrolll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
	panel.add(scrolll);
	chat.setLineWrap(true);
	chat.setBackground(Color.WHITE);  //Lägger till bakgrundsfärg
	//chat.setFont(roboto20Pt); // Ändrar Font och storlek
	chat.setForeground(chatTextColor); //Ändrar färg på texten
	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);	
}

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

@Override
public boolean dispatchKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_TYPED) {
    	 if(e.getKeyChar()=='f'){     		 
          	setFullscreen(!inFullScreenMode);	
  		}
     }
     return false;
	}

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

public void timer(){
final JLabel timerFrame = new JLabel();
contentPane.add(timerFrame);
timerFrame.setVisible(true);
timerFrame.setSize(400,20);
new Timer().schedule(new TimerTask(){

    int second = 5;
    @Override
    public void run() {
        timerFrame.setText("Application will close in " + second-- + " seconds.");
        if(second<0){
        	timerFrame.removeAll();
        	contentPane.remove(timerFrame);
        	repaint();
        }
    }   
},0, 1000);
}
}


