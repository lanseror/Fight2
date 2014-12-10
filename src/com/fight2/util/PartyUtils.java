package com.fight2.util;

import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;

public class PartyUtils {

    public static void refreshPartyHpAtk() {
        final PartyInfo partyInfo = GameUserSession.getInstance().getPartyInfo();
        int partyInfoHp = 0;
        int partyInfoAtk = 0;
        for (final Party party : partyInfo.getParties()) {
            if (party == null) {
                continue;
            }
            int partyHp = 0;
            int partyAtk = 0;
            for (final Card card : party.getCards()) {
                if (card == null) {
                    continue;
                }
                partyHp += card.getHp();
                partyAtk += card.getAtk();
            }
            party.setHp(partyHp);
            party.setAtk(partyAtk);
            partyInfoHp += partyHp;
            partyInfoAtk += partyAtk;
        }
        partyInfo.setHp(partyInfoHp);
        partyInfo.setAtk(partyInfoAtk);
    }
}