;; src/route-functions/network/get-network-information.clj
(ns validis.route-functions.network.get-network-information
  "The real deal."
  (:require [validis.queries.network :as n-query]
            [validis.queries.cis :as c-query]
            [clj-http.client :as client]
            [ring.util.http-response :as respond]))

(defn get-network-information
  "Gets all the information we need."
  [network-id]
  (let [cis-list          (n-query/get-all-cis {:network-id network-id})
        url-list          (c-query/get-all-urls {:cis-list cis-list})
        ;; Add authentication for the server {:basic-auth ["user" "pass"]}
        get-all-responses (map #(client/get %) url-list)
        ;; Temporary :status instead of data!
        get-all-data      (map #(get % :body) get-all-responses)
        _ (println get-all-data)]
    (respond/ok {:message "OLAA"})))
