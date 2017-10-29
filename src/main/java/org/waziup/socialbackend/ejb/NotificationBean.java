/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.waziup.socialbackend.ejb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import com.restfb.types.Notification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Constantin Drabo
 */
@Stateless
public class NotificationBean {

    MongoClient mongoclient;
    List<Notification> notificationList;
    MongoDatabase db;
    MongoCollection<Document> collection;

    @PostConstruct
    private void init() {
        ResourceBundle waziupNotificationBundle = ResourceBundle.getBundle("parameters");
        MongoClientURI mongoCloudURI = new MongoClientURI(waziupNotificationBundle.getString("mongoclouduri"));
        MongoClient mongoCloudClient = new MongoClient(mongoCloudURI);
        MongoDatabase databasecloud = mongoCloudClient.getDatabase(waziupNotificationBundle.getString("mongoclouddatabase"));
        collection = databasecloud.getCollection(waziupNotificationBundle.getString("mongocloudcollection"));
    }

    /**
     *
     * @return
     *
     */
    public List<Document> queryNotification() {
        //List all the notifications stored
        List<Document> listeDocument = new ArrayList<>();
        if (collection.count() != 0) {
            for (Document doc : collection.find()) {
                Document d = new Document();
                d.put("_id", doc.get("_id").toString());
                d.put("user_id", doc.getString("user_id"));
                d.put("channel", doc.getString("channel"));
                d.put("message", doc.getString("message"));
                d.put("username", doc.getString("username"));
                d.put("status", doc.getString("status"));
                d.put("inserttime", doc.getString("inserttime"));
                listeDocument.add(d);
            }
            Logger.getLogger("NotificationBeans").log(Level.INFO, "Notifications queried at " + LocalDateTime.now());
        } else {
            Logger.getLogger("NotificationBeans").log(Level.SEVERE, "No notification stored - " + LocalDateTime.now());
        }
        return listeDocument;
    }

    /**
     *
     * @param id
     * @return
     */
    public Document queryANotification(String id) {
        Logger.getLogger("NotificationBeans").log(Level.INFO, "Notifications  " + id + "queried at " + LocalDateTime.now());
        Document doc  = collection.find(eq("_id", new ObjectId(id))).first() ;
        Document d = new Document();
        d.put("_id", doc.get("_id").toString());
        d.put("user_id",doc.getString("user_id"));
        d.put("channel", doc.getString("channel"));
        d.put("message", doc.getString("message"));
        d.put("username", doc.getString("username"));
        d.put("status", doc.getString("status"));
        d.put("inserttime", doc.getString("inserttime"));
        return d;
    }

    /**
     *
     * @param doc
     *
     */
    public void createNotification(Document doc) {
        //Persist the information to the database
        collection.insertOne(doc);
    }

    /**
     *
     * @param doc
     */
    public void deleteOneNotification(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
        Logger.getLogger("NotificationBeans").log(Level.INFO, "Notifications  " + id + "deleted at " + LocalDateTime.now());
    }

    /**
     * Delete All notifications from database
     */
    public void deleteAllNotification() {
        if (collection.count() != 0) {
            for (Document doc : collection.find()) {
                collection.deleteOne(doc);
            }
        } else {
            Logger.getLogger("NotificationBeans").log(Level.SEVERE, "No notification stored - " + LocalDateTime.now());
        }
    }

}
