package org.waziup.socialbackend.core;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.restfb.types.send.PhoneMessageRecipient;
import com.restfb.types.send.SendResponse;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import org.bson.Document;
import org.waziup.socialbackend.ejb.NotificationBean;
import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author Drabo Constantin
 *
 */
@Model
@Path("socials")
public class Socials implements Serializable {

    ConfigurationBuilder config;
    TwitterFactory tf;
    Twitter twitter;
    DirectMessage tweet;
    @Inject
    NotificationBean notificationbean;
    int statusCode;

    /**
     * Constructor
     */
    public Socials() {
        ResourceBundle waziupNotificationBundle = ResourceBundle.getBundle("parameters");
        config = new ConfigurationBuilder();
        config.setDebugEnabled(true)
                .setOAuthConsumerKey(waziupNotificationBundle.getString("OAuthConsumerKey"))
                .setOAuthConsumerSecret(waziupNotificationBundle.getString("OAuthConsumerSecret"))
                .setOAuthAccessToken(waziupNotificationBundle.getString("OAuthAccessToken"))
                .setOAuthAccessTokenSecret(waziupNotificationBundle.getString("OAuthAccessTokenSecret"));
        tf = new TwitterFactory(config.build());
        twitter = tf.getInstance();
    }

    /**
     *
     * @param doc
     * @return
     */
    @POST
    @Produces("application/json")
    public Response sendNotification(Document doc) {
        String channel = null;
        if (doc != null) {
            channel = doc.getString("channel");
            switch (channel) {
                case "facebook":
                    sendFacebookMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                    break;
                case "twitter":
                    sendTwitterMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                    break;
                case "sms":
                    sendSMSMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                    break;
                case "whatsapp":
                    sendWhatsAppMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                    break;
                default:
                    sendTwitterMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
            }

        } else {
            channel = "Invalid document";
        }
        return Response.accepted(channel).build();
    }

    /**
     *
     * @return
     */
    @GET
    @Produces("application/json")
    public List<Document> getMessageHistory() {
        return notificationbean.queryNotification();
    }

    /**
     *
     * @return
     */
    @GET
    @Produces("application/json")
    @Path("/{id}")
    public Document getAMessageHistory(@PathParam("id") String id) {
        return notificationbean.queryANotification(id);
    }

    /**
     *
     * @return
     */
    @DELETE
    @Produces("application/json")
    @Path("/{id}")
    public Response deleteOneNotification(@PathParam("id") String id) {
        notificationbean.deleteOneNotification(id);
        return Response.ok(id + "  deleted from notification").build();
    }

    /**
     *
     * @return
     */
    @DELETE
    @Produces("application/json")
    public Response deleteAllNotification() {
        notificationbean.deleteAllNotification();
        return Response.ok("All the notifications deleted").build();
    }

    /**
     *
     * @param userSender
     * @param userReceiver
     * @param receiverProfile
     * @param message
     */
    public void sendFacebookMessage(/*String userSender,*/ String userReceiver, /*String receiverProfile,*/ String message) {
         PhoneMessageRecipient recipient = new PhoneMessageRecipient(userReceiver);
       Message msg;
        msg = new Message(message);
      
String pageAccessToken = "EAAXh1hmHeq4BAIekZAddN3lvuKPzRCERfNmdT9B4oKJ8vC0OZCGl2a1GYty8nZB5ZBifhbBbbK3yNrqzgv0YTk3BDyQK9HjLnMFiZA4hJKdIGVgo9HHZC9VH0ZAO9rEx5zE7GmJZASoGZAlj1rnuuFN6qf6sZAqvSwqZC1Xo4JrOQyvnQZDZD";
FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_2_6);
 pageClient.publish("me/messages", SendResponse.class,
     Parameter.with("recipient", recipient), 
	 Parameter.with("message", msg )); 

    }

    /**
     *
     * @param userSender
     * @param userReceiver
     * @param receiverProfile
     * @param message
     */
    public void sendTwitterMessage(String userSender, String userReceiver, String receiverProfile, String message) {
        try {
            tweet = twitter.sendDirectMessage(receiverProfile, message);
            Document notification = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverProfile)
                    .append("channel", "SMS")
                    .append("message", message)
                    .append("status", "Delivered")
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(notification);
        } catch (TwitterException ex) {
            Document notificationfailure = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverProfile)
                    .append("channel", "SMS")
                    .append("message", message)
                    .append("status", "Not delivered")
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(notificationfailure);
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, tweet.getId() + " did not deliver", ex);
        } catch (WebApplicationException webex) {
            Logger.getLogger(Socials.class.getName()).log(Level.INFO, webex.getMessage());
        } finally {
            Logger.getLogger(Socials.class.getName()).log(Level.INFO, "Message delivered by {0} to {1}", new Object[]{tweet.getSenderScreenName(), tweet.getRecipientScreenName()});
        }
    }

    /**
     *
     * @param userSender
     * @param userReceiver
     * @param receiverPhone
     * @param message
     */
    public void sendSMSMessage(String userSender, String userReceiver, String receiverPhone, String message) {
        ResourceBundle waziupNotificationBundle = ResourceBundle.getBundle("parameters");
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        RestAPI api = new RestAPI(waziupNotificationBundle.getString("api_key"), waziupNotificationBundle.getString("api_token"), "v1");
        params.put("src", waziupNotificationBundle.getString("telephonesrc"));
        params.put("dst", receiverPhone);
        params.put("text", message);
        try {
            MessageResponse msgResponse = api.sendMessage(params);
            //Persist the notification
            Document notificationsms = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone)
                    .append("channel", "SMS")
                    .append("message", message)
                    .append("status", "Delivered")
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(notificationsms);
        } catch (PlivoException ex) {
            Document smsnotificationfailure = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone)
                    .append("channel", "SMS")
                    .append("message", message)
                    .append("status", "Not delivered")
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(smsnotificationfailure);
            Logger.getLogger(org.waziup.socialbackend.core.Socials.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param userSender
     * @param userReceiver
     * @param receiverPhone
     * @param message
     */
    public void sendWhatsAppMessage(String userSender, String userReceiver, String receiverPhone, String message) {
        ResourceBundle notifBundle = ResourceBundle.getBundle("parameters");
        Document doc = new Document();
        doc.put("number", receiverPhone);
        doc.put("message", message);
        String jsonPayLoad = doc.toJson();
        try {
            URL url = new URL(notifBundle.getString("WA_GATEWAY_URL_INCOMPLETE") + notifBundle.getString("INSTANCE_ID"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-WM-CLIENT-ID", notifBundle.getString("CLIENT_ID"));
            conn.setRequestProperty("X-WM-CLIENT-SECRET", notifBundle.getString("CLIENT_SECRET"));
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(jsonPayLoad.getBytes());
            os.flush();
            os.close();
            statusCode = conn.getResponseCode();
            conn.disconnect();

            //Persist the notification
            Document whatsappDoc = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone)
                    .append("channel", "whatsapp")
                    .append("message", message)
                    .append("status", "Delivered")
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(whatsappDoc);
        } catch (Exception ex) {
            Document whatsappDocErr = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone)
                    .append("channel", "whatsapp")
                    .append("message", message)
                    .append("status", "Not delivered")
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(whatsappDocErr);
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, null, statusCode);
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Logger.getLogger(Socials.class.getName()).log(Level.INFO, null, statusCode);
        }

    }

}
