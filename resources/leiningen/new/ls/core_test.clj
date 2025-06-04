(ns {{name}}.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [compojure.route :as route]))

(deftest test-route-resources
  (testing "Route resources should return a valid response"
    (let [response (route/resources "/")]
      (is (fn? response)))))

(deftest test-route-files
  (testing "Route files should return a valid response"
    (let [config {:path "/uploads" :uploads "public/uploads"}
          response (route/files (:path config) {:root (:uploads config)})]
      (is (fn? response)))))
