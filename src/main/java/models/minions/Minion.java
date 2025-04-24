package models.minions;


import models.effects.Effect;

import java.util.ArrayList;
import java.util.List;

public class Minion {
    private final String type;
    private final String name;
    private final int cost;
    private final int movement;
    private final String range;
    private final int attack;
    private final int defence;
    private int currentDefence;
    private final String effect;
    private final int effectValue;
    private int healCount = 0;
    private List<Effect> activeEffects = new ArrayList<>();
    private int currentAttack;
    private int currentMovement;

    // Constructor voor alle minions
    public Minion(String type, String name, int cost, int movement, String range, int attack, int defence, String effect, int effectValue) {
        this.type = type;
        this.name = name;
        this.cost = cost;
        this.movement = movement;
        this.range = range;
        this.attack = attack;
        this.defence = defence;
        this.currentDefence = defence;
        this.effect = effect;
        this.effectValue = effectValue;
        this.currentAttack = attack;
        this.currentMovement = movement;
    }

    // Getters

    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getMovement() {
        return movement;
    }

    public String getRange() {
        return range;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }

    public int getCurrentDefence() {
        return currentDefence;
    }

    public void verminderCurrentDefence(int getal) {
        currentDefence -= getal;

    }
    public void setCurrentDefence (int getal){
        currentDefence = getal;
    }

    public String getEffect() {
        return effect;
    }

    public int getEffectValue() {
        return effectValue;
    }

    public int getHealCount() {
        return healCount;
    }

    public void setHealCount(int healCount) {
        this.healCount = healCount;
    }

    public Minion copy() {
        return new Minion(
                this.type,
                this.name,
                this.cost,
                this.movement,
                this.range,
                this.attack,
                this.defence ,
                this.effect,
                this.effectValue
        );
    }
    public boolean hasSpecialAbility() {
        return effectValue != 0;    // heeft ook geen effect
    }

    public void addEffect(Effect effect) {
        activeEffects.add(effect);
        applyEffect(effect);
    }

    private void applyEffect(Effect effect) {
        switch (effect.getType().toLowerCase()) {
            case "rage" -> this.currentAttack += effect.getValue();
            case "slow" -> this.currentMovement -= effect.getValue();
            case "blindness" -> this.currentAttack -= effect.getValue();
            case "burn", "poison" -> this.currentDefence -= effect.getValue();
            case "heal" -> this.currentDefence += effect.getValue();
        }
    }

    public void removeEffect(Effect effect) {
        activeEffects.remove(effect);
        resetEffect(effect);
    }

    private void resetEffect(Effect effect) {
        switch (effect.getType().toLowerCase()) {
            case "rage", "blindness" -> this.currentAttack = attack;
            case "slow" -> this.currentMovement = movement;
        }
    }

    public List<Effect> getActiveEffects() {
        return activeEffects;
    }
}