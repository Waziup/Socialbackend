
package org.waziup.socialbackend;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.PhoneMessageRecipient;
import com.restfb.types.send.SendResponse;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Path("socials")
public class Socials implements Serializable {

    ConfigurationBuilder config;
    TwitterFactory tf;
    Twitter twitter;
    DirectMessage tweet;
//  String api_key = "MAMJC2ZDI2NZUWMDK1NW";
  String api_key= "MAMDA5ZDJIMDM1NZVMZD";
//String api_token = "NDZlZWJlMmQ2MjkyNjA1M2UyODgwODllYjBhNDNh";
String api_token = "NzRlNWJiNmU2MmFjYWJlODhlNTk3MTkyZGEzNzIy";
RestAPI api = new RestAPI(api_key, api_token, "v1");
    

    /**
     * Constructor
     */
    public Socials() {
        config = new ConfigurationBuilder();
        config.setDebugEnabled(true)
                .setOAuthConsumerKey("2jZQWTn50FKzQ5L7dW6srEy3b")
                .setOAuthConsumerSecret("y8IHmnoDv3fXk55bhZvTj9i5wrOzrK5rm7lThSXi92w1BBe8b4")
                .setOAuthAccessToken("1899407767-IWV6LNohrUxVMqnSEKHQx7TtiAc6fCnMkSjGxAb")
                .setOAuthAccessTokenSecret("cyRW7MjIfEcrrb7aRq8BPgMbDvMUA5lYSHMIIKvx9LSRM");
        tf = new TwitterFactory(config.build());
        twitter = tf.getInstance();
    }

    @POST
    public void sendMessage(@FormParam("user_id") String user_id, @FormParam("channel") String channel, @FormParam("message") String message) {

        switch (channel) {
            case "facebook":
                sendFacebookMessage(user_id, message);
                break;
            case "twitter":
                sendTwitterMessage(user_id, message);
                break;
            case "sms":
                sendSMSMessage(user_id,message);
            default:
                System.out.println("choose a channel please");
               // sendTwitterMessage(user_id, message);
        }

    }

    @GET
    public String getMessageHistory() {
        return null;
    }

    @DELETE
    public void deleteMessage() {

    }

    /**
     *
     * @param messageReceiver
     * @param message
     */
    public void sendFacebookMessage(String messageReceiver, String message) {
        
        PhoneMessageRecipient recipient = new PhoneMessageRecipient(messageReceiver);
      //  IdMessageRecipient recipient = new IdMessageRecipient(messageReceiver);
       // message = new Message("Just a simple text");
       
String pageAccessToken = "EAACEdEose0cBAHam3rEV7nHbir6kOoRQiNoBDWbDZBQiBQsOetzMgruyVQ9HsKleu1gU49UDAXlB1VWnXaR4xptBBT510XwMZB5IXTGAinXrHo2W75vJXzJ04ayTxiU5vtsA4zYZCgDvtCtZC2kLwOfnqBiQNZAjlDeqgQOZCkSHT2ObaBBe4X7I3I4p5Msu1KZC6DvQOD3aXzLa9iO4qZCNyRN5c7ijRmZAZAIvekO7h58QZDZD";
//pageAccessToken=pageAccessToken.substring(13,pageAccessToken.lastIndexOf("&"));
FacebookClient pageClient = new DefaultFacebookClient(pageAccessToken, Version.VERSION_2_10);

/*SendResponse resp =*/ pageClient.publish("me/messages", SendResponse.class,
     Parameter.with("recipient", recipient), // the id or phone recipient
	 Parameter.with("message", message)); // one of the messages from above
//System.out.println("message envoyé à"+recipient);
    }

    /**
     *
     * @param messageReceiver
     * @param message
     */
    public void sendTwitterMessage(String messageReceiver, String message) {
        try {
            tweet = twitter.sendDirectMessage(messageReceiver, message);
        } catch (TwitterException ex) {
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, tweet.getId() + " did not deliver", ex);
        } finally {
            Logger.getLogger(Socials.class.getName()).log(Level.INFO, "Message delivered by {0} to {1}", new Object[]{tweet.getSenderScreenName(), tweet.getRecipientScreenName()});

        }
    }
    
    public  void sendSMSMessage(String messageReceiver, String message) {
        LinkedHashMap<String, String> params = new LinkedHashMap<> ();
params.put("src","+393806412092");

params.put("dst",messageReceiver);

params.put("text",message);
        try {
            //PlivoMessage message = api.sendMessage(params);
            MessageResponse msgResponse = api.sendMessage(params);
        } catch (PlivoException ex) {
            Logger.getLogger(Socials.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }

}
