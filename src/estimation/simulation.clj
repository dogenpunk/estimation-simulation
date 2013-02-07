(ns estimation.simulation
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [estimation.math :as math])
  (:import [org.apache.commons.math3.distribution LogNormalDistribution]
           [org.apache.commons.math3.stat.descriptive.rank Percentile]))

(defn- get-estimate
  "Returns the estimate at the index"
  [index line]
  (try (Double/parseDouble (get line index)) (catch Exception e 0)))

(defn- low-estimate
  "Returns the low estimate"
  [low-confidence-index line]
  (let [estimate (get-estimate low-confidence-index line)]
    (if (> estimate 0) estimate 0.0001)))

(defn- high-estimate
  "Returns the high  estimate"
  [high-confidence-index line]
  (let [estimate (get-estimate high-confidence-index line)]
    (if (> estimate 0) estimate 0.001)))

(defn trial-distribution
  "Return a distribution for this estimate"
  [low-confidence-index high-confidence-index line]
  (let [p20 (low-estimate low-confidence-index line)
        p80 (high-estimate high-confidence-index line)
        mean (math/mean p20 p80)
        stddev (math/standard-deviation p20 p80 60)]
    (LogNormalDistribution. mean stddev)))

(defn trials
  "Run the trials"
  [num-trials low-confidence-index high-confidence-index lines]
  (double-array (apply map + (for [line lines]
                               (let [distribution (trial-distribution low-confidence-index high-confidence-index line)]
                                    (repeatedly num-trials #(.sample distribution)))))))
(defn simulate
  "Runs the simulation"
  [file]
  (with-open [in-file (io/reader file)]
    (let [lines (csv/read-csv (slurp in-file))
          header (first lines)
          low-confidence-index (.indexOf header "Low Confidence Estimate")
          high-confidence-index (.indexOf header "High Confidence Estimate")
          lines (rest lines)]
      (trials 100000 low-confidence-index high-confidence-index lines))))

(defn -main
  "Runs a Monte Carlo simulation against a set of estimates from a CSV file."
  [& args]
  (let [percentile (Percentile.)
        trials (simulate (first args))]
    (println (format "Ran %d simulations" (count trials)))
    (doseq [x [ 5 10 20 25 50 75 80 90 95]]
      (println (format "%d%% likely to be done after %d days" x (Math/round (.evaluate percentile trials x)))))))
