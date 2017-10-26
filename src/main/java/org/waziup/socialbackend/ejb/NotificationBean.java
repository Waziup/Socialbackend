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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
     */
    public List<Document> queryNotification() {
        List<Document> listeDocument = new ArrayList<>();
        for (Document doc : collection.find()) {
            listeDocument.add(doc);
        }
        return listeDocument;
    }

    /**
     *
     * @param doc
     */
    public void createNotification(Document doc) {
        collection.insertOne(doc);
    }

    /**
     *
     * @param doc
     */
    public void deleteOneNotification(String id) {
        collection.deleteOne(eq("_id", new ObjectId(id)));
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
            System.out.println("Impossible to delete document");
        }
    }


}
