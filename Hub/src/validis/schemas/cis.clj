;; src/schemas/cis.clj
(ns validis.schemas.cis
  (:require [schema.core :as s]))

(s/defschema CIS
  {:name s/Str
   :address s/Str
   :api-url s/Str})
