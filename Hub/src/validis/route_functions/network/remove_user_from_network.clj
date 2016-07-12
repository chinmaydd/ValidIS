;; src/route-functions/network/remove_user_from_network.clj
(ns validis.route-functions.network.remove-user-from-network
  (:require [ring.util.http-response :as respond]
            [validis.queries.network :as query]))

(defn remove-user-from-network
  "Removes a user from a network!"
  [network-id user-id]
  (let [remove-user-query (query/remove-user-from-network {:network-id network-id :user-id user-id})]
    (respond/ok {:message "User was successfully removed from the shared users' list."})))

(defn remove-user-from-network-response
  "Generates a response on removal of a user from a shared list of users for a network. We first check if the user we are trying to removis present in the hared-user-list`. If not, we then return a not-found response."
  [network-id user-id]
  (let [shared-with-user? (query/check-if-shared? {:network-id network-id :user-id user-id})]
    (if shared-with-user?
      (remove-user-from-network network-id user-id)
      (respond/not-found {:error "The user you are trying to remove is not in the shared users' list"}))))
