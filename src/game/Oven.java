package game;

import com.zalinius.architecture.Collidable;
import com.zalinius.architecture.GameObject;
import com.zalinius.geometry.Rectangle;
import com.zalinius.geometry.Shape;
import com.zalinius.physics.Point2D;
import com.zalinius.utilities.time.GameClock;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Bakes dough into Bread
 * @author Zach
 *
 */
public class Oven implements GameObject, Collidable{

	private Holdable contents;
	private Point2D center;
	private final double size;
	private static final double COOKING_TIME = 2;
	private InputSlot<Bread> breadSlot;
	
	public Oven(double xPosition, InputSlot<Bread> breadSlot) {
		size = 100;
		this.center = new Point2D(xPosition, size / 2);
		contents = null;
		this.breadSlot = breadSlot;
	}
	
	public boolean insert(Holdable item) {
		if(item == null || contents != null) {
			return false;
		}
		else {
			contents = item;
			GameClock.addTimer(this, COOKING_TIME);
			return true;
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.setFill(Color.DARKGRAY);
		gc.fillRect(center.x-size/2, center.y-size/2, size, size);
		
		if(contents == null) {
			gc.setFill(Color.BLACK);
		}
		else {
			gc.setFill(Color.ORANGE);
		}
		gc.fillRect(center.x - size/4, center.y - size/4, size/2, size/4);
	}

	@Override
	public void update(double delta) {
		if(contents != null) {
			if(GameClock.isTimerDone(this) && contents != null)
			{
				contents = contents.cook();
				if(contents != null) {
					contents.moveTo(center.add(0, -size/8));
					boolean outputted = contents.selfInsert(null, breadSlot);
					if(outputted) {
						contents = null;
					}
				}
			}
			else {
				//Bread is cooking
			}
		}
	}

	@Override
	public Shape getCollisionBox() {
		return new Rectangle(new Point2D(center.x-size/2, center.y-size/2), size, size);
	}
	
	

}
