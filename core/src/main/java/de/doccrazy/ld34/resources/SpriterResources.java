package de.doccrazy.ld34.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brashmonkey.spriter.Entity;
import de.doccrazy.shared.spriter.ResourcesBaseSpriter;

public class SpriterResources extends ResourcesBaseSpriter {
    public Entity dog = entity("dog");
    public Entity child = entity("child");
    public Entity crap = entity("crap");

    public SpriterResources(TextureAtlas atlas) {
        super("Game.scml", atlas);
    }
}
