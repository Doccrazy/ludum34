package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.data.GameRules;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.SpriterActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class CrapActor extends SpriterActor<GameWorld> implements Hittable {
    private static final float RADIUS = 0.3f;

    public CrapActor(GameWorld world, Vector2 spawn) {
        super(world, spawn, false, Resource.SPRITER.crap, Resource.SPRITER::getDrawer);
        setzOrder(20);
        player.setScale(0.0075f);
        player.setAnimation("idle");
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forStatic(spawn)
                .fixShape(ShapeBuilder.circle(RADIUS))
                .noRotate();
    }

    @Override
    protected void doAct(float delta) {
        super.doAct(delta);
        if (world.getPlayer().isCaughtInShockwave(body.getPosition())) {
            world.addActor(new SmallFireActor(world, body.getPosition()));
            kill();
        }
    }

    @Override
    public int getPoints() {
        return GameRules.POINTS_CRAP;
    }

    public void runOver() {
        world.getPlayer().damage(GameRules.DAMAGE_CRAP);
        kill();
    }
}
