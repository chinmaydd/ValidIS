;; test/validis/network/network_modification_tests.clj
(ns validis.network.network-modification-tests
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.network :as n-query]
            [validis.queries.user :as u-query]
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test user for deletion queries ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(use-fixtures :each setup-teardown)

(deftest can-modify-network-with-different-name-by-owner
  (testing "Can modify network with a different name by owner"
    (let [network-1         (n-query/get-network-by-name {:name "test-network-1"})
          network-1-id      (str (:_id network-1))
          new-network-data  {:name "test-network-3" :location "India"}
          response          (app (-> (mock/request :patch (str "/api/network/" network-1-id) (ch/generate-string new-network-data))
                                     (mock/content-type "application/json")
                                     (helper/get-token-auth-header-for-user "user1:password")))
         modified-network   (n-query/get-network-by-id {:network-id network-1-id})
         modified-name      (:name modified-network)
         modified-location  (:location modified-network)
         body               (helper/parse-body (:body response))]
      (is (= 200              (:status response)))
      (is (= "test-network-3" modified-name))
      (is (= "India"          modified-location)))))

(deftest can-not-modify-network-with-same-name-by-owner
  (testing "Cannot modify network with a same final name by owner"
    (let [network-1             (n-query/get-network-by-name {:name "test-network-1"})
          network-1-id          (str (:_id network-1))
          new-network-data      {:name "test-network-2" :location "India"}
          response              (app (-> (mock/request :patch (str "/api/network/" network-1-id) (ch/generate-string new-network-data))
                                         (mock/content-type "application/json")
                                         (helper/get-token-auth-header-for-user "user1:password")))
         modified-network       (n-query/get-network-by-id {:network-id network-1-id})
         network-name           (:name modified-network)
         network-location       (:location modified-network)
         body                   (helper/parse-body (:body response))
        expected-error-message "Network with the same name already exists!"]
      (is (= 409                    (:status response)))
      (is (= expected-error-message (:error body)))
      (is (= "test-network-1"       network-name))
      (is (= "BC"                   network-location)))))
