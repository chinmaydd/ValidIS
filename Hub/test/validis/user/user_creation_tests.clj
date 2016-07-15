;; test/validis/user/user-creation-tests.clj
(ns validis.user.user-creation-tests
  "Contains user creation tests."
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as query]
            [ring.mock.request :as mock]
            [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fixture function (before-hook) ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  "Empties the entire database for testing."
  [f]
    (do
      (query/empty-user-database)
      (f)
      (query/empty-user-database)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test user creation queries ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-user
  "Creates a test user provided a user map."
  [user-map]
  (app (-> (mock/request :post "/api/user" (ch/generate-string user-map))
           (mock/content-type "application/json"))))

(use-fixtures :each setup-teardown)

;; General function for checking a diff between two users.
(defn assert-no-dup
  "Asserts no duplication between users."
  [user-1 user-2 expected-error-message]
  (let [_         (create-user user-1)
        response  (create-user user-2)
        body      (helper/parse-body (:body response))]

    (is (= 409                       (:status response)))
    (is (= expected-error-message    (:error body)))))

;;;;;;;;;;;;;;;;
;; Test cases ;;
;;;;;;;;;;;;;;;;

(deftest can-successfully-create-a-new-user
  (testing "Can successfully create a new user"
    (let [test-user {:email "user1@test.com" :username "user1" :password "pass"}
          response  (create-user test-user)
          new-user  (query/get-user-by-field {:username (:username test-user)})]
      (is (= 200                      (:status response)))
      (is (= (:email test-user)       (:email new-user)))
      (is (= (:username test-user)    (:username new-user)))
      ;; Dummy test case
      ;; (is (= false (:verified? new-user)))
      )))

(deftest can-not-create-a-user-with-same-username
  (testing "Cannot create a user with the same username"
    (assert-no-dup {:email "user1@test.com" :username "user1" :password "pass"}
                  {:email "user2@test.com" :username "user1" :password "pass"}
                  "Username already exists!")))

(deftest can-not-create-a-user-with-same-email
  (testing "Cannot create a user with same email"
    (assert-no-dup {:email "user1@test.com" :username "user1" :password "pass"}
                   {:email "user1@test.com" :username "user2" :password "pass"}
                   "Email already exists!")))

(deftest can-not-create-a-user-with-same-email-and-username
  (testing "Cannot create a user with same email and username"
    (assert-no-dup {:email "user1@test.com" :username "user1" :password "pass"}
                   {:email "user1@test.com" :username "user1" :password "pass"}
                   "Username and email already exist!")))
