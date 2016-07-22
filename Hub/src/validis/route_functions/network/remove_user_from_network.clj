;; src/route-functions/network/remove_user_from_network.clj
(ns validis.route-functions.network.remove-user-from-network
  "Contains functions to remove a cis from a network."
  (:require [ring.util.http-response :as respond]
            [validis.queries.network :as n-query]
            [validis.queries.user :as u-query]))

(defn remove-user-from-network
  "Removes a user from a network!"
  [network-id user-id]
  (let [remove-user-query (n-query/remove-user-from-network {:network-id network-id :user-id user-id})]
    (respond/ok {:message "User was successfully removed from the shared users' list."})))

(defn remove-user-from-network-response
  "Generates a response on removal of a user from a shared list of users for a network. We first check if the user we are trying to removis present in the hared-user-list`. If not, we then return a not-found response."
  [network-id user-id]
  (let [user-query        (u-query/get-user-by-id {:id user-id})
        user-exists?      (not-empty user-query)
        shared-with-user? (n-query/check-if-shared? {:network-id network-id :user-id user-id})]
    (cond
      (not user-exists?)        (respond/bad-request {:error "The user you are trying to remove does not exist!"})
      (not shared-with-user?)   (respond/bad-request {:error "The user you are trying to remove is not in the shared users list!"})
      :else                     (remove-user-from-network network-id user-id))))
