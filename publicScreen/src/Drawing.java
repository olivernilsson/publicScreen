import java.awt.Color;


public class Drawing implements Comparable<Drawing> {
	
	private int x, y;
	private int color;
	private String id;
	private Color c2 = new Color(color);

	public Drawing(String color, int x, int y){
		
		this.color=Integer.parseInt(color);
		this.x=x;
		this.y=y;

	}

	public Color getColor(){
		
		return c2;
	}
	
	public void setColor(Color c){
		
		this.c2 = c;
	}
	
	public int getX(){
		
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY(){
		
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int compareTo(Drawing drawing) {
		return id.compareTo(drawing.getId());
	}
	
	public String getId() {
		return id;
	}
}


