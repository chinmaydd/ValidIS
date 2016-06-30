(ns validis.queries.network
  (:require [monger.collection  :as mc]
            [validis.db-handler :refer [db]]
            [monger.conversion  :refer [from-db-object]]
            [monger.operators   :refer :all])
  (:import org.bson.types.ObjectId))

(defn create-new-network!
  "Creates a new empty network (document) in the database"
  [network-data]
  (mc/insert-and-return db "networks" network-data))

(defn check-if-owned-network?
  "Checks if the user id provided matches with the owner user id of the network"
  ;; Bug in monger. Need to fix!
  [network-data]
  (if (= (str (:owner_id (mc/find-one-as-map db "networks" {:_id (ObjectId. (:id network-data))} [:owner_id]))) (:owner_id network-data))
  0
  1))

(defn delete-network!
  "Deletes a network (document) from the database"
  [network-data]
  (.getN (mc/remove-by-id db "networks" (ObjectId. (:id network-data)))))
