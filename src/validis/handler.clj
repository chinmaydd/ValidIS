(ns validis.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [validis.routes.user :refer [user-routes]]
            [schema.core :as s]))

; (s/defschema Pizza
;   {:name s/Str
;    (s/optional-key :description) s/Str
;    :size (s/enum :L :M :S)
;    :origin {:country (s/enum :FI :PO)
;             :city s/Str}})

(defapi app
  {:swagger
   {:ui "/"
    :spec "/swagger.json"
    :data {:info {:title "Validis"
                  :description "Cloud based runtime monitoring system for clinical information systems"
                  :version "0.0.1"}
           :tags [{:name "User" :description "Create, delete and update user details"}]}}}

  user-routes)
