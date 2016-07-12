;; src/route-functions/cis/get_all_cis.clj
(ns validis.route-functions.cis.get-all-cis
  (:require [validis.queries.cis     :as query]
            [ring.util.http-response :as respond]))

(defn get-all-cis
  "Returns a list of all CIS with their ids. This is useful when registered users need to add a new CIS to their network but do not have the ID. Response is a list of CIS with their information and IDs."
  []
  (let [all-cis (query/get-all-cis)]
    (respond/ok {:list all-cis})))
