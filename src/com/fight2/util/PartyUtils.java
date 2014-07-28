package com.fight2.util;

import java.io.IOException;

import org.andengine.util.debug.Debug;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;

import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;

public class PartyUtils {
    public static final String HOST_URL = "http://192.168.1.178:8080/Fight2Server";

    public static boolean saveParties() {
        final String url = HOST_URL + "/party/edit";
        final GameUserSession session = GameUserSession.getInstance();
        final Card[][] parties = session.getParties();
        final JSONArray partyJson = new JSONArray();
        for (final Card[] party : parties) {
            final JSONArray cardJson = new JSONArray();
            for (final Card card : party) {
                if (card != null) {
                    cardJson.put(card.getId());
                } else {
                    cardJson.put(-1);
                }
            }
            partyJson.put(cardJson);
        }

        try {
            return HttpUtils.postJSONString(url, partyJson.toString());
        } catch (final ClientProtocolException e) {
            Debug.e(e);
        } catch (final IOException e) {
            Debug.e(e);
        }
        return false;
    }
}