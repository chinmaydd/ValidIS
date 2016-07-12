;; src/route-functions/network/add_user_to_network.clj
(ns validis.route-functions.network.add-user-to-network
  (:require [validis.queries.user    :as u-query]
            [validis.queries.network :as n-query]
            [ring.util.http-response :as respond]))

(defn add-user-to-network
  "Adds a user to the list of shared users. Each network has a `shared-user-list` consisting of a list of User ids the owner wants to share the network data statistics with. Response is a message if the operation was successful."
  [network-id user-id]
  (let [add-user-query (n-query/add-user-to-network {:network-id network-id :user-id user-id})]
    (respond/ok {:message "User was successfully added!"})))

(defn add-user-to-network-response
  "Generates a response on addition of a user to a network (sharing). Checks if the user we are trying to share the network with already exists in the database. If not, we return a not-found response."
  [network-id user-id]
  (let [shared-user   (u-query/get-user-by-id {:id user-id})
        user-exists?  (not-empty shared-user)]
    (if user-exists?
      (add-user-to-network network-id user-id)
      (respond/not-found {:error "User not found!"}))))
