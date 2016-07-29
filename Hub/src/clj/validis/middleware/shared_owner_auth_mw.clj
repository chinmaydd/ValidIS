;; src/validis/middleware/shared_owner_auth_mw.clj
(ns validis.middleware.shared-owner-auth-mw
  "Contains middleware to check if the request made to check for network data is made by a shared (network) user"
  (:require [ring.util.http-response :refer [unauthorized not-found]]
            [validis.queries.network :as query]))

(defn shared-owner-auth-mw
  "Middleware used to check if the request made for accessing the network information is done by a user who is in the `shared-users-list` of the network. This is specifically for the `/api/network` context."
  [handler]
  (fn [request]
    (let [network-id      (get-in request [:params :network-id])
          network-query   (query/get-network-by-id {:network-id network-id})
          network-exists? (not-empty network-query)
          user-id         (get-in request [:identity :id])
          shared-user?    (query/check-if-shared? {:network-id network-id :user-id user-id})]
      (cond
        (and network-exists? shared-user?) (handler request)
        (not network-exists?)              (not-found {:error "Network not found!"})
        (not shared-user?)                 (unauthorized {:error "Not authorized"})))))

