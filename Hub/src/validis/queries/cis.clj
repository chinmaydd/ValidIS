;; src/queries/cis.clj
(ns validis.queries.cis
  (:require [monger.collection  :as mc]
            [validis.db-handler :refer [db]]
            [monger.conversion  :refer [from-db-object]]
            [monger.operators   :refer :all]
            [monger.util        :refer [object-id]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Insert functions for CIS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn insert-cis
  "Inserts a new CIS.
  CIS-data is of the form:
  {:inserter-id :name :address :api-url}
  "
  [cis-data]
  (mc/insert-and-return db "cis" cis-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Getter functions for CIS ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-cis-by-id
  "Returns a CIS, if exists.
  CIS-data is of the form:
  {:cis-id}
  "
  [cis-data]
  (let [id (object-id (:cis-id cis-data))] 
    (mc/find-one-as-map db "cis" {:_id id})))

(defn get-cis-by-field
  "Returns a CIS, if exists.
  CIS-data is of the form:
  {:field &}
  "
  [cis-data]
  (mc/find-one-as-map db "cis" cis-data))
