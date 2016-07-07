(ns validis.schemas.user
  (:require [schema.core :as s]))

;; A User schema is relatively easy to understand.
;; The use case kept in mind is that of a network-creator,
;; someone who is going to monitor the network of the entire city.
;; We can add other functionalities as well, but for v0.0.1 it is not needed.
(s/defschema User
  {:username s/Str
   :email    s/Str
   :password s/Str})
