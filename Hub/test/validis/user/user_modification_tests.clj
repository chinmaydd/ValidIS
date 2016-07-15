;; test/validis.user/user-modification-tests.clj
(ns validis.user.user-modification-tests
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

(deftest can-modify-user-who-is-self
  (testing "Can modify user who is self"
    (let [user-id (str (:_id (query/get-user-by-field {:username "user1"})))
          new-user-map {:username "userx" :password "passx"}
          response (app (-> (mock/request :patch (str "/api/user/" user-id) (ch/generate-string new-user-map))
                            (mock/content-type "application/json")
                            (helper/get-token-auth-header-for-user "user1:password")))
          body (helper/parse-body (:body response))
          expected-response (str "User id " user-id " was successfully modified.")]
      (is (= 200 (:status response)))
      (is (= expected-response (:message body)))
      (is (= 2 (count (query/get-all-users)))))))
