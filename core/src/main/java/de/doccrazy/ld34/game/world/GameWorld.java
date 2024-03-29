package de.doccrazy.ld34.game.world;

import box2dLight.RayHandler;
import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.actor.*;
import de.doccrazy.shared.game.actor.WorldActor;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GameWorld extends Box2dWorld<GameWorld> {

    private final Function<GameWorld, Level>[] levels;
    private int currentLevel = 0;
    private PlayerActor player;
	private boolean waitingForRound, gameOver;
	private int round;
    private Vector2 mouseTarget;
    private Level level;
    private Map<Class<?>, String> help = new HashMap<Class<?>, String>() {{
        put(FussballActor.class, "Where did that come from??\nI should wait for the owner to properly \"thank\" him...");
        put(ChildActor.class, "Comeon, get your ball, you brat!\nThen I will get you...");
        put(DogActor.class, "Damn pests, spreading happiness and poop over my precious lawn!\nSeems like the dog was attracted by the ball...");
    }};

	public GameWorld(Function<GameWorld, Level>... levels) {
        super(GameRules.GRAVITY);
        if (levels.length == 0) {
            throw new IllegalArgumentException("No levels");
        }
        this.levels = levels;
        RayHandler.useDiffuseLight(true);
        //transition(GameState.PRE_GAME);
    }

    @Override
    protected void doTransition(GameState newState) {
        Resource.SOUND.engine.stop();
        switch (newState) {
            case INIT:
                Resource.MUSIC.victory.stop();
            	waitingForRound = false;
                level = levels[currentLevel].apply(this);

                addActor(level);
                addActor(player = new PlayerActor(this, level.getSpawn()));
                break;
            case PRE_GAME:
            	round++;
                break;
            case GAME:
            	//Resource.MUSIC.intro.stop();
            	Resource.MUSIC.victory.stop();
            	//Resource.MUSIC.fight[(int)(Math.random()*Resource.MUSIC.fight.length)].play();
                Resource.SOUND.engine.setLooping(Resource.SOUND.engine.play(), true);
                player.setupKeyboardControl();
                stage.setKeyboardFocus(player);
                break;
            case VICTORY:
                Resource.MUSIC.victory.play();
                if (currentLevel + 1 < levels.length) {
                    currentLevel++;
                } else {
                    gameOver = true;
                }
            case DEFEAT:
                Resource.MUSIC.music1.stop();
                Resource.MUSIC.music2.stop();
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

    public void advanceLevel() {
        if (currentLevel + 1 < levels.length) {
            currentLevel++;
        }
    }

    public void resetAll() {
        currentLevel = 0;
        reset();
    }

    @Override
    public void addScore(int value) {
        super.addScore(value);
        //Resource.SOUND.catchFly.play();
    }

    @Override
    public void addActor(WorldActor<GameWorld> actor) {
        super.addActor(actor);
        if (help.containsKey(actor.getClass())) {
            postEvent(new HelpEvent(help.get(actor.getClass())));
            help.remove(actor.getClass());
        }
    }
}
