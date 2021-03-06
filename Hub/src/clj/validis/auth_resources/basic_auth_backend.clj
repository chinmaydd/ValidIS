;; src/validis/auth_resources/basic_auth_backend.clj
(ns validis.auth-resources.basic-auth-backend
  "User auth backend."
  (:require [validis.queries.user :as query]
            [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
            [buddy.hashers :as hashers]))

(defn get-user-info
  "Since we want to accept eiter username or email as an identifier we will query for both and check
   for a match."
  [identifier]
  (let [registered-user-username (query/get-user-by-field {:username identifier})
        registered-user-email    (query/get-user-by-field {:email identifier})
        registered-user          (first (remove nil? [registered-user-username registered-user-email]))]
    (when-not (nil? registered-user)
      {:user-data (-> registered-user
                      (assoc-in [:username] (str (:username registered-user)))
                      (assoc-in [:email]    (str (:email registered-user)))
                      (assoc-in [:id]       (str (:_id registered-user)))
                      (dissoc   :created_on)
                      (dissoc   :password))
       :password  (:password registered-user)})))

(defn basic-auth
  "This function will delegate determining if we have the correct username and
   password to authorize a user. The return value will be added to the request
   with the keyword of :identity. We will accept either a valid username or
   valid user email in the username field. It is a little strange but to adhere
   to legacy basic auth api of using username:password we have to make the
   field do double duty."
  [request auth-data]
  (let [identifier  (:username auth-data)
        password    (:password auth-data)
        user-info   (get-user-info identifier)]
    (if (and user-info (hashers/check password (:password user-info)))
      (:user-data user-info)
      false)))

(def basic-backend
  "Use the basic-auth function defined in this file as the authentication
   function for the http-basic-backend"
  (http-basic-backend {:authfn basic-auth}))
