;; test/validis.user/user-deletion-tests.clj
(ns validis.user.user-deletion-tests
  "Contains user deletion tests."
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as query]
            [ring.mock.request :as mock]
            [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;
;; Teardown function ;;
;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  [f]
  (do
    (helper/add-users)
    (f)
    (query/empty-user-database)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test user for deletion queries ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use-fixtures :each setup-teardown)

(deftest can-delete-user-who-is-self
  (testing "Can delete user who is self"
    (let [user-id           (str (:_id (query/get-user-by-field {:username "user1"})))
          response          (app (-> (mock/request :delete (str "/api/user/" user-id))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          expected-response (str "User id " user-id " successfully removed")]
      (is (= 200               (:status response)))
      (is (= expected-response (:message body)))
      (is (= 1                 (count (query/get-all-users)))))))

(deftest can-not-delete-user-who-is-not-self
  (testing "Cannot delete user who is not self"
    (let [user-id-1               (str (:_id (query/get-user-by-field {:username "user1"})))
          user-id-2               (str (:_id (query/get-user-by-field {:username "user2"})))
          response                (app (-> (mock/request :delete (str "/api/user/" user-id-1))
                                           (mock/content-type "application/json")
                                           (helper/get-token-auth-header-for-user "user2:pass12345")))
          body                    (helper/parse-body (:body response))
          expected-error-response "Not authorized"]
      (is (= 401                     (:status response)))
      (is (= expected-error-response (:error body)))
      (is (= 2                       (count (query/get-all-users)))))))
