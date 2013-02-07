(ns estimation.simulation-test
  (:use clojure.test
        estimation.simulation)
    (:import [org.apache.commons.math3.stat.descriptive.rank Percentile]))

(deftest simulation-test
  (let [trials (simulate "test/fixtures/estimates.csv")
        percentile (Percentile.)]
    (is (= 1 (Math/round (.evaluate percentile trials 5))))
    (is (= 2 (Math/round (.evaluate percentile trials 10))))
    (is (= 3 (Math/round (.evaluate percentile trials 20))))
    (is (= 3 (Math/round (.evaluate percentile trials 25))))
    (is (= 5 (Math/round (.evaluate percentile trials 50))))
    (is (= 10 (Math/round (.evaluate percentile trials 75))))
    (is (= 12 (Math/round (.evaluate percentile trials 80))))))
