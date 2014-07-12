package com.fight2.entity;

public class Card {
    private String id;
    private String image;
    private String name;
    private int star;
    private int level;
    private int tier;// Evolution tier
    private int hp;
    private int atk;// Attack value;
    private String skill;
    private int skillLevel;
    private int version;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getStar() {
        return star;
    }

    public void setStar(final int star) {
        this.star = star;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(final int tier) {
        this.tier = tier;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(final int hp) {
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(final int atk) {
        this.atk = atk;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(final String skill) {
        this.skill = skill;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(final int skillLevel) {
        this.skillLevel = skillLevel;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

}
