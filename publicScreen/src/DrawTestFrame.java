import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TextArea;
import java.awt.TextField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Path;


//Vi vill hï¿½mta kordinaterna frï¿½n databasen och rita ut dem pï¿½ skï¿½rmen.
public class DrawTestFrame extends JFrame {
	private String tempurl = "";
	private int dir;

	String splitter;
	String prevtempurl ="";
	private JPanel contentPane;
	Firebase firebase = new Firebase("https://brilliant-fire-8250.firebaseio.com/draw/");
	Firebase firebasechat = new Firebase("https://brilliant-fire-8250.firebaseio.com/chat/");
	
	TextArea chat = new TextArea();
	TextField field = new TextField();
	
	
	
	
	//change to vector
   Vector <String> author = new Vector <String> ();
   Vector<String> msg = new Vector<String>();
	
	private Graphics g;
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
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(507, 0, 267, 529);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(chat);
		chat.setBackground(new Color(147,192,191));  //Lägger till bakgrundsfärg
		chat.setFont(new Font("Arial", Font.PLAIN, 16)); // Ändrar Font och storlek
		chat.setForeground(Color.white); //Ändrar färg på texten
	
		setContentPane(contentPane);
		
								

	    //coordinates = new ArrayList<Drawing>();
		
		//ï¿½r det rï¿½tt att anvï¿½nda addchildeventlistener nï¿½r vi ska konstant avlyssna kordinater
	    firebase.addChildEventListener(new ChildEventListener() {
	        

	        @Override
	        public void onChildAdded(DataSnapshot snapshot, String arg1) {
	        	
	        	Iterable<DataSnapshot> dsList= snapshot.getChildren();
				tempurl = snapshot.getKey();
				
				
				
				
				
				
				//kan vi anvï¿½nda place pï¿½ samma sï¿½tt som du gï¿½r nï¿½r vi ska hï¿½mta mï¿½nga kordinater?
				
	        	for (DataSnapshot dataSnapshot : dsList) {
	        	
	   
	        		
	        	}
	        	 
	        	
	        	Firebase firebasetemp = new Firebase("https://brilliant-fire-8250.firebaseio.com/draw/" + tempurl + "/points/");
	        	
	        	firebasetemp.addChildEventListener(new ChildEventListener() {

					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onChildAdded(DataSnapshot snapshot, String arg1) {
						
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

							
							
							if(dataSnapshot.getKey().equals("x")){
								
								String tempX = dataSnapshot.getValue().toString();
								int intX = Integer.parseInt(tempX);
								user.setX(intX+180);
								
								
							}
							
							if(dataSnapshot.getKey().equals("y")){
								String tempY = dataSnapshot.getValue().toString();
								int intY = Integer.parseInt(tempY);
								user.setY(intY+220);
								
							}
							users.add(user);
							 
						}
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
	    
	    // Lï¿½ser av chatt frï¿½n Firebase
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
                                    		if(i==(author.size()-1)){
                                    			splitter="";
                                    		}
                                    		
                                    		else{
                                    			
                                    			splitter="\n";
                                    		}
                                            chat.append(author.get(i) + ":  " + msg.get(i) + splitter);
                                           
                                    }
                           
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
	
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2= (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setColor(Color.BLACK);
		
		//g.drawString("ScreenNbr: "+Constants.screenNbr, 10,  20);
		//Test
		for (Drawing user : users) {
			int x = (user.getX());
			int y = (user.getY());
			//g2.setColor(user.getColor());
			g2.fillOval(x,y, 5, 5);
			g2.setColor(Color.BLACK);
			
			
			
			//g.drawString(drawing.getId(),x+15,y+15);
		}
		
	}
}