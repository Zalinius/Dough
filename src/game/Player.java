package game;

import java.util.ArrayList;
import java.util.Collection;

import com.zalinius.architecture.GameObject;
import com.zalinius.architecture.input.Inputtable;
import com.zalinius.physics.Point2D;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class Player implements GameObject{

	private double x;
	private double s;

	private Holdable heldDough;

	public Player() {
		x = 100;
		s = 100;

		heldDough = new Dough(new Point2D());
	}




	@Override
	public void render(GraphicsContext gc) {
		gc.setFill(Color.BLACK);
		gc.fillRect(x - 10, 0, 20, 40);

		if(heldDough != null) {
			heldDough.render(gc);
		}
	}


	@Override
	public void update(double delta) {
		if(heldDough != null) {
			heldDough.moveTo(new Point2D(x, 15));
		}
	}


	public Collection<Inputtable> controls(){
		Collection<Inputtable> controls = new ArrayList<>();

		controls.add(new Inputtable() {

			@Override
			public KeyCode keyCode() {
				return KeyCode.LEFT;
			}
			@Override
			public void held(double delta) {
				x -= s * delta;
			}
		});
		controls.add(new Inputtable() {

			@Override
			public KeyCode keyCode() {
				return KeyCode.RIGHT;
			}
			@Override
			public void held(double delta) {
				x += s * delta;
			}
		});


		return controls;
	}

	public boolean handsFull() {
		return heldDough != null;
	}

	public Holdable dropHeld() {
		if(handsFull()) {
			Holdable held = heldDough;
			heldDough = null;
			return held;
		}
		else {
			return null;
		}
	}
	
	public void take(Holdable dough) {
		heldDough = dough;
	}
	
	public Point2D center() {
		return new Point2D(x - 10, 5);
	}

}
