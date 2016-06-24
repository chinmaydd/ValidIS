(ns validis.queries.user
  (:require [monger.collection  :as    mc]
            [validis.db-handler :refer [db]]
            [monger.conversion  :refer [from-db-object]]
            [monger.operators   :refer :all])
  (:import org.bson.types.ObjectId))

(defn insert-registered-user!
  "Inserts a registered user data(document) into the database"
  [user_data]
  (mc/insert-and-return db "users" user_data))

(defn delete-registered-user!
  "Deletes a user from the database with the id provided"
  [user_data]
  (mc/remove-by-id db "users" (:id user_data))
  ;; Return value needed as 0. Needs to be fixed!
  0)

(defn update-registered-user!
  "Updates a user document in the database based on the information sent in the request."
  [user_data]
  ;; Need to check if there are any database errors due to this fact.
  (mc/update-by-id db "users" (ObjectId. (:id user_data)) user_data))

(defn get-registered-user-by-id
  [user_data]
  (mc/find-one-as-map db "users" {:_id (ObjectId. (:id user_data))}) )

(defn get-registered-user-details-by-username
  [user_data]
  (mc/find-one-as-map db "users" user_data))

(defn get-registered-user-details-by-email
  [user_data]
  (mc/find-one-as-map db "users" user_data))

(defn update-registered-user-refresh-token!
  [user_data]
  (mc/update db "users" {:_id (ObjectId. (:id user_data))} {$set {:refresh_token (:refresh_token user_data)}})
  )
