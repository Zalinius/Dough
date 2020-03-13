package game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.zalinius.architecture.GameContainer;
import com.zalinius.architecture.Graphical;
import com.zalinius.architecture.Logical;
import com.zalinius.architecture.input.Inputtable;
import com.zalinius.physics.Gravity;
import com.zalinius.physics.Point2D;
import com.zalinius.physics.collisions.Collision;
import com.zalinius.plugins.FPSDisplay;
import com.zalinius.plugins.Plugin;
import com.zalinius.utilities.time.GameClock;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Game extends GameContainer implements Logical, Graphical {



	public static void main(String[] args) {
		System.out.println("Dough time!");
		launch(args);
	}

	private Collection<Dough> doughs;
	private Collection<Bread> breads;
	private Oven oven;
	private Player player;
	private int breadDelivered;

	
	@Override
	public List<Plugin> getPlugins() {
		System.out.println("RAWR");
		List<Plugin> plugins = new ArrayList<>();
		plugins.add(new FPSDisplay());
		
		return plugins;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		super.start(primaryStage);
		player = new Player();

		Collection<Inputtable> controls = player.controls();
		controls.add(exitAction());
		controls.addAll(interactions());
		//controls.addAll(player.keyboardControls());
		addControls(controls, new ArrayList<>());

		doughs = new ArrayList<>();
		breads = new ArrayList<>();
		breads.add(new Bread(new Point2D(40, 40), 1.0));

		InputSlot<Bread> breadSlot = new InputSlot<Bread>() {

			@Override
			public boolean insert(Bread item) {
				breads.add(item);
				return true;
			}
		};
		oven = new Oven(150, breadSlot);

	}



	private Inputtable exitAction() {
		return new Inputtable() {

			@Override
			public KeyCode keyCode() {
				return KeyCode.ESCAPE;
			}
			@Override
			public void pressed() {
				System.out.println("Chowskies!");
				exitGame();
			}
		};
	}

	private Collection<Inputtable> interactions(){
		Collection<Inputtable> controls = new ArrayList<>();

		controls.add(new Inputtable() {

			@Override
			public KeyCode keyCode() {
				return KeyCode.SPACE;
			}
			@Override
			public void pressed() {
				if(player.handsFull()) {
					if(oven.getCollisionBox().contains(player.center())){
						oven.insert(player.dropHeld());
					}
					else{
						Holdable droppedDough = player.dropHeld();
						if(droppedDough.getClass() == Dough.class) {
							doughs.add((Dough) droppedDough);
						}
						else {
							breads.add((Bread) droppedDough);
						}
					}
				}
				else {
					Holdable taken = null;

					for (Iterator<Bread> breadIt = breads.iterator(); breadIt.hasNext();) {
						Bread bread = breadIt.next();

						if(taken == null && bread.getCollisionBox().contains(player.center()) || bread.getCollisionBox().contains(new Point2D(player.center().x, 10))) { //Grab a single piece of dough
							player.take(bread);
							taken = bread;
						}
					}
					for (Iterator<Dough> doughIt = doughs.iterator(); doughIt.hasNext();) {
						Dough dough = doughIt.next();

						if(taken == null && dough.getCollisionBox().contains(player.center()) && dough.canSplit(1.0)) { //Separate a piece of dough
							Dough splitDough = dough.split(1.0);
							player.take(splitDough);
							taken = splitDough;
						}
						else if(taken == null && dough.getCollisionBox().contains(player.center())) { //Grab a single piece of dough
							player.take(dough);
							taken = dough;
						}
					}

					if(taken != null) {
						//Remove item from world;
						taken.selfRemove(doughs, breads);
					}
				}
			}
		});

		return controls;

	}

	@Override
	public Logical gameLogic() {
		return this;
	}

	@Override
	public Graphical gameGraphics() {
		return this;
	}

	@Override
	public Point2D windowSize() {
		return new Point2D(800, 800);
	}

	@Override
	public String windowTitle() {
		return "Dough!";
	}

	@Override
	public void render(GraphicsContext gc) {
		gc.clearRect(0, 0, 800, 800);

		for (Iterator<Dough> iterator = doughs.iterator(); iterator.hasNext();) {
			Dough dough = iterator.next();
			dough.render(gc);
		}
		oven.render(gc);


		for (Iterator<Bread> iterator = breads.iterator(); iterator.hasNext();) {
			Bread bread = iterator.next();
			bread.render(gc);
		}


		player.render(gc);


		gc.setFill(Color.BLACK);
		gc.strokeRect(0, 0, 800, 799);
	}


	@Override
	public void update(double delta) {

		Set<Set<Dough>> mergers = new HashSet<>();

		for (Iterator<Dough> iterator = doughs.iterator(); iterator.hasNext();) {
			Dough dough = iterator.next();
			dough.accelerate(Gravity.fall(delta*10, dough));
			dough.move(delta);

			for(Iterator<Dough> doughIt2 = doughs.iterator(); doughIt2.hasNext();) {
				Dough other = doughIt2.next();
				if(Collision.isOverlapping(dough, other) && dough != other) {
					//There is a collision!

					Collection<Set<Dough>> foundSets = new ArrayList<>();
					for (Iterator<Set<Dough>> mergIt = mergers.iterator(); mergIt.hasNext();) {
						Set<Dough> set = mergIt.next();

						if(set.contains(dough) || set.contains(other)){
							foundSets.add(set); //This set contains one of the doughs!
						}
					}

					if(foundSets.isEmpty()) {
						Set<Dough> set = new HashSet<>();
						set.add(dough);
						set.add(other);
						mergers.add(set);

					}
					else if(foundSets.size() == 1) {
						Set<Dough> singleSet = foundSets.iterator().next();
						singleSet.add(dough);
						singleSet.add(other);
					}
					else {
						//multiple overlapping sets, now we combine them
						Set<Dough> combinedSet = new HashSet<>();
						combinedSet.add(dough);
						combinedSet.add(other);
						for (Iterator<Set<Dough>> foundIt = foundSets.iterator(); foundIt.hasNext();) {
							Set<Dough> set = foundIt.next();
							combinedSet.addAll(set);
							mergers.remove(set);
						}

						mergers.add(combinedSet);


					}



				}
			}
		}


		for (Iterator<Set<Dough>> mergerIt = mergers.iterator(); mergerIt.hasNext();) {
			Set<Dough> mergingSet = mergerIt.next();
			doughs.removeAll(mergingSet);
			doughs.add(new Dough(mergingSet));

		}

		for (Iterator<Bread> iterator = breads.iterator(); iterator.hasNext();) {
			Bread bread = iterator.next();
			bread.accelerate(Gravity.fall(delta*10, bread));
			bread.move(delta);
		}

		oven.update(delta);

		player.update(delta);

		if(GameClock.isTimerDone(this)) {
			doughs.add( new Dough(new Point2D(400, 750)));
			GameClock.addTimer(this, 1);
		}
	}


}
