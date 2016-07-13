;; src/route-functions/network/add_cis.clj
(ns validis.route-functions.network.add-cis
  "Contains function to add a new CIS to a network document."
  (:require [validis.queries.cis :as c-query]
            [validis.queries.network :as n-query]
            [ring.util.http-response :as respond]))

(defn add-cis-to-network
  "Adds CIS to network given `cis-id` and `network-id`. Response is a message on successful addition."
  [network-id cis-id]
  (let [updated-network (n-query/add-cis-to-network {:cis-id cis-id
                                                     :network-id network-id})]
    (respond/ok {:message "CIS was successfully added."})))

(defn add-cis-response
  "Generates response on addition of a CIS to a network. If the CIS does not exist, we return a not-found response."
  [network-id cis-id]
  (let [cis-query       (c-query/get-cis-by-id {:cis-id cis-id})
        cis-exists?     (not-empty cis-query)]
    (cond
      cis-exists? (add-cis-to-network network-id cis-id)
      :else       (respond/not-found {:error "CIS does not exist! Please add it to the db first"}))))
