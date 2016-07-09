;; src/route-functions/network/remove_cis.clj
(ns validis.route-functions.network.remove-cis
  (:require [validis.queries.cis :as c-query]
            [validis.queries.network :as n-query]
            [ring.util.http-response :as respond]))

(defn remove-cis-from-network
  "Removes a CIS from a network"
  [network-id cis-id]
  (let [upated-network (n-query/remove-cis-from-network {:cis-id cis-id
                                                         :network-id network-id})]
    (respond/ok {:message "CIS was successfully removed."})))

(defn remove-cis-response
  "Generates a response on removal of CIS from a network"
  [request network-id cis-id]
  (let [cis-query (c-query/get-cis-by-id {:cis-id cis-id})
        cis-exists? (not-empty cis-query)]
    (cond
      cis-exists? (remove-cis-from-network network-id cis-id)
      :else       (respond/not-found {:error "CIS you are trying to delete does not exist!"}))))
