;; test/validis/network/delete_network_tests.clj
(ns validis.network.delete-network-tests
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.user :as u-query]
            [validis.queries.network :as n-query]
            [ring.mock.request :as mock]
            [cheshire.core :as ch]))

;;;;;;;;;;;;;;;;;;;;;;;
;; Teardown function ;;
;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown
  [f]
  (do
      (helper/add-users)
      (helper/add-networks)
      (f)
      (n-query/empty-network-database)
      (u-query/empty-user-database)))

(use-fixtures :each setup-teardown)

(deftest can-delete-network-if-valid-network-and-owner
  (testing "Can create network if valid user and owner"
    (let [network-info      (n-query/get-network-by-name {:name "test-network-1"})
          network-id        (str (:_id network-info))
          response          (app (-> (mock/request :delete (str "/api/network/" network-id))
                                     (helper/get-token-auth-header-for-user "user1:password")))
          body              (helper/parse-body (:body response))
          all-networks      (n-query/get-all-networks)
          expected-response (str "Network with id " network-id " removed successfully!")]
      (is (= 200               (:status response)))
      (is (= expected-response (:message body)))
      (is (= 1                 (count all-networks))))))
