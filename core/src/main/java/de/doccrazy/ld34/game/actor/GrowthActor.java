package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import de.doccrazy.ld34.core.Resource;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.ShapeBuilder;

import java.util.ArrayList;
import java.util.List;

public class GrowthActor extends ShapeActor<GameWorld> {
    public static final float RADIUS = 0.1f;
    public static final float LEAF_SIZE = 0.5f;
    private float angle;
    private Body previous;
    private Joint joint;
    private List<Leaf> leaves = new ArrayList<>();
    private boolean hasGrowShape;

    public GrowthActor(GameWorld world, Vector2 spawn, float angle, Body previous) {
        super(world, spawn, false);
        this.angle = angle;
        this.previous = previous;
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forDynamic(spawn).fixShape(ShapeBuilder.circle(RADIUS)).rotation(angle).noRotate().gravityScale(0.8f).damping(0.9f, 0);
    }

    @Override
    protected void init() {
        super.init();
        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.initialize(previous, getBody(), Vector2.Zero, Vector2.Zero);
        jointDef.localAnchorA.x = 0;
        jointDef.localAnchorB.x = 0;
        jointDef.localAnchorA.y = 0;
        jointDef.localAnchorB.y = 0;
        jointDef.length = 0.3f;
        jointDef.frequencyHz = 60;
        jointDef.dampingRatio = 0.8f;
        this.joint = world.box2dWorld.createJoint(jointDef);
    }

    @Override
    protected void doAct(float delta) {
        super.doAct(delta);
        if (stateTime > 0.5f && leaves.size() == 0) {
            Leaf l = new Leaf();
            l.spawnTime = stateTime;
            l.sprite = Resource.GFX.leaves[MathUtils.random(Resource.GFX.leaves.length-1)];
            l.angle = MathUtils.random(360f);
            l.pos = MathUtils.random(0f, 1f);
            leaves.add(l);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (Leaf leaf : leaves) {
            Vector2 d = getBody().getPosition().cpy().sub(previous.getPosition());
            Vector2 pos = previous.getPosition().cpy().add(d.scl(leaf.pos));
            float ar = leaf.sprite.getWidth()/leaf.sprite.getHeight();
            float scale = Interpolation.bounceIn.apply(MathUtils.clamp((stateTime - leaf.spawnTime)*1.5f, 0, 1f));
            batch.draw(leaf.sprite, pos.x - (LEAF_SIZE*ar)/2f, pos.y, (LEAF_SIZE*ar)/2f, 0, LEAF_SIZE * ar, LEAF_SIZE, scale, scale, leaf.angle);
        }
    }

    @Override
    protected void doRemove() {
        //world.box2dWorld.destroyJoint(joint);
        super.doRemove();
    }

    public void setGrowDist(float dist, float angle) {
        if (dist > 0.005f) {
            if (!hasGrowShape) {
                body.createFixture(ShapeBuilder.circle(RADIUS).build(), 1f);
                hasGrowShape = true;
            }
            float rotation = body.getAngle() + angle;
            Vector2 dst = new Vector2(1, 0).rotateRad(rotation).scl(dist);

            ((CircleShape)body.getFixtureList().get(1).getShape()).setPosition(dst);
        } else {
            if (hasGrowShape) {
                body.destroyFixture(body.getFixtureList().get(1));
                hasGrowShape = false;
            }
        }
    }
}

class Leaf {
    float pos;
    float angle;
    Sprite sprite;
    float spawnTime;
}
