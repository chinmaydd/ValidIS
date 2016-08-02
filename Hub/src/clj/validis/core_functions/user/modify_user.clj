;; src/validis/core-functions/user/modify-user.clj
(ns validis.core-functions.user.modify-user
  "Contains functions to modify user."
  (:require [validis.queries.user :as query]
            [buddy.hashers :as hashers]
            [ring.util.http-response :as respond]))

(defn modify-user
  "Modifies user information. If no new information is provided, we use the already exsiting one for updating the database document."
  [current-user-info id username password]
  (let [new-username  (if (empty? username) (:username current-user-info) username)
        new-password  (if (empty? password) (:password current-user-info) (hashers/derive password))
        new-user-info (query/update-user {:id id
                                          :username new-username
                                          :password new-password})]
    (respond/ok {:message (format "User id %s was successfully modified." id)})))

(defn modify-user-response
  "Generates a user response for modification of his own details."
  [request id username password]
  (let [current-user-info (query/get-user-by-id {:id id})
        modifying-self?   (= (str id) (get-in request [:identity :id]))
        new-user-query    (query/get-user-by-field {:username username})
        new-user-exists?  (not-empty new-user-query)
        modify?           (and modifying-self? (not new-user-exists?))]
    (cond
      modify?                    (modify-user current-user-info id username password)
      (not modifying-self?)      (respond/unauthorized {:error "Not authorized"})
      new-user-exists?           (respond/conflict {:error "User with the same username already exists!"}))))
