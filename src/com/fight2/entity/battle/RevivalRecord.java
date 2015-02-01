package com.fight2.entity.battle;


public class RevivalRecord {
    private int comboId;
    private int point;
    private int partyNumber;
    private RevivalType type;

    public int getComboId() {
        return comboId;
    }

    public void setComboId(final int comboId) {
        this.comboId = comboId;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(final int point) {
        this.point = point;
    }

    public int getPartyNumber() {
        return partyNumber;
    }

    public void setPartyNumber(final int partyNumber) {
        this.partyNumber = partyNumber;
    }

    public RevivalType getType() {
        return type;
    }

    public void setType(final RevivalType type) {
        this.type = type;
    }

    public enum RevivalType {
        AfterSkill,
        AfterAttack;
    }
}
