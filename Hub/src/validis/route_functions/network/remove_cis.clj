;; src/route-functions/network/remove_cis.clj
(ns validis.route-functions.network.remove-cis
  "Contains functions to remove a cis from a network."
  (:require [validis.queries.cis :as c-query]
            [validis.queries.network :as n-query]
            [ring.util.http-response :as respond]))

(defn remove-cis-from-network
  "Removes a CIS from a network. Returns a message on successful operation."
  [network-id cis-id]
  (let [upated-network (n-query/remove-cis-from-network {:cis-id cis-id
                                                         :network-id network-id})]
    (respond/ok {:message "CIS was successfully removed."})))

(defn remove-cis-response
  "Generates a response on removal of CIS from a network. We first check if the CIS we are trying to remove exists. If it does not, we return a not-found response."
  [network-id cis-id]
  (let [cis-query               (c-query/get-cis-by-id {:cis-id cis-id})
        cis-exists?             (not-empty cis-query)
        cis-list                (:CIS-list (n-query/get-network-by-id {:network-id network-id}))
        cis-present-in-network? (.contains cis-list cis-id)]
    (cond
      (and cis-exists? cis-present-in-network?) (remove-cis-from-network network-id cis-id)
      (not cis-exists?)                         (respond/bad-request {:error "CIS you are trying to remove does not exist!"})
      (not cis-present-in-network?)             (respond/bad-request {:error "CIS you are trying to remove is not present in the network!"}))))
