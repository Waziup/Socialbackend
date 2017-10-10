/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waziup.socialbackend;

import com.restfb.FacebookClient;
import java.io.Serializable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author Drabo Constantin
 *
 */
@Path("facebook")
public class BackendSocialFacebook implements Serializable {

    FacebookClient facebookClient;

    public BackendSocialFacebook() {
    }

    @GET
    @Produces("application/json")
    public String getHtml() {
        return "Hello world";
    }

    @GET
    @Path("sendfb")
    @Produces({"application/json", "application/xml"})
    public void sendFacebookMessage() {

//        facebookClient = new DefaultFacebookClient("EAACEdEose0cBAIZAovHrc2XvDKOIQK7LyDZC0cAsbngQsHuZAZByFfuO6c2kHKm3ogtoPq7xykOEuUGyOgHPXoWh0LZChiHwKkT744GZCJJkJXabQr0bqlzV1esj4xt5vJZCZAl2R7xlSASH2KYMADwHmlhZBR3aeN3t8dFbK785l5iePBneuRNivsV9lZBlsAt1tPNqi1PvoQaQZDZD", Version.VERSION_2_10);
//        User user = facebookClient.fetchObject("me", User.class);
//
//        //Affiche la liste des amis
//        Connection<User> myFriends = facebookClient.fetchConnection("/me/friends", User.class);
//        List<User> friends = myFriends.getData();
//        Iterator<User> iter = friends.iterator();
//        while (iter.hasNext()) {
//            User myuser = iter.next();
//            System.out.println(myuser.getName() + " - " + myuser.getId());
//        }
//
//        System.out.println("============================================");
//
//        FacebookClient publicOnly = new DefaultFacebookClient("EAACEdEose0cBAIZAovHrc2XvDKOIQK7LyDZC0cAsbngQsHuZAZByFfuO6c2kHKm3ogtoPq7xykOEuUGyOgHPXoWh0LZChiHwKkT744GZCJJkJXabQr0bqlzV1esj4xt5vJZCZAl2R7xlSASH2KYMADwHmlhZBR3aeN3t8dFbK785l5iePBneuRNivsV9lZBlsAt1tPNqi1PvoQaQZDZD");
//        JsonObject toto = publicOnly.fetchObject("me", JsonObject.class);
//       // GraphResponse publishMessageResponse = facebookClient.publish("1929825243900077/feed", GraphResponse.class, Parameter.with("message", "Waziup social notifications test"));
//       
//        IdMessageRecipient recipient   =   new IdMessageRecipient("835217743207536"); 
//        Message simpleTextMessage = new Message("Just a simple text");
//
//        SendResponse resp = facebookClient.publish("me", SendResponse.class,Parameter.with("recipient", recipient), Parameter.with("message", "Hello"));
//       
       
    }



}
