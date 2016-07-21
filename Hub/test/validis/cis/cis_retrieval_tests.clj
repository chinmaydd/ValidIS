;; test/validis/cis/cis_retrieval_tests.clj
(ns validis.cis.cis-retrieval-tests
  "Contains tests to check for retrieval of all CIS"
  (:require [clojure.test :refer :all]
            [validis.handler :refer :all]
            [validis.test-utils :as helper]
            [validis.queries.cis :as query]
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
      (helper/add-cis)
      (f)
      (query/empty-cis-database)))

(use-fixtures :each setup-teardown)

(deftest can-retrieve-all-cis-from-db
  (testing "Can retrieve all cis from database"
    (let [response           (app (-> (mock/request :get "/api/cis/all")))
          body               (helper/parse-body (:body response))
          cis-list           (:list body)
          all-cis            (query/get-all-cis)
          first-cis-from-db  (query/get-cis-by-field {:api-url "https://google.com"})
          second-cis-from-db (query/get-cis-by-field {:api-url "https://github.com"})
          updated-first-cis  (update-in first-cis-from-db [:_id] str)
          updated-second-cis (update-in second-cis-from-db [:_id] str)
          first-cis          (nth cis-list 0)
          second-cis         (nth cis-list 1)]
      (is (= 2          (count cis-list)))
      (is (= 2          (count all-cis)))
      (is (= first-cis  updated-first-cis))
      (is (= second-cis updated-second-cis)))))
