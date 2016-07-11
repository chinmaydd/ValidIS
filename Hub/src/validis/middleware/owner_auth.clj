;; src/middleware/owner_auth.clj
(ns validis.middleware.owner-auth
  (:require [ring.util.http-response :refer [unauthorized not-found]]
            [validis.queries.network :as query]))

(defn owner-auth-mw
  "Middleware used to check if the request made for changing the network properties is done by the user who owns the network."
  [handler]
  (fn [request]
    (let [network-id      (get-in request [:params :network-id])
          network-query   (query/get-network-by-id {:network-id network-id})
          network-exists? (not-empty network-query)
          owner-id        (get-in request [:identity :id])
          owner?          (query/check-if-owned-network? {:owner-id owner-id
                                                     :network-id network-id})
          _ (println owner?)]
      (cond
        (and network-exists? owner?) (handler request)
        (not network-exists?)        (not-found {:error "Network not found!"})
        (not owner?)                 (unauthorized {:error "Not authorized"})))))
