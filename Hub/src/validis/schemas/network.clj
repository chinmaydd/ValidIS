(ns validis.schemas.network
  (:require [schema.core         :as s]
            [validis.schemas.cis :refer [CIS]]))

;; A network is considered to be an aggregation of multiple CISs.
;; But, unless the user specifically asks for the data, theschema should match the below specification.
(s/defschema Network
  {:name     s/Str
   :owner-id s/Str
   :_id      s/Str
   :location s/Str
   :CIS_list [CIS]
   })

(s/defschema NetworksList
  [Network])
