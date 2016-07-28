;; src/validis/api-routes/user.clj
(ns validis.api-routes.user
  "api-routes for user."
  (:require
   ;; Middleware inclusions
   [validis.middleware.cors :refer [cors-mw]]
   [validis.middleware.token-auth :refer [token-auth-mw]]
   [validis.middleware.authenticated :refer [authenticated-mw]]

   ;; Route function inclusions
   [validis.core-functions.user.create-user :refer [create-user-response]]
   [validis.core-functions.user.delete-user :refer [delete-user-response]]
   [validis.core-functions.user.modify-user :refer [modify-user-response]]
   [validis.core-functions.user.list-networks :refer [list-network-response]]

   ;; Schema inclusions
   [validis.schemas.user :refer [User]]
   [validis.schemas.network :refer [NetworksList]]

   ;; Utility libs
   [ring.swagger.json-schema :as json-schema]
   [schema.core :as s]
   [compojure.api.sweet :refer :all]))

(def user-api-routes
  "Specify api-routes for the user functions"
  (context "/api/user" []
    :tags ["User"]

    (POST "/"       {:as request}
      :return       {:message String}
      :middleware   [cors-mw]
      :body-params  [email :- String username :- String password :- String]
      :summary      "Create a new user with provided username, email and password."
      (create-user-response email username password))

    (DELETE "/:id"     {:as request}
      :path-params     [id :- String]
      :header-params   [authorization :- String]
      :return          {:message String}
      :middleware      [token-auth-mw cors-mw authenticated-mw]
      :summary         "Deletes the specified user. Requires token to have self ID."
      :description     "Authorization header expects the following format 'Token {token}'"
      (delete-user-response request id))

    (PATCH  "/:id"   {:as request}
      :path-params   [id :- String]
      :body-params   [{username :- String ""}
                      {password :- String ""}]
      :header-params [authorization :- String]
      :return        {:message String}
      :middleware    [token-auth-mw cors-mw authenticated-mw]
      :summary       "Update some or all fields of a specified user. Requires token to have self ID."
      :description   "Authorization header expects the following format 'Token {token}'"
      (modify-user-response request id username password))

    (GET "/networks" {:as request}
      :header-params [authorization :- String]
      :return {:list NetworksList}
      :middleware [token-auth-mw cors-mw authenticated-mw]
      :summary "Used to return a list of networks belonging to a user."
      :description "Authorization header expecs the following format 'Token {token}'"
      (list-network-response request))))
