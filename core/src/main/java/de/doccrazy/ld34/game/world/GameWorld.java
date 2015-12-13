package de.doccrazy.ld34.game.world;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.actor.Level;
import de.doccrazy.ld34.game.actor.Level1Actor;
import de.doccrazy.ld34.game.actor.Level2Actor;
import de.doccrazy.ld34.game.actor.PlayerActor;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.GameState;

import java.util.function.Function;

public class GameWorld extends Box2dWorld<GameWorld> {

    private PlayerActor player;
	private boolean waitingForRound, gameOver;
	private int round;
    private Vector2 mouseTarget;
    private Level level;
    private Function<GameWorld, Level> nextLevel;

	public GameWorld() {
        super(GameRules.GRAVITY);
        RayHandler.useDiffuseLight(true);
        transition(GameState.PRE_GAME);
    }

    @Override
    protected void doTransition(GameState newState) {
        switch (newState) {
            case INIT:
            	waitingForRound = false;
            	if (nextLevel == null) {
            	    nextLevel = Level1Actor::new;
            	}
                level = nextLevel.apply(this);

                addActor(level);
                addActor(player = new PlayerActor(this, level.getSpawn()));
                break;
            case PRE_GAME:
            	round++;
                break;
            case GAME:
            	//Resource.MUSIC.intro.stop();
            	//Resource.MUSIC.victory.stop();
            	//Resource.MUSIC.fight[(int)(Math.random()*Resource.MUSIC.fight.length)].play();
                player.setupKeyboardControl();
                stage.setKeyboardFocus(player);
                break;
            case VICTORY:
            case DEFEAT:
            	//for (Music m : Resource.MUSIC.fight) {
            	//	m.stop();
            	//}
            	//Resource.MUSIC.victory.play();
            	//players[0].setupController(null);
        }
    }

    @Override
    protected void doUpdate(float delta) {
    	switch (getGameState()) {
    	case GAME:
    	    if (player.isDead() || getRemainingTime() <= 0) {
    	        transition(GameState.DEFEAT);
    	    }
    	    if (getScore() >= level.getScoreGoal()) {
    	        transition(GameState.VICTORY);
    	    }
	    	/*if (players[1].isDead()) {
	    		scores[0]++;
	    		transition(GameState.VICTORY);
	    	} else if (players[0].isDead()) {
	    		scores[1]++;
	    		transition(GameState.DEFEAT);
	    	}
	    	if (scores[0] >= GameRules.ROUNDS_TO_WIN || scores[1] >= GameRules.ROUNDS_TO_WIN) {
	    		gameOver = true;
	    	}*/
    		break;
    	case PRE_GAME:
            if (getStateTime() > 0.5f) {
                transition(GameState.GAME);
            }
    		break;
		default:
    	}
    }

    public PlayerActor getPlayer() {
		return player;
	}

    public int getRound() {
    	return round;
    }

    public boolean isGameOver() {
    	return gameOver;
    }

    public void waitingForRound() {
    	waitingForRound = true;
    }

    public boolean isWaitingForRound() {
    	return waitingForRound;
    }

    public void setMouseTarget(Vector2 mouseTarget) {
        this.mouseTarget = mouseTarget;
    }

    public Vector2 getMouseTarget() {
        return mouseTarget;
    }

    public Level getLevel() {
        return level;
    }

    public float getRemainingTime() {
        return Math.max(0, level.getTime() - (isGameFinished() ? getLastStateTime() : getStateTime()));
    }

    public void setNextLevel(Function<GameWorld, Level> nextLevel) {
        this.nextLevel = nextLevel;
    }

    @Override
    public void addScore(int value) {
        super.addScore(value);
        //Resource.SOUND.catchFly.play();
    }
}
