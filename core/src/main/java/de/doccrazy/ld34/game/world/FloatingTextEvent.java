package de.doccrazy.ld34.game.world;

import de.doccrazy.shared.game.event.Event;

public class FloatingTextEvent extends Event {
    private final String text;
    private final boolean important;

    public FloatingTextEvent(float x, float y, String text, boolean important) {
        super(x, y);
        this.text = text;
        this.important = important;
    }

    public String getText() {
        return text;
    }

    public boolean isImportant() {
        return important;
    }
}
