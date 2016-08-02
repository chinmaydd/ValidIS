;; src/validis/api-routes/auth.clj
(ns validis.api-routes.auth
  "api-routes for authentication API."
  (:require [validis.middleware.basic-auth :refer [basic-auth-mw]]
            [validis.middleware.authenticated :refer [authenticated-mw]]
            [validis.middleware.cors :refer [cors-mw]]
            [validis.core-functions.auth.get-auth-credentials :refer [auth-credentials-response]]
            [schema.core :as s]
            [compojure.api.sweet :refer :all]))

(def auth-api-routes
  "Specify api-routes for Authentication functions"
  (context "/api/auth" []
    :tags ["Auth"]

    (GET "/"         {:as request}
      :return        {:id String :username String :token String :refreshToken String}
      :header-params [authorization :- String]
      :middleware    [basic-auth-mw cors-mw authenticated-mw]
      :summary       "Returns auth info given a username and password in the '`Authorization`' header."
      :description   "Authorization header expects '`Basic username:password`' where `username:password`
                     is base64 encoded. Username or email is accepted."
      (auth-credentials-response request))))
