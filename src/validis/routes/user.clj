(ns validis.routes.user
  (:require [validis.middleware.cors                  :refer [cors-mw]]
            [validis.middleware.token-auth            :refer [token-auth-mw]]
            [validis.middleware.authenticated         :refer [authenticated-mw]]
            [validis.route-functions.user.create-user :refer [create-user-response]]
            [validis.route-functions.user.delete-user :refer [delete-user-response]]
            [validis.route-functions.user.modify-user :refer [modify-user-response]]
            [ring.swagger.json-schema                 :as json-schema]
            [schema.core                              :as s]
            [compojure.api.sweet                      :refer :all])
  (:import  [org.bson.types ObjectId]))

;; Test schema for a Pizza!
; (s/defschema Pizza
;   {:name s/Str
;    (s/optional-key :description) s/Str
;    :size (s/enum :L :M :S)
;    :origin {:country (s/enum :FI :PO)
;             :city s/Str}})

; More details to be added later
;; Let us say that the user ID is of the type :_id (org.bson.types.ObjectId.)
;; Hence the User schema can be defined as:
(s/defschema User
  {:name s/Str
   :username s/Str
   :password s/Str
   :id (ObjectId.)
   })

;; Add swagger support for ObjectId
;; adding swagger-support for ObjectId
(defmethod json-schema/json-type ObjectId [_] {:type "string"})

(def user-routes
  "Specify routes for the user functions"
  (context "/api/user" []
           :tags ["User"]

    (POST "/"           {:as request}
          :return       {:username String}
          :middleware   [cors-mw]
          :body-params  [email :- String username :- String password :- String]
          :summary      "Create a new user with provided username, email and password."
          (create-user-response email username password))

    (DELETE "/:id"        {:as request}
             :path-params [id :- String]
             :header-params [authorization :- String]
             :return      {:message String}
             :middleware  [token-auth-mw cors-mw authenticated-mw]
             :summary     "Deletes the specified user. Requires token to have `admin` auth or self ID."
             :description "Authorization header expects the following format 'Token {token}'"
             (delete-user-response request id))

    (PATCH  "/:id"          {:as request}
             :path-params   [id :- String]
             :body-params   [{username :- String ""} {password :- String ""} {email :- String ""}]
             :header-params [authorization :- String]
             :return        {:id String :email String :username String}
             :middleware    [token-auth-mw cors-mw authenticated-mw]
             :summary       "Update some or all fields of a specified user. Requires token to have `admin` auth or self ID."
             :description   "Authorization header expects the following format 'Token {token}'"
             (modify-user-response request id username password email))))