;; src/route-functions/cis/get_all_cis.clj
(ns validis.route-functions.cis.get-all-cis
  (:require [validis.queries.cis     :as query]
            [ring.util.http-response :as respond]))

(defn get-all-cis
  "Returns a list of CIS with their ids."
  []
  (let [all-cis     (query/get-all-cis)
        updated-ids (map #(update-in % [:_id] str) all-cis)
        updated-ins (map #(update-in % [:inserter-id] str) updated-ids)]
    (respond/ok {:list updated-ins})))
