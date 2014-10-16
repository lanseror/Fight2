package com.fight2.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.entity.Bid;
import com.fight2.entity.Bid.BidItemType;
import com.fight2.entity.Bid.BidStatus;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Guild;
import com.fight2.entity.GuildArenaUser;
import com.fight2.entity.GuildStoreroom;
import com.fight2.entity.User;

public class GuildUtils {

    public static boolean applyGuild(final String name) {
        final String url = HttpUtils.HOST_URL + "/guild/apply";
        final JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("name", URLEncoder.encode(name, "UTF-8"));
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            final String responseJsonStr = HttpUtils.postJSONString(url, infoJson.toString());
            final JSONObject responseJson = new JSONObject(responseJsonStr);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static Guild getUserGuild() {
        final String url = HttpUtils.HOST_URL + "/guild/get-user-guild";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            if (status < 0) {
                return null;
            }
            final JSONObject guildJson = responseJson.getJSONObject("guild");
            final Guild guild = new Guild();
            guild.setId(guildJson.getInt("id"));
            guild.setName(guildJson.getString("name"));
            guild.setNotice(guildJson.optString("notice"));
            guild.setQq(guildJson.optString("qq"));
            guild.setPollEnabled(guildJson.getBoolean("pollEnabled"));
            final User president = new User();
            final JSONObject presidentJson = guildJson.getJSONObject("president");
            president.setId(presidentJson.getInt("id"));
            president.setName(presidentJson.getString("name"));
            guild.setPresident(president);
            // Arena user
            final JSONArray arenaUserJSONArray = guildJson.getJSONArray("arenaUsers");
            final SparseArray<GuildArenaUser> arenaUsers = new SparseArray<GuildArenaUser>();
            for (int i = 0; i < arenaUserJSONArray.length(); i++) {
                final JSONObject arenaUserJSON = arenaUserJSONArray.getJSONObject(i);
                final GuildArenaUser guildArenaUser = new GuildArenaUser();
                guildArenaUser.setId(arenaUserJSON.getInt("id"));
                guildArenaUser.setLocked(arenaUserJSON.getBoolean("locked"));
                arenaUsers.append(guildArenaUser.getId(), guildArenaUser);
            }
            guild.setArenaUsers(arenaUsers);

            final GameUserSession session = GameUserSession.getInstance();
            session.setGuildContribution(responseJson.getInt("guildContribution"));
            return guild;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static GuildStoreroom getGuildStoreroom(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/guild/get-storeroom";
        try {
            final JSONObject storeroomJson = HttpUtils.getJSONFromUrl(url);
            final GuildStoreroom storeroom = new GuildStoreroom();
            storeroom.setId(storeroomJson.getInt("id"));
            storeroom.setCoin(storeroomJson.getInt("coin"));
            storeroom.setStamina(storeroomJson.getInt("stamina"));
            storeroom.setTicket(storeroomJson.getInt("ticket"));
            final JSONArray cardJsonArray = storeroomJson.getJSONArray("cards");
            final List<Card> cards = new ArrayList<Card>();
            for (int i = 0; i < cardJsonArray.length(); i++) {
                final JSONObject cardJson = cardJsonArray.getJSONObject(i);
                final Card card = new Card();
                card.setId(cardJson.getInt("id"));
                card.setHp(cardJson.getInt("hp"));
                card.setAtk(cardJson.getInt("atk"));
                card.setName(cardJson.getString("name"));
                card.setStar(cardJson.getInt("star"));
                final String image = cardJson.getString("image");
                if (image != null && !"".equals(image)) {
                    final String localImage = ImageUtils.getLocalString(image, activity);
                    card.setImage(localImage);
                    TextureFactory.getInstance().addCardResource(activity, localImage);
                }
                card.setAmount(cardJson.getInt("amount"));
                cards.add(card);
            }
            storeroom.setCards(cards);

            return storeroom;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean joinGuild(final int id) {
        final String url = HttpUtils.HOST_URL + "/guild/join?id=" + id;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean quitGuild() {
        final String url = HttpUtils.HOST_URL + "/guild/quit";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean addArenaUser(final int id) {
        final String url = HttpUtils.HOST_URL + "/guild/add-arena-user?id=" + id;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static int removeArenaUser(final int id) {
        final String url = HttpUtils.HOST_URL + "/guild/remove-arena-user?id=" + id;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<User> getMembers(final int id) {
        final String url = HttpUtils.HOST_URL + "/guild/members?id=" + id;
        try {
            final List<User> members = new ArrayList<User>();
            final JSONArray memberJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < memberJsonArray.length(); i++) {
                final JSONObject memberJson = memberJsonArray.getJSONObject(i);
                final User member = new User();
                member.setId(memberJson.getInt("id"));
                member.setName(memberJson.getString("name"));
                members.add(member);
            }
            return members;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<Guild> getTopGuilds() {
        final String url = HttpUtils.HOST_URL + "/guild/list-tops";
        try {
            final List<Guild> guilds = new ArrayList<Guild>();
            final JSONArray guildJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < guildJsonArray.length(); i++) {
                final JSONObject guildJson = guildJsonArray.getJSONObject(i);
                final Guild guild = new Guild();
                guild.setId(guildJson.getInt("id"));
                guild.setName(guildJson.getString("name"));
                final User president = new User();
                final JSONObject presidentJson = guildJson.getJSONObject("president");
                president.setId(presidentJson.getInt("id"));
                president.setName(presidentJson.getString("name"));
                guild.setPresident(president);
                guilds.add(guild);
            }
            return guilds;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean editGuild(final Guild guild) {
        final String url = HttpUtils.HOST_URL + "/guild/president-edit";
        final JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("id", guild.getId());
            infoJson.put("qq", guild.getQq());
            infoJson.put("notice", URLEncoder.encode(guild.getNotice(), "UTF-8"));
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        try {
            final String responseJsonStr = HttpUtils.postJSONString(url, infoJson.toString());
            final JSONObject responseJson = new JSONObject(responseJsonStr);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    public static int vote(final int candidateId) {
        final String url = HttpUtils.HOST_URL + "/guild/vote?id=" + candidateId;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status;
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return 2;
    }

    public static boolean hasVoted() {
        final String url = HttpUtils.HOST_URL + "/guild/has-voted";
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 1;
        } catch (final ClientProtocolException e) {
            LogUtils.e(e);
        } catch (final Exception e) {
            LogUtils.e(e);
        }
        return true;
    }

    public static int sendCardToBid(final int cardTemplateId) {
        final String url = HttpUtils.HOST_URL + "/guild/sent-card-to-bid?id=" + cardTemplateId;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int sendItemToBid(final BidItemType itemType) {
        final String url = HttpUtils.HOST_URL + "/guild/sent-item-to-bid?itemType=" + itemType;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Bid> getBids(final GameActivity activity) {
        final String url = HttpUtils.HOST_URL + "/bid/list-by-guild";
        try {
            final List<Bid> bids = new ArrayList<Bid>();
            final JSONArray bidJsonArray = HttpUtils.getJSONArrayFromUrl(url);
            for (int i = 0; i < bidJsonArray.length(); i++) {
                final JSONObject bidJson = bidJsonArray.getJSONObject(i);
                final Bid bid = new Bid();
                bid.setId(bidJson.getInt("id"));
                bid.setAmount(bidJson.getInt("amount"));
                bid.setPrice(bidJson.getInt("price"));
                bid.setStatus(BidStatus.valueOf(bidJson.getString("status")));
                final BidItemType itemType = BidItemType.valueOf(bidJson.getString("type"));
                bid.setType(itemType);
                if (itemType == BidItemType.Card) {
                    final Card card = new Card();
                    final JSONObject cardJson = bidJson.getJSONObject("card");
                    card.setId(cardJson.getInt("id"));
                    card.setHp(cardJson.getInt("hp"));
                    card.setAtk(cardJson.getInt("atk"));
                    card.setName(cardJson.getString("name"));
                    card.setStar(cardJson.getInt("star"));
                    final String image = cardJson.getString("image");
                    if (image != null && !"".equals(image)) {
                        final String localImage = ImageUtils.getLocalString(image, activity);
                        card.setImage(localImage);
                        TextureFactory.getInstance().addCardResource(activity, localImage);
                    }
                    card.setAmount(cardJson.getInt("amount"));
                    bid.setCard(card);
                }
                bid.setVersion(bidJson.getInt("version"));
                bid.setMyBid(bidJson.getBoolean("isMyBid"));
                bid.setRemainTime(bidJson.getInt("remainTime"));
                bids.add(bid);
            }
            return bids;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } catch (final JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public static int bid(final Bid bid) {
        final String url = HttpUtils.HOST_URL + "/bid/bid?id=" + bid.getId() + "&version=" + bid.getVersion();
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            if (status == 0 || status == 2) {
                final JSONObject bidJson = responseJson.getJSONObject("bid");
                bid.setPrice(bidJson.getInt("price"));
                bid.setVersion(bidJson.getInt("version"));
                bid.setRemainTime(bidJson.getInt("remainTime"));
            }
            return status;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkMyBid(final int bidId) {
        final String url = HttpUtils.HOST_URL + "/bid/check-my-bid?id=" + bidId;
        try {
            final JSONObject responseJson = HttpUtils.getJSONFromUrl(url);
            final int status = responseJson.getInt("status");
            return status == 0;
        } catch (final ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}