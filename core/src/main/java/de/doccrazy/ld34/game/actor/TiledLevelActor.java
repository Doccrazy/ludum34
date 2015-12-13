package de.doccrazy.ld34.game.actor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.ShortArray;
import de.doccrazy.ld34.game.world.GameWorld;
import de.doccrazy.shared.game.world.BodyBuilder;

import java.util.ArrayList;
import java.util.List;

public class TiledLevelActor extends Level {
    private final TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private final float unitScale;
    private List<Body> bodies = new ArrayList<>();

    public TiledLevelActor(GameWorld world, TiledMap map) {
        super(world);
        this.map = map;
        unitScale = 1 / 32f;
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        for (MapObject object : map.getLayers().get("objects").getObjects()) {
            if (object instanceof PolygonMapObject && "solid".equals(object.getProperties().get("type"))) {
                Polygon poly = ((PolygonMapObject) object).getPolygon();
                float[] transformedVertices = poly.getTransformedVertices();
                ShortArray tris = triangulator.computeTriangles(transformedVertices);

                for (int i = 0; i < tris.size; i += 3) {
                    float[] tri = new float[6];
                    tri[0] = transformedVertices[tris.get(i)*2]*unitScale;
                    tri[1] = transformedVertices[tris.get(i)*2+1]*unitScale;
                    tri[2] = transformedVertices[tris.get(i+1)*2]*unitScale;
                    tri[3] = transformedVertices[tris.get(i+1)*2+1]*unitScale;
                    tri[4] = transformedVertices[tris.get(i+2)*2]*unitScale;
                    tri[5] = transformedVertices[tris.get(i+2)*2+1]*unitScale;

                    PolygonShape shape = new PolygonShape();
                    shape.set(tri);
                    bodies.add(BodyBuilder.forStatic(new Vector2(0, 0)).fixShape(shape).build(world));
                }
            }
        }
    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }

    @Override
    public Rectangle getGrassBox() {
        return null;
    }

    @Override
    public Vector2 getSpawn() {
        EllipseMapObject spawn = (EllipseMapObject)map.getLayers().get("objects").getObjects().get("spawn");
        Vector2 v = new Vector2((spawn.getEllipse().x + spawn.getEllipse().width/2f) * unitScale, (spawn.getEllipse().y + spawn.getEllipse().height/2f) * unitScale);
        return v;
    }

    @Override
    public int getScoreGoal() {
        return 99999;
    }

    @Override
    public float getTime() {
        return 99999;
    }

    @Override
    protected void doAct(float delta) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (renderer == null) {
            renderer = new OrthogonalTiledMapRenderer(map, unitScale, batch);
        }
        batch.end();
        renderer.setView((OrthographicCamera) getStage().getCamera());
        renderer.render();
        batch.begin();
    }

    @Override
    protected void doRemove() {
        super.doRemove();
        for (Body body : bodies) {
            world.box2dWorld.destroyBody(body);
        }
    }
}
