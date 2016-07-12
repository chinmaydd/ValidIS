;; src/route-functions/user/modify-user.clj
(ns validis.route-functions.user.modify-user
  (:require [validis.queries.user    :as query]
            [buddy.hashers           :as hashers]
            [ring.util.http-response :as respond]))

(defn modify-user
  "Modify user information. If no new information is provided, we use the already exsiting one for updating the database document."
  [current-user-info username password email]
  (let [new-email     (if (empty? email)    (str (:email current-user-info)) email)
        new-username  (if (empty? username) (str (:username current-user-info)) username)
        new-password  (if (empty? password) (:password current-user-info) (hashers/encrypt password))
        new-user-info (query/update-user  {:id (str (:_id current-user-info))
                                           :email new-email
                                           :username new-username
                                           :password new-password
                                           :refresh_token (:refresh_token current-user-info)})]
    (respond/ok {:id (str (:_id current-user-info)) :email new-email :username new-username})))

(defn modify-user-response
  "Generates a user response for modification of his own details."
  [request id username password email]
  (let [current-user-info (query/get-user-by-id {:id id})
        modifying-self?   (= (str id) (get-in request [:identity :id]))
        modify?           (and modifying-self? (not-empty current-user-info))]
    (cond
      modify?                    (modify-user current-user-info username password email)
      (not modifying-self?)      (respond/unauthorized {:error "Not authorized"})
      (empty? current-user-info) (respond/not-found {:error "Userid does not exist"}))))
