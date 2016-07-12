;; src/route-functions/network/delete-network.clj
(ns validis.route-functions.network.delete-network
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond]))

(defn delete-network
  "Delete a network by its id."
  [network-id]
  (query/delete-network {:id network-id}))


(defn delete-network-response
  "Generates a response on deletion of a network."
  [network-id]
  (let [deleted-network (delete-network network-id)]
    (if not= 0 deleted-network)
      (respond/ok        {:message (format "Network with id %s removed successfully!" network-id)})
      (respond/not-found {:error "Network does not exist."})))
