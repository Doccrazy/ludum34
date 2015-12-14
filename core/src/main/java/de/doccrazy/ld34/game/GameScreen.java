package de.doccrazy.ld34.game;

import com.badlogic.gdx.scenes.scene2d.Stage;

import de.doccrazy.ld34.game.actor.Level1Actor;
import de.doccrazy.ld34.game.actor.Level2Actor;
import de.doccrazy.ld34.game.ui.UiRoot;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.BaseGameScreen;

public class GameScreen extends BaseGameScreen<GameWorld, GameRenderer> {

	@Override
	protected GameWorld createWorld() {
		return new GameWorld(Level1Actor::new, Level2Actor::new);
	}

	@Override
	protected GameRenderer createRenderer(GameWorld world) {
		return new GameRenderer(world);
	}

	@Override
	protected void createUI(Stage uiStage, GameWorld world, GameRenderer renderer) {
		new UiRoot(uiStage, world, renderer);
	}
}
