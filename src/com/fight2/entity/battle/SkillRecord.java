package com.fight2.entity.battle;

import java.util.List;

public class SkillRecord {
    private int cardIndex;
    private String name;
    private String effect;
    private List<SkillOperation> operations;

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(final int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(final String effect) {
        this.effect = effect;
    }

    public List<SkillOperation> getOperations() {
        return operations;
    }

    public void setOperations(final List<SkillOperation> operations) {
        this.operations = operations;
    }

}
