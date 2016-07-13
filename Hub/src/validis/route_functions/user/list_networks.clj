;; src/route-functions/user/list-networks.clj
(ns validis.route-functions.user.list-networks
  (:require [validis.queries.network :as query]
            [ring.util.http-response :as respond]))

(defn list-networks
  "Returns a list of networks belonging to a user along with their ids."
  [owner-id]
  (let [networks-list (query/list-networks-for-user {:owner-id owner-id})
        updated-network-id (map #(update-in % [:_id] str)
                                networks-list)]
    (respond/ok {:list updated-network-id})))

(defn list-network-response
  "Returns a response for on a request for a list of networks."
  [request]
  (let [owner-id (get-in request [:identity :id])]
    (list-networks owner-id)))
