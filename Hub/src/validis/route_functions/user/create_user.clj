(ns validis.route-functions.user.create-user
  (:require [validis.queries.user    :as query]
            [buddy.hashers           :as hashers]
            [ring.util.http-response :as respond]))

(defn create-user-response
  "Create a new user with the provided credentials `email`, `username`, `password` and respond back with the `username` string."
  [email username password]
  (let [hashed-password (hashers/encrypt password)
        new-user        (query/insert-registered-user! {:email email
                                                        :username username
                                                        :password hashed-password})]
    (respond/created {:username (str (:username new-user))})))