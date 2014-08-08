package com.fight2.entity.battle;

public enum SkillApplyParty {
    Self(false, "本方团队"),
    Opponent(false, "敌方团队"),
    Leader(false, "本方领军团队"),
    OpponentLeader(false, "敌方领军团队"),
    SelfAll(true, "本方团队（全体）"),
    OpponentAll(true, "敌方团队（全体）");

    private final boolean isIndividual;
    private final String description;

    private SkillApplyParty(final boolean isIndividual, final String description) {
        this.isIndividual = isIndividual;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIndividual() {
        return isIndividual;
    }

}
