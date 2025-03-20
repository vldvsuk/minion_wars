package parsers;

import effects.Effect;
import models.Minion;
import powers.Power;

import java.lang.reflect.Field;
import java.util.List;

public class HoofdParser {
    private MinionParser minionParser;
    private PowerParser powerParser;
    private FieldParser fieldParser;
    private EffectParser effectParser;

    public HoofdParser() {
        this.minionParser = new MinionParser();
        this.powerParser = new PowerParser();
        this.fieldParser = new FieldParser();
        this.effectParser = new EffectParser();
    }

    public List<Minion> parseMinions() {
        return minionParser.parseMinions();
    }

    public List<Power> parsePowers() {
        return powerParser.parsePowers();
    }

    public Field parseField() {
        return fieldParser.parseField();
    }

    public List<Effect> parseEffects() {
        return effectParser.parseEffects();
    }
