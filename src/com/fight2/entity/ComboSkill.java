package com.fight2.entity;

import java.util.List;

import com.fight2.entity.battle.SkillOperation;

public class ComboSkill {
    private int id;
    private String name;
    private String icon;
    private List<Card> cards;
    private List<SkillOperation> operations;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(final List<Card> cards) {
        this.cards = cards;
    }

    public List<SkillOperation> getOperations() {
        return operations;
    }

    public void setOperations(final List<SkillOperation> operations) {
        this.operations = operations;
    }

}
