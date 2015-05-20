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
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Path;


//Vi vill h�mta kordinaterna fr�n databasen och rita ut dem p� sk�rmen.
public class DrawTestFrame extends JFrame implements KeyEventDispatcher {
	private String tempurl = "";
	private int dir;

	//anv�nds f�r att g�ra en by rad i chatten
	String splitter= "\n";
	//h�ller den genererade URLen f�r att g� ner i databasen
	String prevtempurl ="";
	
	private JPanel contentPane;
	
	Firebase firebase = new Firebase("https://brilliant-fire-8250.firebaseio.com/draw/");
	Firebase firebasechat = new Firebase("https://brilliant-fire-8250.firebaseio.com/chat/");
	Firebase firebasedraw = new Firebase("https://brilliant-fire-8250.firebaseio.com/");
	
	JTextArea chat = new JTextArea();
	DefaultCaret caret = (DefaultCaret)chat.getCaret();
	TextField field = new TextField();
	final JScrollPane scrolll = new JScrollPane(chat);
	String roundWinner;
	String selectedWord;
	JLabel label = new JLabel();
	JLabel label2 = new JLabel();
	JPanel panel = new JPanel();

	private int PrevX = 100 ,PrevY = 100 ,PrevWidth = 480,PrevHeight = 640;
	
	private boolean inFullScreenMode = false;
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	//change to vector
   Vector <String> author = new Vector <String> ();
   Vector<String> msg = new Vector<String>();
	
	private Graphics g;
	//vector som h�ller det som ska ritas ut
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
		setFullscreen(true);
		/*
		JPanel panel = new JPanel();

		panel.setBounds(testtest, 0, 267, 529);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(chat);
		chat.setBackground(new Color(147,192,191));  //L�gger till bakgrundsf�rg
		chat.setFont(new Font("Arial", Font.PLAIN, 16)); // �ndrar Font och storlek
		chat.setForeground(Color.white); //�ndrar f�rg p� texten
		
	*/
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
		
