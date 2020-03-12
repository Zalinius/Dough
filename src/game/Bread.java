package game;

import java.util.Collection;

import com.zalinius.architecture.Collidable;
import com.zalinius.architecture.GameObject;
import com.zalinius.geometry.Rectangle;
import com.zalinius.geometry.Shape;
import com.zalinius.physics.Moveable;
import com.zalinius.physics.Point2D;
import com.zalinius.physics.Vector2D;
import com.zalinius.utilities.ZMath;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Bread implements Moveable, Collidable, Holdable, GameObject{
	
	public static final double DENSITY = 1.0 / 640;  //density is in mass/px^2

	private double mass;
	private Point2D center;
	private Vector2D velocity;
	
	private Paint color;

	public Bread(Point2D center) {
		this(center, 1.0);
	}

	public Bread(Point2D center, double mass) {
		this.mass = mass;
		color = Color.BURLYWOOD;
		
		this.center = center;
		velocity = new Vector2D();
	}
	
	@Override
	public Vector2D velocity() {
		return velocity;
	}

	@Override
	public Point2D position() {
		return center;
	}
	
	public void accelerate(Vector2D newVelocity) {
		this.velocity = newVelocity;
	}
	
	public void move(double delta) {
		this.center = Point2D.add(center, velocity.scale(delta));
		this.center = ZMath.clamp(center, new Point2D(0, getHeight()/2), new Point2D(800,800));

	}
	public void moveTo(Point2D newCenter) {
		this.center = newCenter;
	}
	

	
	public double getHeight() {
		return Math.sqrt(mass /2 / DENSITY);
	}

	
	public double getLength() {
		return mass / DENSITY / getHeight();
	}

	@Override
	public Shape getCollisionBox() {
		return new Rectangle(new Point2D(center.x-getLength()/2, center.y-getHeight()/2), getLength(), getHeight());
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	public boolean onFloor() {
		return center.y == 0.0;
	}
	
	public void render(GraphicsContext gc) {
		gc.setFill(color);
		gc.fillRect(center.x - getLength()/2, center.y - getHeight()/2, getLength(), getHeight());
	}

	@Override
	public void update(double delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Holdable cook() {
		return null;
	}

	@Override
	public boolean selfInsert(InputSlot<Dough> doughSlot, InputSlot<Bread> breadSlot) {
		return breadSlot.insert(this);
	}

	@Override
	public boolean selfRemove(Collection<Dough> doughSlot, Collection<Bread> breadSlot) {
		return breadSlot.remove(this);
	}	
	

	

}
