package game;

import java.util.Collection;

import com.zalinius.architecture.GameObject;
import com.zalinius.physics.Moveable;
import com.zalinius.physics.Point2D;

public interface Holdable extends GameObject, Moveable {

	public void moveTo(Point2D position);

	public Holdable cook();
	public boolean selfInsert(InputSlot<Dough> doughSlot, InputSlot<Bread> breadSlot);
	public boolean selfRemove(Collection<Dough> doughSlot, Collection<Bread> breadSlot);
}
