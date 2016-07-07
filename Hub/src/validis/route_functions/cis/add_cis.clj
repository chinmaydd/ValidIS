;; src/route-functions/cis/add_cis.clj
(ns validis.route-functions.cis.add-cis
  (:require [validis.queries.cis     :as c-query]
            [validis.queries.network :as n-query]
            [ring.util.http-response :as respond]))

(defn add-cis-to-network
  "Adds CIS to network given `cis-id` and `network-id`"
  [network-id cis-id]
  (let [updated-network (n-query/add-cis-to-network {:cis-id cis-id
                                                     :network-id network-id})]
      (respond/ok {:message "CIS was successfully added."})))
    
(defn add-cis-response
  "Generates response on addition of a CIS to a network."
  [request network-id cis-id]
  (let [owner-id        (get-in request [:identity :id])
        cis-query       (c-query/get-cis-by-id {:cis-id cis-id})
        network-query   (n-query/get-network-by-id {:network-id network-id})
        owner?          (n-query/check-if-owned-network? {:network-id network-id 
                                                          :owner-id owner-id})
        network-exists? (not-empty network-query)
        cis-exists?     (not-empty cis-query)]
    (cond
          (and owner? network-exists? cis-exists?) (add-cis-to-network network-id cis-id)
          (not owner?)                             (respond/unauthorized {:error "Not authorized"})
          (not network-exists?)                    (respond/bad-request {:error "Network does not exist!"})
          (not cis-exists?)                        (respond/bad-request {:error "CIS does not exist! Please add it to the db first"}))))
