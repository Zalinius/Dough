package game;

import java.util.Collection;
import java.util.Iterator;

import com.zalinius.architecture.Collidable;
import com.zalinius.architecture.GameObject;
import com.zalinius.geometry.Circle;
import com.zalinius.geometry.Shape;
import com.zalinius.physics.Point2D;
import com.zalinius.physics.Vector2D;
import com.zalinius.utilities.ZMath;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;

public class Dough implements Collidable, Holdable, GameObject{
	
	public static final double DENSITY = 1.0 / 640;  //density is in mass/px^2

	private double mass;
	private Point2D center;
	private Vector2D velocity;
	
	private Paint color;

	public Dough(Point2D center) {
		this(center, 1.0);
	}

	public Dough(Point2D center, double mass) {
		this.mass = mass;
		color = Color.BISQUE;
		
		this.center = center;
		velocity = new Vector2D();
	}
	
	public Dough(Dough d1, Dough d2) {
		mass = d1.mass + d2.mass;
		color = Color.BISQUE;
		
		center = new Point2D( (d1.center.x + d2.center.x)/2.0 , (d1.center.y + d2.center.y)/2.0 );
		velocity = new Vector2D();
	}
	
	//merging a whole bunch of dough . . .
	public Dough(Collection<Dough> doughs) {
		center = new Point2D();
		mass = 0;
		
		boolean onFloor = false;
		for (Iterator<Dough> doughIt = doughs.iterator(); doughIt.hasNext();) {
			Dough dough = doughIt.next();
			if(dough.center.y == 0.0) {
				onFloor = true;
			}
			center = new Point2D( (center.x * mass + dough.center.x  * dough.mass)/(mass + dough.mass) , (center.y * mass + dough.center.y * dough.mass)/(mass + dough.mass) );
			mass += dough.mass;
		}
		if(onFloor) {
			center = new Point2D(center.x, 0.0);
		}
		color = Color.BISQUE;
		
		velocity = new Vector2D();
		
	}
	
	public boolean canSplit(double amount) {
		return amount < mass;
	}
	
	public Dough split(double amount) {
		mass -= amount;
		return new Dough(center, amount);
	}

	
	private double getDiameter() {
		return Math.sqrt(4 * mass / DENSITY / Math.PI);
	}
	private double getHalfCircleDiameter() {
		return Math.sqrt(8 * mass / DENSITY / Math.PI);
	}
	
	@Override
	public Vector2D velocity() {
		return velocity;
	}

	@Override
	public Point2D position() {
		return position();
	}
	
	public void accelerate(Vector2D newVelocity) {
		this.velocity = newVelocity;
	}
	
	public void move(double delta) {
		this.center = Point2D.add(center, velocity.scale(delta));
		this.center = ZMath.clamp(center, new Point2D(), new Point2D(800,800));
	}
	public void moveTo(Point2D newCenter) {
		this.center = newCenter;
	}

	@Override
	public Shape getCollisionBox() {
		if(onFloor()) {
			return new Circle(center, getDiameter() / Math.sqrt(2.0)); //Kind of a hack
		}
		else {
			return new Circle(center, getDiameter() / 2.0);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	public boolean onFloor() {
		return center.y == 0.0;
	}
	
	public void render(GraphicsContext gc) {
		if(onFloor()) {
			renderFloor(gc);
		}
		else {
			renderSphere(gc);
		}
	}	
	
	public void renderSphere(GraphicsContext gc) {
		gc.setFill(color);
		double diameter = getDiameter();
		gc.fillOval(center.x - diameter / 2, center.y , diameter, diameter);
	}

	
	public void renderFalling() {
		
	}
	
	public void renderTube() {
		
	}
	
	public void renderFloor(GraphicsContext gc) {
		double semiCircleDiameter = getHalfCircleDiameter();
		gc.setFill(color);
		gc.fillArc(center.x - semiCircleDiameter/2, center.y - semiCircleDiameter / 2, semiCircleDiameter, semiCircleDiameter, 180, 180, ArcType.OPEN);		
	}

	@Override
	public void update(double delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Holdable cook() {
		return new Bread(center, mass);
	}

	@Override
	public boolean selfInsert(InputSlot<Dough> doughSlot, InputSlot<Bread> breadSlot) {
		return doughSlot.insert(this);
	}
	@Override
	public boolean selfRemove(Collection<Dough> doughSlot, Collection<Bread> breadSlot) {
		return doughSlot.remove(this);
	}	

}
