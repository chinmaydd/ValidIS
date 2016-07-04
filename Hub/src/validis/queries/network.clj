;; src/queries/network.clj
(ns validis.queries.network
  (:require [monger.collection  :as mc]
            [validis.db-handler :refer [db]]
            [monger.conversion  :refer [from-db-object]]
            [monger.operators   :refer :all]
            [monger.util        :refer [object-id]])
  (:import org.bson.types.ObjectId))

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create a new network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-new-network
  "Creates a new network in the database.
  Network data is of the form:
  {:name :location :owner-id}
  "
  [network-data]
  (mc/insert-and-return db "networks" network-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Updation queries for Network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-network
 "Updates a network in the database.
 Network data is of the form:
 {:name :location :owner-id :network-id}
 "
 [network-data]
  (let [id (object-id (:network-id network-data))]
  (mc/update-by-id db "networks" {:_id id} {$set network-data})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Deletion queries for Network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; HACK: We need to invoke `.getN` since the only way we can check if the document was deleted is to check the number of updated/inserted/deleted documents. We can directly return this value.
(defn delete-network
  "Deletes a network from the database.
  Network data is of the form:
  {:name :location :owner-id :network-id}
  "
  [network-data]
  (let [id (object-id (:network-id network-data))]
  (.getN (mc/remove-by-id db "networks" id))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Getter functions for Network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-network-by-id
  "Returns a network with the id, if exists.
  Network data is of the form:
  {:network-id}
  "
  [network-data]
  (let [id (object-id (:network-id network-data))]
  (mc/find-one-as-map db "networks" {:_id id})))

(defn get-network-by-name
  "Returns a network with the name, if exists.
  Network data is of the form:
  {:name}
  "
  [network-data]
  (let [name (:name network-data)]
    (mc/find-one-as-map db "networks" {:name name})))

(defn check-if-owned-network?
  "Checks if the user-id provided matches with the owner's user-id of the network.
  Returns 0 if true, 1 otherwise.
  Network data is of the form:
  {:network-id :owner-id}
  "
  ;; Bug in monger. Need to fix!
  [network-data]
  (let [network-id (object-id (:network-id network-data))
        owner-id   (:owner-id (network-data))]
  (if (= (str (:owner-id (mc/find-one-as-map db "networks" {:_id network-id} [:owner-id]))) owner-id)
  0
  1)))
