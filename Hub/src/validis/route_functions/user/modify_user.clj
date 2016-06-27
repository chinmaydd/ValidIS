(ns validis.route-functions.user.modify-user
  (:require [validis.queries.user    :as query]
            [buddy.hashers           :as hashers]
            [ring.util.http-response :as respond]))

;; TODO Make sure that the :_id field returned by the monger query is a "String"
(defn modify-user
  "Update user info (`:email`/`:username`/`:password`)"
  [current-user-info username password email]
  (let [new-email     (if (empty? email)    (str (:email current-user-info)) email)
        new-username  (if (empty? username) (str (:username current-user-info)) username)
        new-password  (if (empty? password) (:password current-user-info) (hashers/encrypt password))
        new-user-info (query/update-registered-user!  {:id (str (:_id current-user-info))
                                                       :email new-email
                                                       :username new-username
                                                       :password new-password
                                                       :refresh_token (:refresh_token current-user-info)})]
    (respond/ok {:id (str (:_id current-user-info)) :email new-email :username new-username})))

(defn modify-user-response
  "User is allowed to update attributes for a user if the requester is
   modifying attributes associated with its own id or has admin permissions."
  [request id username password email]
  (let [auth              (get-in request [:identity])
        current-user-info (query/get-registered-user-by-id {:id id})
        modifying-self?   (= (str id) (get-in request [:identity :id]))
        modify?           (and modifying-self? (not-empty current-user-info))]
    (cond
      modify?                    (modify-user current-user-info username password email)
      (not modifying-self?)      (respond/unauthorized {:error "Not authorized"})
      (empty? current-user-info) (respond/not-found {:error "Userid does not exist"}))))
