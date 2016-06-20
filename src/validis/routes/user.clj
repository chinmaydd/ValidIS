(ns validis.routes.user
  (:require [validis.middleware.cors                  :refer [cors-mw]]
            [validis.middleware.token-auth            :refer [token-auth-mw]]
            [validis.middleware.authenticated         :refer [authenticated-mw]]
            [validis.route-functions.user.create-user :refer [create-user-response]]
            [validis.route-functions.user.delete-user :refer [delete-user-response]]
            [validis.route-functions.user.modify-user :refer [modify-user-response]]
            [compojure.api.sweet                      :refer :all]))

;; Assumed for now that we are going to have the user ID as a string when it
;; actually is a Uuid

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