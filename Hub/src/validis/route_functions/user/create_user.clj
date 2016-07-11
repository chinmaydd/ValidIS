;; src/route-functions/user/create-user.clj
(ns validis.route-functions.user.create-user
  (:require [validis.queries.user    :as query]
            [buddy.hashers           :as hashers]
            [ring.util.http-response :as respond]
            [postal.core             :as mail]
            [crypto.random           :as crypto] 
            [environ.core            :refer [env]]))

(defn send-verification-email
  "Sends a verification email to the user."
  [email verification-string]
  (let [conn  {:host "smtp.gmail.com"
                :user (env :email)
                :pass (env :password)
                :ssl true}
        body-string (str "Your API key is " verification-string ". Please verify yourself at /api/verify endpoint. We are stoked to have you using our system. - LeadLab")
        config {:from (env :email)
                :to email
                :subject "Welcome to Validis - Real time safety monitoring for your clinical information devices."
                :body body-string}]
    (mail/send-message conn config)))


(defn create-user
  "Create user with  `email`, `username`, `password`."
  [email username password] 
  (let [hashed-password     (hashers/encrypt password)
        verification-string (crypto/base64 5) 
        new-user            (query/insert-user {:email email
                                                :username username
                                                :password hashed-password
                                                :verified? false
                                                :verification-string verification-string})
        _                   (send-verification-email email verification-string)]
    (respond/ok {:message "A verification email has been sent. Please verify yourself at /api/verify."})))


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
