;; src/validis/schemas/network.clj
(ns validis.schemas.network
  "Schemas for Network and NetworkList."
  (:require [schema.core         :as s]
            [validis.schemas.cis :refer [CIS]]))

;; A network is considered to be an aggregation of multiple CISs.
;; But, unless the user specifically asks for the data, theschema should match the below specification.
(s/defschema Network
  "Schema for Network."
  {:name     s/Str
   :owner-id s/Str
   :_id      s/Str
   :location s/Str
   :CIS-list [s/Str]
   :shared-user-list [s/Str]})

(s/defschema NetworksList
  "Schema for NetworksList."
  [Network])
