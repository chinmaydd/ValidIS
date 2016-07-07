;; src/route-functions/network/create-network.clj
(ns validis.route-functions.network.create-network
  (:require [validis.queries.network    :as query]
            [buddy.hashers              :as hashers]
            [ring.util.http-response    :as respond]
            [monger.util                :refer [object-id]]))

(defn create-network
  "Create a network with `name`, `location` and `owner-id`."
  [name location owner-id]
  (let [new-network (query/create-new-network {:name name
                                               :location location
                                               :owner-id (object-id owner-id)
                                               :CIS-list []})]
    (respond/created {:name name :id (str (:_id new-network))})))

(defn create-network-response
  "Generates a response on creation of a network. `owner-id` is assigned through the id present in the `:identity` field in the request."
  [request name location]
  (let [owner-id        (get-in request [:identity :id])
        network-query   (query/get-network-by-name {:name name})
        network-exists? (not-empty network-query)]
    (if network-exists?
      (respond/conflict {:error "Network already exists!"})
      (create-network name location owner-id))))
