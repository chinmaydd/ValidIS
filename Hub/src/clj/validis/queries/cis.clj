;; src/validis/queries/cis.clj
(ns validis.queries.cis
  "Queries for the CIS document store."
  (:require [monger.collection :as mc]
            [validis.db-handler :refer [db]]
            [monger.operators :refer :all]
            [monger.util :refer [object-id]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Insert functions for CIS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn insert-cis
  "Inserts a new CIS.
  CIS-data is of the form:
  `{:inserter-id :name :address :api-url}`
  "
  [cis-data]
  (mc/insert-and-return db "cis" cis-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Getter functions for CIS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-all-cis
  "Returns all CIS"
  []
  (mc/find-maps db "cis"))

(defn get-cis-by-id
  "Returns a CIS, if exists.
  CIS-data is of the form:
  `{:cis-id}`
  "
  [cis-data]
  (let [id (object-id (:cis-id cis-data))]
    (mc/find-one-as-map db "cis" {:_id id})))

(defn get-cis-by-field
  "Returns a CIS, if exists.
  CIS-data is of the form:
  `{:field &}`
  "
  [cis-data]
  (mc/find-one-as-map db "cis" cis-data))

(defn get-all-urls
  "Returns a list of all urls for the given CIS.
  CIS-list is of the form:
  `[CIS_ID1 CIS_ID2 &]`
  "
  [cis-data]
  (let [cis-list (:cis-list cis-data)]
    (map #(get (mc/find-one-as-map db "cis" {:_id (object-id %)} [:api-url]) :api-url) cis-list)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Deletion queries for CIS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn empty-cis-database
  "Empties the CIS database and removes all the documents in it."
  []
  (mc/remove db "cis"))
