package game;

import com.zalinius.architecture.GameObject;
import com.zalinius.physics.Moveable;
import com.zalinius.physics.Point2D;

public interface Holdable extends GameObject, Moveable {

	public void moveTo(Point2D position);
}
