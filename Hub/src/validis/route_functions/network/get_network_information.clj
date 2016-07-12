;; src/route-functions/network/get-network-information.clj
(ns validis.route-functions.network.get-network-information
  (:require [validis.queries.network :as n-query]
            [validis.queries.cis     :as c-query]
            [clj-http.client         :as client]))

(defn get-network-information
  "Gets all the URLs we need to visit for the CISs."
  [network-id]
  (let [cis-list (n-query/get-all-cis {:network-id network-id})
        url-list (c-query/get-all-urls {:cis-list cis-list})
        get-all-responses (get (map #(client/get % {:accept :json :basic-auth ["user" "pass"]})) :data)]
    (println get-all-responses)))

