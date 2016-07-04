;; src/route-functions/network/modify-network.clj
(ns validis.route-functions.network.modify-network
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond])
  (:import org.bson.types.ObjectId))

(defn modify-network
  "Modify network information."
  [current-network-info name location owner-id]
  (let [new-name     (if (empty? name)     (str (:name current-network-info)) name)
        new-location (if (empty? location) (str (:location current-network-info)) location)
        new-owner    (if (empty? owner-id) (str (:owner-id current-network-info)) owner-id)

        new-network-info (query/update-network  {:id (str (:_id current-network-info))
                                                 :name name
                                                 :location location
                                                 :owner-id owner-id})]
    (respond/ok {:id (str (:_id current-network-info)) :name name :location location})))

(defn modify-network-response
  "Generates a response on modification/updation of a network."
  [request network-id name location owner-id]
  (let [current-network-info   (query/get-network-by-id {:id network-id})
        modifying-own-network? (and
                                 ;; Authorization creds and token verification
                                 (= owner-id (get-in request [:identity :id]))
                                 (= 0 (query/check-if-owned-network? {:id network-id
                                                                      :owner-id owner-id})))]
    (if modifying-own-network?
      (modify-network current-network-info name location owner-id)
      (respond/unauthorized {:error "Not authorized"}))))
