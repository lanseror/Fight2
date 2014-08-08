package com.fight2.entity.battle;

public class SkillOperation {

    private int sign; // +1/-1

    private int point;// Percentage, 1/100.

    private SkillType skillType;

    private SkillPointAttribute skillPointAttribute;

    private SkillApplyParty skillApplyParty;

    public int getSign() {
        return sign;
    }

    public void setSign(final int sign) {
        this.sign = sign;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(final int point) {
        this.point = point;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(final SkillType skillType) {
        this.skillType = skillType;
    }

    public SkillPointAttribute getSkillPointAttribute() {
        return skillPointAttribute;
    }

    public void setSkillPointAttribute(final SkillPointAttribute skillPointAttribute) {
        this.skillPointAttribute = skillPointAttribute;
    }

    public SkillApplyParty getSkillApplyParty() {
        return skillApplyParty;
    }

    public void setSkillApplyParty(final SkillApplyParty skillApplyParty) {
        this.skillApplyParty = skillApplyParty;
    }

}
