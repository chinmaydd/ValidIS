;; src/queries/network.clj
(ns validis.queries.network
  "Queries for the network document store."
  (:require [monger.collection :as mc]
            [validis.db-handler :refer [db]]
            [monger.conversion :refer [from-db-object]]
            [monger.operators :refer :all]
            [monger.util :refer [object-id]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Create a new network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-new-network
  "Creates a new network in the database.
  Network data is of the form:
  `{:name :location :owner-id}`
  "
  [network-data]
  (mc/insert-and-return db "networks" network-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Updation queries for Network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-network
  "Updates a network in the database.
  Network data is of the form:
  `{:name :location :owner-id :network-id}`
  "
  [network-data]
  (let [network-id (object-id (:network-id network-data))]
    (mc/update-by-id db "networks" network-id {$set network-data})))

(defn add-cis-to-network
  "Adds a CIS to a given network
  Network data is of the form:
  `{:cis-id :network-id}`
  "
  [network-data]
  (let [network-id (object-id (:network-id network-data))
        cis-id (:cis-id network-data)]
    (mc/update-by-id db "networks" network-id {$addToSet {:CIS-list cis-id}})))

(defn remove-cis-from-network
  "Removes a CIS from a network
  Network data is of the form:
  `{:cis-id :network-id}`
  "
  [network-data]
  (let [network-id (object-id (:network-id network-data))
        cis-id (:cis-id network-data)]
    (mc/update-by-id db "networks" network-id {$pull {:CIS-list cis-id}})))

(defn add-user-to-network
  "Adds a user to a network, i.e shares the network with the new user
  Network data is of the form:
  `{:network-id :user-id}`
  "
  [network-data]
  (let [network-id (object-id (:network-id network-data))
        user-id    (:user-id network-data)]
    (mc/update-by-id db "networks" network-id {$addToSet {:shared-user-list user-id}})))

(defn remove-user-from-network
  "Removes a user from a shared list of users for a network
  Network data is of the form:
  `{:network-id :user-id}`
  "
  [network-data]
  (let [network-id (object-id (:network-id network-data))
        user-id (:user-id network-data)]
    (mc/update-by-id db "networks" network-id {$pull {:shared-user-list user-id}})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Deletion queries for Network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; HACK: We need to invoke `.getN` since the only way we can check if the document was deleted is to check the number of updated/inserted/deleted documents. We can directly return this value.
(defn delete-network
  "Deletes a network from the database.
  Network data is of the form:
  `{:name :location :owner-id :network-id}`
  "
  [network-data]
  (let [id (object-id (:network-id network-data))]
    (.getN (mc/remove-by-id db "networks" id))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Getter functions for Network ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn list-networks-for-user
  "Returns a list of networks belonging to a particular user.
  network-data is of the form:
  `{:network-id :owner-id}`
  "
  [network-data]
  (let [owner-id (:owner-id network-data)]
    (vec (mc/find-maps db "networks" {:owner-id owner-id}))))

(defn get-network-by-id
  "Returns a network with the id, if exists.
  Network data is of the form:
  `{:network-id}`
  "
  [network-data]
  (let [id (object-id (:network-id network-data))]
    (mc/find-one-as-map db "networks" {:_id id})))

(defn get-network-by-name
  "Returns a network with the name, if exists.
  Network data is of the form:
  `{:name}`
  "
  [network-data]
  (let [name (:name network-data)]
    (mc/find-one-as-map db "networks" {:name name})))

(defn check-if-owned-network?
  "Checks if the user-id provided matches with the owner's user-id of the network.
  Returns 0 if true, 1 otherwise.
  Network data is of the form:
  `{:network-id :owner-id}`
  "
  [network-data]
  (let [network-id (object-id (:network-id network-data))
        owner-id   (:owner-id network-data)]
    (if (= (str (:owner-id (mc/find-one-as-map db "networks" {:_id network-id} [:owner-id]))) owner-id)
      true
      false)))

(defn check-if-shared?
  "Checks if the user is present in the shared-user-list for a network!
  Network data is of the form:
  `{:network-id :user-id}`
  "
  [network-data]
  (let [network-id (object-id (:network-id network-data))
        user-id (:user-id network-data)]
    (if (not-empty (mc/find-one-as-map db "networks" {$and [{:_id network-id} {:shared-user-list {$in [user-id]}}]}))
      true
      false)))

(defn get-all-cis
  "Return a list of all CIS in the network.
  Network data is of the form:
  `{:network-id}`"
  [network-data]
  (let [network-id (object-id (:network-id network-data))]
    (get (mc/find-one-as-map db "networks" {:_id network-id} [:CIS-list]) :CIS-list)))
