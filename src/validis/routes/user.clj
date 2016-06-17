(ns validis.routes.user
  (:require [validis.middleware.cors                  :refer [cors-mw]]
            [validis.route-functions.user.create-user :refer [create-user-response]]
            [compojure.api.sweet                      :refer :all]))

(def user-routes
  "Specify routes for the user functions"
  (context "/api/user" []
           :tags ["User"]

    (POST "/"           {:as request}
          :return       {:username String}
          :middleware   [cors-mw]
          :body-params  [email :- String username :- String password :- String]
          :summary      "Create a new user with provided username, email and password."
          (create-user-response email username password))))