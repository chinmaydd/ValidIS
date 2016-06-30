(ns validis.queries.network
  (:require [monger.collection  :as mc]
            [validis.db-handler :refer [db]]
            [monger.conversion  :refer [from-db-object]]
            [monger.operators   :refer :all])
  (:import org.bson.types.ObjectId))

(defn create-new-network
  "Creates a new empty network (document) in the database"
  [network-data]
  (mc/insert-and-return db "networks" network-data))

(defn check-if-owned-network?
  "Checks if the user id provided matches with the owner user id of the network. Returns 0 if true, 1 otherwise."
  ;; Bug in monger. Need to fix!
  [network-data]
  (if (= (str (:owner-id (mc/find-one-as-map db "networks" {:_id (ObjectId. (:network-id network-data))} [:owner-id]))) (:owner-id network-data))
  ;; Return 0 if true, 1 otherwise.
  0
  1))

(defn delete-network
  "Deletes a network (document) from the database"
  [network-data]
  (.getN (mc/remove-by-id db "networks" (ObjectId. (:network-id network-data)))))

(defn get-network-by-id
  "Returns a network (document) from the database based on the id provided"
  [network-data]
  (mc/find-one-as-map db "networks" {:_id (ObjectId. (:network-id network-data))}))

(defn update-network
 "Updates a network (document) in the database based on the data provided"
 [network-data]
  (mc/update-by-id db "networks" {:_id (ObjectId. (:network-id network-data))} {$set network-data}))
