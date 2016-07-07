;; src/schemas/cis.clj
(ns validis.schemas.cis
  (:require [schema.core :as s]))

(s/defschema CIS
  {(s/optional-key :_id) s/Str
   (s/optional-key :inserter-id) s/Str
   :name s/Str
   :address s/Str
   :api-url s/Str})

(s/defschema CISList
  [CIS])
