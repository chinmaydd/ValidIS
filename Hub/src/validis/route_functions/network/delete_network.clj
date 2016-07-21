;; src/route-functions/network/delete-network.clj
(ns validis.route-functions.network.delete-network
  "Contains functions to delete a network."
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond]))

(defn delete-network
  "Delete a network by its id."
  [network-id]
  (query/delete-network {:network-id network-id}))

(defn delete-network-response
  "Generates a response on deletion of a network."
  [network-id]
  (let [deleted-network (delete-network network-id)]
    (respond/ok {:message (format "Network with id %s removed successfully!" network-id)})))
