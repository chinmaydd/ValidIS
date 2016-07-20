;; test/validis/validis/cis/insert_cis_tests.clj
(ns validis.user.user-modification-tests
  "Contains user deletion tests."
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as query]
            [ring.mock.request :as mock]
            [buddy.hashers :as hashers]
            [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;
;; Teardown function ;;
;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  "Sets up the database for testing"
  [f]
  (do
    (helper/add-users)
    (f)
    (query/empty-user-database)))

(use-fixtures :each setup-teardown)

(deftest can-modify-user-who-is-self
  (testing "Can modify user who is self"
    (let [user-id           (str (:_id (query/get-user-by-field {:username "user1"})))
          new-user-map      {:username "userx" :password "passx"}
          response          (app (-> (mock/request :patch (str "/api/user/" user-id) (ch/generate-string new-user-map))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body                       (helper/parse-body (:body response))
          expected-response (str "User id " user-id " was successfully modified.")]
      (is (= 200               (:status response)))
      (is (= expected-response (:message body)))
      (is (= 2                 (count (query/get-all-users)))))))

(deftest can-not-modify-user-who-is-not-self
  (testing "Cannot modify user who is not self"
    (let [user-id            (str (:_id (query/get-user-by-field {:username "user1"})))
          new-user-map       {:username "userx" :password "passx"}
          response           (app (-> (mock/request :patch (str "/api/user/" user-id) (ch/generate-string new-user-map))
                                      (mock/content-type "application/json")
                                      (helper/get-token-auth-header-for-user "user2:pass12345")))
          body                        (helper/parse-body (:body response))
          expected-response  "Not authorized"]
      (is (= 401               (:status response)))
      (is (= expected-response (:error body)))
      (is (= 2                 (count (query/get-all-users)))))))

(deftest can-not-modify-user-to-change-username-to-existing
 (testing "Cannot modify username to change it to already existing"
   (let [user-id           (str (:_id (query/get-user-by-field {:username "user1"})))
         new-user-map      {:username "user2" :password "passx"}
         response          (app (-> (mock/request :patch (str "/api/user/" user-id) (ch/generate-string new-user-map))
                                    (mock/content-type "application/json")
                                    (helper/get-token-auth-header-for-user "user1:password")))
         body              (helper/parse-body (:body response))
         expected-response "User with the same username already exists!"]
     (is (= 409               (:status response)))
     (is (= expected-response (:error body)))
     (is (= 2                 (count (query/get-all-users)))))))

(deftest keep-the-old-credentials-if-no-data-was-provided
  (testing "Keep old credentials if no data was provided in the reqest"
    (let [user-id           (str (:_id (query/get-user-by-field {:username "user1"})))
          new-user-map      {}
          response          (app (-> (mock/request :patch (str "/api/user/" user-id) (ch/generate-string new-user-map))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          expected-response (str "User id " user-id " was successfully modified.")
          updated-user      (query/get-user-by-id {:id user-id})
          updated-username  (:username updated-user)
          updated-password  (:password updated-user)]
      (is (= 200               (:status response)))
      (is (= expected-response (:message body)))
      (is (= "user1"           updated-username))
      (is (hashers/check "password" updated-password)))))
