(ns validis.queries.user
  (:require [monger.collection  :as    mc]
            [validis.db-handler :refer [db]]
            [monger.conversion  :refer [from-db-object]]
            [monger.operators   :refer :all])
  (:import org.bson.types.ObjectId))

(defn insert-registered-user!
  "Inserts a registered user data(document) into the database"
  [user-data]
  (mc/insert-and-return db "users" user-data))

;; We need to invoke getN since the only way we can check if the document was deleted is to check the number of updated/inserted/deleted documents. We can directly return this value.
(defn delete-registered-user!
  "Deletes a user from the database with the id provided"
  [user-data]
  (.getN (mc/remove-by-id db "users" (ObjectId. (:id user-data)))))

(defn update-registered-user!
  "Updates a user document in the database based on the information provided"
  [user-data]
  ;; Need to check if there are any database errors due to this fact.
  (mc/update-by-id db "users" {:_id (ObjectId. (:id user-data))} {$set user-data}))

(defn get-registered-user-by-id
  "Returns a registered user with the id provided"
  [user-data]
  (mc/find-one-as-map db "users" {:_id (ObjectId. (:id user-data))}))

(defn get-registered-user-details-by-username
  "Returns a registered user with the details provided"
  [user-data]
  (mc/find-one-as-map db "users" user-data))

(defn get-registered-user-details-by-email
  "Returns a registered user with the details provided"
  [user-data]
  (mc/find-one-as-map db "users" user-data))

(defn update-registered-user-refresh-token!
  "Updates the refresh token for the registered user"
  [user-data]
  (mc/update db "users" {:_id (ObjectId. (:id user-data))} {$set {:refresh_token (:refresh_token user-data)}}))
