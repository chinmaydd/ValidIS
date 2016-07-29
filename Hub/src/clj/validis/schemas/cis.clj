;; src/validis/schemas/cis.clj
(ns validis.schemas.cis
  "Schemas for CIS and CISList."
  (:require [schema.core :as s]))

(s/defschema CIS
  "Schema for a CIS."
  {(s/optional-key :_id) s/Str
   :name s/Str
   :address s/Str
   :api-url s/Str})

(s/defschema CISList
  "Schema for a CISList."
  [CIS])
