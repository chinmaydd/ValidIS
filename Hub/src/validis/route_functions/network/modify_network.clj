;; src/route-functions/network/modify-network.clj
(ns validis.route-functions.network.modify-network
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond])) 

(defn modify-network
  "Modify network information. If the new information is not provided, we use the already existing one for updating the database document."
  [network-id name location owner-id]
  (let [current-network-info (query/get-network-by-id {:id network-id})
        new-name     (if (empty? name)     (str (:name current-network-info)) name)
        new-location (if (empty? location) (str (:location current-network-info)) location)
        new-owner    (if (empty? owner-id) (str (:owner-id current-network-info)) owner-id)

        new-network-info (query/update-network  {:id (str (:_id current-network-info))
                                                 :name name
                                                 :location location
                                                 :owner-id owner-id})]
    (respond/ok {:id (str (:_id current-network-info)) :name name :location location})))

(defn modify-network-response
  "Generates a response on modification of a network"
  [network-id name location owner-id]
  (modify-network network-id name location owner-id))
