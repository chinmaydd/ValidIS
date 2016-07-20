;; src/routes/auth.clj
(ns validis.routes.auth
  "Routes for authentication API."
  (:require [validis.middleware.basic-auth :refer [basic-auth-mw]]
            [validis.middleware.authenticated :refer [authenticated-mw]]
            [validis.middleware.cors :refer [cors-mw]]
            [validis.route-functions.auth.get-auth-credentials :refer [auth-credentials-response]]
            [schema.core :as s]
            [compojure.api.sweet :refer :all]))

(def auth-routes
  "Specify routes for Authentication functions"
  (context "/api/auth" []
    :tags ["Auth"]

    (GET "/"             {:as request}
      :return        {:id String :username String :token String :refreshToken String}
      :header-params [authorization :- String]
      :middleware    [basic-auth-mw cors-mw authenticated-mw]
      :summary       "Returns auth info given a username and password in the '`Authorization`' header."
      :description   "Authorization header expects '`Basic username:password`' where `username:password`
                           is base64 encoded. To adhere to basic auth standards we have to use a field called
                           `username` however we will accept a valid username or email as a value for this key."
      (auth-credentials-response request))))
