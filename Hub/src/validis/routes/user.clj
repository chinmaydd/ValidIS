(ns validis.routes.user
  (:require [validis.middleware.cors                    :refer [cors-mw]]
            [validis.middleware.token-auth              :refer [token-auth-mw]]
            [validis.middleware.authenticated           :refer [authenticated-mw]]
            [validis.route-functions.user.create-user   :refer [create-user-response]]
            [validis.route-functions.user.delete-user   :refer [delete-user-response]]
            [validis.route-functions.user.modify-user   :refer [modify-user-response]]
            [validis.route-functions.user.list-networks :refer [list-network-response]]
            [ring.swagger.json-schema                   :as json-schema]
            [schema.core                                :as s]
            [compojure.api.sweet                        :refer :all])
  (:import  [org.bson.types.ObjectId]))

(s/defschema Network
  {:owner-id s/Str
   :_id s/Str
   :location s/Str
   :name s/Str
   })

(s/defschema NetworksList
  [Network])

(s/defschema User
  {:username s/Str
   :email s/Str
   :password s/Str})

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

    (DELETE "/:id"            {:as request}
             :path-params     [id :- String]
             :header-params   [authorization :- String]
             :return          {:message String}
             :middleware      [token-auth-mw cors-mw authenticated-mw]
             :summary         "Deletes the specified user. Requires token to have `admin` auth or self ID."
             :description     "Authorization header expects the following format 'Token {token}'"
             (delete-user-response request id))

    (PATCH  "/:id"          {:as request}
             :path-params   [id :- String]
             :body-params   [{username :- String ""}
                             {password :- String ""} 
                             {email :- String ""}]
             :header-params [authorization :- String]
             :return        User 
             :middleware    [token-auth-mw cors-mw authenticated-mw]
             :summary       "Update some or all fields of a specified user. Requires token to have `admin` auth or self ID."
             :description   "Authorization header expects the following format 'Token {token}'"
             (modify-user-response request id username password email))

    (GET "/networks" {:as request}
            :header-params [authorization :- String]
            :return {:list NetworksList}
            :middleware [token-auth-mw cors-mw authenticated-mw]
            :summary "Used to return a list of networks belonging to a user"
            :description "Authorization header expecs the following format 'Token {token}'"
            (list-network-response request))))
