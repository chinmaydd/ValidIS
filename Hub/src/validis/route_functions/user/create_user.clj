;; src/route-functions/user/create-user.clj
(ns validis.route-functions.user.create-user
  (:require [validis.queries.user    :as query]
            [buddy.hashers           :as hashers]
            [ring.util.http-response :as respond]))

(defn create-user
  "Create user with  `email`, `username`, `password`."
  [email username password] 
  (let [hashed-password (hashers/encrypt password)
        new-user        (query/insert-user {:email email
                                            :username username
                                            :password hashed-password})]
    (respond/created {:username username})))


(defn create-user-response
  "Generate response for user creation."
  [email username password]
  (let [username-query    (query/get-user-by-field {:username username})
        email-query       (query/get-user-by-field {:email email})
        username-exists?  (not-empty username-query)
        email-exists?     (not-empty email-query)]
    (cond
      (and username-exists? email-exists?) (respond/conflict {:error "Username and email already exist!"})
      username-exists?                     (respond/conflict {:error "Username already exists!"})
      email-exists?                        (respond/conflict {:error "Email already exists!"})
      :else                                (create-user email username password))))
