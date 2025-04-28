package models.minions;


import models.effects.Effect;

import java.util.ArrayList;
import java.util.List;

public class Minion {
    private final String type;
    private final String name;
    private final int cost;
    private final int movement;
    private final int minRange;
    private final int maxRange;
    private final int attack;
    private final int defence;
    private int currentDefence;
    private final String effect;
    private final int effectValue;
    private int healCount = 0;
    private final List<Effect> activeEffects = new ArrayList<>();
    private int currentMovement;
    private int currentMaxRange;
    private int currentAttack;
    private boolean isParalized;
    private boolean specialAttackUsed = false;
    private int restCount = 0;


    // Constructor voor alle minions
    public Minion(String type, String name, int cost, int movement, int minRange, int maxRange, int attack, int defence, String effect, int effectValue) {
        this.type = type;
        this.name = name;
        this.cost = cost;
        this.movement = movement;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.attack = attack;
        this.defence = defence;
        this.currentDefence = defence;
        this.effect = effect;
        this.effectValue = effectValue;
        this.currentMovement = movement;
        this.currentMaxRange = maxRange;
        this.currentAttack = attack;
        this.isParalized = false;

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
    public int getCurrentMovement(){
        return currentMovement;
    }

    public int getMinRange() {
        return minRange;
    }
    public int getMaxRange() {
        return maxRange;
    }

    public int getCurrentMaxRange() {
        return currentMaxRange;
    }

    public int getAttack() {
        return attack;
    }

    public int getCurrentAttack() {
        return currentAttack;
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
    public boolean isParalized() {
        return isParalized;
    }

    public Minion copy() {
        return new Minion(
                this.type,
                this.name,
                this.cost,
                this.movement,
                this.minRange,
                this.maxRange,
                this.attack,
                this.defence ,
                this.effect,
                this.effectValue
        );
    }
    public boolean hasSpecialAbility() {
        return !effect.equals("none") && !specialAttackUsed;
    }

    public void addEffect(Effect effect) {
        activeEffects.add(effect);
    }

    public void applyEffect(Effect effect) {
        switch (effect.getType().toLowerCase()) {
            case "rage" -> this.currentAttack +=  effect.getValue();
            case "slow" -> this.currentMovement -= effect.getValue();
            case "blindness" -> this.currentMaxRange -=  effect.getValue();
            case "burn", "poison" -> this.currentDefence -= effect.getValue();
            case "paralysis" -> isParalized = true;
            case "heal" -> this.currentDefence = Math.min(
                    currentDefence + effect.getValue(),
                    defence
            );

        }
    }


    public void removeEffect(Effect effect) {
        activeEffects.remove(effect);
        resetEffect(effect);
    }

    private void resetEffect(Effect effect) {
        switch (effect.getType().toLowerCase()) {
            case "rage" -> this.currentAttack = attack;
            case "slow" -> this.currentMovement = movement;
            case "blindness" -> this.currentMaxRange = maxRange;
            case "burn", "poison" -> {} // Geen reset nodig voor deze
            case "heal" -> {} // Genezing is tijdelijk
            case "paralysis" -> isParalized = false;
        }
    }

    public List<Effect> getActiveEffects() {
        return activeEffects;
    }
    public boolean isSpecialAttackUsed() {
        return specialAttackUsed;
    }

    public void setSpecialAttackUsed(boolean specialAttackUsed) {
        this.specialAttackUsed = specialAttackUsed;
    }

    public int getRestCount() {
        return restCount;
    }

    public void setRestCount(int restCount) {
        this.restCount = restCount;
    }
}