package de.doccrazy.ld34.core;

import de.doccrazy.ld34.resources.FontResources;
import de.doccrazy.ld34.resources.GfxResources;
import de.doccrazy.ld34.resources.MusicResources;
import de.doccrazy.ld34.resources.SoundResources;

public class Resource {
    public static GfxResources GFX;
    //public static SpriterResources SPRITER;
    public static FontResources FONT;
    public static SoundResources SOUND;
    public static MusicResources MUSIC;

    private Resource() {
    }

    public static void init() {
        GFX = new GfxResources();
        //SPRITER = new SpriterResources(GFX.getAtlas());
        FONT = new FontResources();
        SOUND = new SoundResources();
        MUSIC = new MusicResources();
    }
}

