package org.waziup.socialbackend.core;

import com.plivo.api.Plivo;
import com.plivo.api.exceptions.PlivoRestException;
import com.plivo.api.models.call.Call;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
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
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.GraphResponse;
import org.bson.Document;
import org.waziup.socialbackend.ejb.NotificationBean;
import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import com.plivo.api.models.message.MessageCreateResponse;
import java.util.Collections;

/**
 *
 * @author Drabo Constantin
 *
 */
@Model
@Path("socials")
public class Socials implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
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
        config.setDebugEnabled(true).setOAuthConsumerKey(waziupNotificationBundle.getString("OAuthConsumerKey"))
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
        final Response response;
        if (doc != null) {
            channel = doc.getString("channel");
            switch (channel) {
                case "twitter":
                    response = sendTwitterMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                    break;
                case "sms":
                    response = sendSMSMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                    break;
                case "voice":
                    response = sendVoiceMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                    break;
                //case "whatsapp":
                //    response = sendWhatsAppMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
                //    break;
                default:
                    response = sendTwitterMessage(doc.getString("userSender"), doc.getString("userReceiver"), doc.getString("user_id"), doc.getString("message"));
            }

        } else {
            response = Response.status(400).entity("Unrecognized channel. Supported channels are twitter, sms and voice").build();
        }
        return response;
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
     * @param id
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
     * @param id
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
    public void sendFacebookMessage(/*String userSender, String userReceiver, String receiverProfile,*/String message) {

        String pageAccessToken = "EAAXh1hmHeq4BAIekZAddN3lvuKPzRCERfNmdT9B4oKJ8vC0OZCGl2a1GYty8nZB5ZBifhbBbbK3yNrqzgv0YTk3BDyQK9HjLnMFiZA4hJKdIGVgo9HHZC9VH0ZAO9rEx5zE7GmJZASoGZAlj1rnuuFN6qf6sZAqvSwqZC1Xo4JrOQyvnQZDZD";
        FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_2_6);
        pageClient.publish("me/feed", GraphResponse.class,
                Parameter.with("message", message));

    }

    /**
     *
     * @param userSender
     * @param userReceiver
     * @param receiverProfile
     * @param message
     */
    public Response sendTwitterMessage(String userSender, String userReceiver, String receiverProfile, String message) {

        Response.ResponseBuilder response;
        try {
            Logger.getLogger(Socials.class.getName()).log(Level.INFO, "Sending twitter to {0} from {1} with message: {2}", new Object[]{receiverProfile, twitter.users().verifyCredentials().getName(), message});
            tweet = twitter.sendDirectMessage(receiverProfile, message);
            Document notification = new Document("userSender", userSender).append("userReceiver", userReceiver)
                    .append("user_id", receiverProfile).append("channel", "SMS").append("message", message)
                    .append("status", "Delivered").append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(notification);
            response = Response.ok(notification);

        } catch (TwitterException ex) {
            Document notificationfailure = new Document("userSender", userSender).append("userReceiver", userReceiver)
                    .append("user_id", receiverProfile).append("channel", "SMS").append("message", message)
                    .append("status", "Not delivered").append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(notificationfailure);
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, "Tweet did not deliver", ex);
            response = Response.serverError();
            response.entity("Twitter not sent: " + ex);

        } catch (WebApplicationException webex) {
            Logger.getLogger(Socials.class.getName()).log(Level.INFO, webex.getMessage());
            response = Response.serverError();
            response.entity("Application error, Twitter not sent");
        }
        return response.build();
    }

    /**
     *
     * @param userSender
     * @param userReceiver
     * @param receiverPhone
     * @param message
     */
    public Response sendSMSMessage(String userSender, String userReceiver, String receiverPhone, String message) {
        ResourceBundle waziupNotificationBundle = ResourceBundle.getBundle("parameters");
        final Response.ResponseBuilder response;
        try {
            Plivo.init(waziupNotificationBundle.getString("api_key"), waziupNotificationBundle.getString("api_token"));
            MessageCreateResponse resp = com.plivo.api.models.message.Message.creator(waziupNotificationBundle.getString("telephonesrc"), Collections.singletonList(receiverPhone), message).create();
            //Persist the notification
            Document notificationsms = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone)
                    .append("channel", "SMS")
                    .append("message", message)
                    .append("status", resp.getMessage())
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(notificationsms);
            return Response.ok().build();
        } catch (PlivoRestException ex) {
            Document smsnotificationfailure = new Document("userSender", userSender)
                    .append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone)
                    .append("channel", "SMS")
                    .append("message", message)
                    .append("status", "Not delivered")
                    .append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(smsnotificationfailure);
            Logger.getLogger(org.waziup.socialbackend.core.Socials.class.getName()).log(Level.SEVERE, null, ex);
            response = Response.serverError();
            response.entity("SMS not sent: " + ex);
        } catch (IOException ex) {
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, null, ex);
            response = Response.serverError();
            response.entity("SMS not sent: " + ex);
        }
        return response.build();
    }

    /**
     *
     * @param userSender
     * @param userReceiver
     * @param receiverPhone
     * @param message
     */
    public Response sendVoiceMessage(String userSender, String userReceiver, String receiverPhone, String message) {
        ResourceBundle waziupNotificationBundle = ResourceBundle.getBundle("parameters");
        Plivo.init(waziupNotificationBundle.getString("api_key"), waziupNotificationBundle.getString("api_token"));
        try {

            String myURI = "http://callcenter-callcenter.a3c1.starter-us-west-1.openshiftapps.com/callback/" + message;
            System.out.println(myURI);
            Call.creator(waziupNotificationBundle.getString("telephonesrc"), Collections.singletonList(receiverPhone), myURI).answerMethod("GET").create();
            return Response.ok().build();
        } catch (IOException | PlivoRestException ex) {
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().entity("voice message not sent:" + ex).build();
        }
    }

    /**
     * @GET @Produces("application/xml")
     * @Path("/{answerxml}") public com.plivo.api.xml.Response
     * answerxml(@PathParam("answerxml") String answerxml) {
     * com.plivo.api.xml.Response response = new com.plivo.api.xml.Response()
     * .children( new Speak(answerxml) ); return response; } *
     */
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
            URL url = new URL(
                    notifBundle.getString("WA_GATEWAY_URL_INCOMPLETE") + notifBundle.getString("INSTANCE_ID"));
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

            // Persist the notification
            Document whatsappDoc = new Document("userSender", userSender).append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone).append("channel", "whatsapp").append("message", message)
                    .append("status", "Delivered").append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(whatsappDoc);
        } catch (IOException ex) {
            Document whatsappDocErr = new Document("userSender", userSender).append("userReceiver", userReceiver)
                    .append("user_id", receiverPhone).append("channel", "whatsapp").append("message", message)
                    .append("status", "Not delivered").append("insertTime", LocalDateTime.now().toString());
            notificationbean.createNotification(whatsappDocErr);
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, null, statusCode);
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Logger.getLogger(Socials.class.getName()).log(Level.INFO, null, statusCode);
        }

    }

}