		// Firebase win listener
		Firebase gameIsWon = new Firebase("https://brilliant-fire-8250.firebaseio.com/").child("gameInProgress");
		gameIsWon.addValueEventListener(new ValueEventListener(){

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.getValue().toString().equals("false")){
					contentPane.add(label);
					contentPane.add(label2);
					
					label.setText("Winner: " + roundWinner.toUpperCase());
					label2.setText("Word: " + selectedWord.toUpperCase());
					label.setBounds(100, 100, 1000, 200 );
					label2.setBounds(100, 300, 1000, 200 );
					label.setFont(new Font("Sefir", Font.BOLD, 40));
					label2.setFont(new Font("Sefir", Font.BOLD, 40));
					label2.setForeground(Color.BLACK);
					label.setForeground(Color.BLACK);			
					setContentPane(contentPane);
				}
				if(dataSnapshot.getValue().toString().equals("true")){
					if(label.getText() != null){
					contentPane.remove(label);
					contentPane.remove(label2);
					}
					contentPane.setBackground(Color.WHITE);
					panel.add(scrolll);
					setContentPane(contentPane);
					//SwingUtilities.updateComponentTreeUI(contentPane);
					contentPane.invalidate();
					contentPane.revalidate();	
					contentPane.repaint();
				}

				
			}
			
		});

		
		//�r det r�tt att anv�nda addchildeventlistener n�r vi ska konstant avlyssna kordinater
	    firebase.addChildEventListener(new ChildEventListener() {
	        

	        @Override
	        public void onChildAdded(DataSnapshot snapshot, String arg1) {
	        	
	        	Iterable<DataSnapshot> dsList= snapshot.getChildren();
				tempurl = snapshot.getKey();
				
				
				
				
				
				
				//kan vi anv�nda place p� samma s�tt som du g�r n�r vi ska h�mta m�nga kordinater?
				
	        	for (DataSnapshot dataSnapshot : dsList) {
	        	
	   
	        		
	        	}
	        	 
	        	//URL f�r att h�mta punkterna som ska ritas ut fr�n databasen
	        	Firebase firebasetemp = new Firebase("https://brilliant-fire-8250.firebaseio.com/draw/" + tempurl + "/points/");
	        	
	        	firebasetemp.addChildEventListener(new ChildEventListener() {

					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onChildAdded(DataSnapshot snapshot, String arg1) {
						
						//H�mtar inneh�llet i databasen
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

							
							//letar upp och l�gger till x kordinaterna i objektet
							if(dataSnapshot.getKey().equals("x")){
								
								String tempX = dataSnapshot.getValue().toString();
								int intX = Integer.parseInt(tempX);
								user.setX(intX+5);
								
								
							}
							
							//letar upp och l�gger till y kordinaterna i objektet
							if(dataSnapshot.getKey().equals("y")){
								String tempY = dataSnapshot.getValue().toString();
								int intY = Integer.parseInt(tempY);
								user.setY(intY+5);
								
							}
							//l�gger till objekten i vectorn
							users.add(user);
							 
						}
						//m�lar om sk�rmen
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
	    
	    // L�ser av chatt fr�n Firebase
        firebasechat.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onCancelled(FirebaseError arg0) {
                            // TODO Auto-generated method stub
                           
                    }

                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String arg1) {
                           
                            Iterable<DataSnapshot> dsList= snapshot.getChildren();
                           
                                    for (DataSnapshot dataSnapshot : dsList) {
                                            // H�mtar och sorterar data fr�n firebase
                                            if(dataSnapshot.getKey().equals("author")){
                                                    author.add(dataSnapshot.getValue().toString());
                                                   
                                         
                                            } else {
                                                    msg.add(dataSnapshot.getValue().toString());
                                            }
                           
                                    }
                                    // L�gger till data fr�n arraylist "author" och "msg" till TextArea chat
                                    chat.setText(null);
                                    
                                    for (int i = 0; i < author.size(); i++){
                                    	//g�r s� att den sista raden inte 
                                    		if(i==(author.size()-1) && i != 0){
                                    			splitter="";
                                    		}
                                    		
                                    		else{
                                    			
                                    			splitter="\n";
                                    		}
                                            chat.append(author.get(i) + ":  " + msg.get(i) + splitter);
                                           
                                    }
                                    //g�r att chatten inte g�r att ta bort med backspace
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
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getSize().width-(scrolll.getWidth()), getSize().height);
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
		try {
			chatSettings();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//setFullscreen(true);
	}


public void chatSettings() throws Exception{



	panel.setBounds((int) (getSize().width*0.75-17), 0, (int) (getSize().width*0.25), screenSize.height-45);
	contentPane.add(panel);
	panel.setLayout(new BorderLayout(0, 0));
	panel.add(scrolll);
	chat.setLineWrap(true);
	chat.setBackground(new Color(208,128,20));  //L�gger till bakgrundsf�rg
	//chat.setFont(roboto20Pt); // �ndrar Font och storlek
	loadFont();
	chat.setForeground(Color.white); //�ndrar f�rg p� texten
	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	
	
}

public void setFullscreen(boolean fullscreen) {
	 GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	     GraphicsDevice[] gd = ge.getScreenDevices();    
		 if(fullscreen){
			 	PrevX = 0;
				PrevY = 0;
				PrevWidth = getWidth();
				PrevHeight = getHeight();
				dispose();
			//Always on last screen!
			//setUndecorated(true);
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
   	 if(e.getKeyChar()=='F'){     		 
         	setFullscreen(!inFullScreenMode);	
 		}
    }
    return false;
	}

public void loadFont() throws Exception{
	
	File f = new File("Roboto-Regular.ttf");
	FileInputStream in = new FileInputStream(f);
	Font roboto = Font.createFont(Font.TRUETYPE_FONT, in);
	Font roboto20Pt = roboto.deriveFont(20f);
	chat.setFont(roboto20Pt);
	
	
}
}


