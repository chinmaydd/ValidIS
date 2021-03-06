;; src/validis/queries/user.clj
(ns validis.queries.user
  "Queries for the user document store."
  (:require [monger.collection :as mc]
            [validis.db-handler :refer [db]]
            [monger.conversion :refer [from-db-object]]
            [monger.operators :refer :all]
            [monger.util :refer [object-id]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Insertion queries for User ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn insert-user
  "Inserts a user into the database.
  User data is of the form:
  `{:email :username :password}`
  "
  [user-data]
  (mc/insert-and-return db "users" user-data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Updation queries for User ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-user
  "Updates a user in the database.
  User data is of the form:
  `{:id :username :email :password}`
  "
  [user-data]
  (let [id (object-id (:id user-data))]
    (mc/update db "users" {:_id id} {$set (dissoc user-data :id)})))

(defn update-user-refresh-token
  "Updates the refresh token for the user.
  User data is of the form:
  `{:id :refresh-token}`
  "
  [user-data]
  (let [id            (object-id (:id user-data))
        refresh-token (:refresh-token user-data)]
    (mc/update db "users" {:_id id} {$set {:refresh_token refresh-token}})))

;;;;;;;;;;;;;;;;;
;; Verify User ;;
;;;;;;;;;;;;;;;;;

(defn verify-user
  "Verify a particular user!
  User data is of the form:
  `{:email}`
  "
  [user-data]
  (mc/update db "users" {:email user-data} {$set {:verified? true}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Deletion queries for User ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; HACK: We need to invoke `.getN` since the only way we can check if the document was deleted is to check the number of updated/inserted/deleted documents. We can directly return this value.
(defn delete-user
  "Deletes a user with the id.
  User data is of the form:
  `{:id}`
  "
  [user-data]
  (let [id (object-id (:id user-data))]
    (.getN (mc/remove-by-id db "users" id))))

(defn empty-user-database
  "Removes all documents in the user database."
  []
  (mc/remove db "users"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Getter functions for User ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-all-users
  "Returns all the users in the db"
  []
  (mc/find-maps db "users"))

(defn get-user-by-field
  "Returns a user with the field, if exists
  User data is of the form:
  `{:field &}`
  "
  [user-data]
  (mc/find-one-as-map db "users" user-data))

(defn get-user-by-id
  "Returns a user with the id, if exists.
  User data is of the form:
  `{:id}`
  "
  [user-data]
  (let [id (object-id (:id user-data))]
    (mc/find-one-as-map db "users" {:_id id})))
