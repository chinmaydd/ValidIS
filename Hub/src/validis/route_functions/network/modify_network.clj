;; src/route-functions/network/modify-network.clj
(ns validis.route-functions.network.modify-network
  "Contains functions to modify network information."
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond]))

(defn modify-network
  "Modify network information. If the new information is not provided, we use the already existing one for updating the database document."
  [network-id name location owner-id]
  (let [current-network-info (query/get-network-by-id {:network-id network-id})
        new-name     (if (empty? name)     (str (:name current-network-info)) name)
        new-location (if (empty? location) (str (:location current-network-info)) location)
        new-network-info (query/update-network  {:network-id (str (:_id current-network-info))
                                                 :name name
                                                 :location location
                                                 :owner-id owner-id})]
    (respond/ok {:network-id (str (:_id current-network-info))})))

(defn modify-network-response
  "Generates a response on modification of a network"
  [request network-id name location]
  (let [owner-id        (get-in request [:identity :id])
        network-query   (query/get-network-by-name {:name name})
        network-exists? (not-empty network-query)]
    (cond
      network-exists? (respond/conflict {:error "Network with the same name already exists!"})
      :else           (modify-network network-id name location owner-id))))
