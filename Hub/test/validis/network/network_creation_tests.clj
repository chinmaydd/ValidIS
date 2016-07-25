;; test/validis/network/network_creation_tests.clj
(ns validis.network.network-creation-tests
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
      (f)
      (n-query/empty-network-database)
      (u-query/empty-user-database)))

(use-fixtures :each setup-teardown)

(deftest can-create-network-if-unique-network-name-and-valid-user
  (testing "Can create user if unique network name and valid user"
    (let [test-user    (u-query/get-user-by-field {:username "user1"})
          user-id      (str (:_id test-user))
          network-data {:name "test-network-1" :location "BC"}
          response     (app (-> (mock/request :post "/api/network" (ch/generate-string network-data))
                                (mock/content-type "application/json")
                                (helper/get-token-auth-header-for-user "user1:password")))
          network-1    (n-query/get-network-by-name {:name "test-network-1"})
          network-id   (str (:_id network-1))
          all-networks (n-query/get-all-networks)
          body         (helper/parse-body (:body response))]
      (is (= 200        (:status response)))
      (is (= network-id (:network-id body)))
      (is (= "BC"       (:location network-1)))
      (is (= user-id    (:owner-id network-1)))
      (is (= 1          (count all-networks))))))

(deftest can-not-create-network-with-the-same-name
  (testing "Cannot create a network with the same name and valid user"
    (let [network-1               {:name "test-network-1" :location "BC"}
          _                       (app (-> (mock/request :post "/api/network" (ch/generate-string network-1))
                                           (mock/content-type "application/json")
                                           (helper/get-token-auth-header-for-user "user1:password")))
          network-2               {:name "test-network-1" :location "Vancouver"}
          response                (app (-> (mock/request :post "/api/network" (ch/generate-string network-2))
                                           (mock/content-type "application/json")
                                           (helper/get-token-auth-header-for-user "user1:password")))
          all-networks           (n-query/get-all-networks)
          body                   (helper/parse-body (:body response))
          expected-error-message "Network already exists!"]
      (is (= 409                    (:status response)))
      (is (= expected-error-message (:error body)))
      (is (= 1                      (count all-networks))))))
