(ns estimation.simulation
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [estimation.math :as math])
  (:import [org.apache.commons.math3.distribution LogNormalDistribution]
           [org.apache.commons.math3.stat.descriptive.rank Percentile]))

(defn- p20
  "Returns the 20% confidence estimate"
  [low-confidence-index line]
  (let [p20 (try (Double/parseDouble (get line low-confidence-index)) (catch Exception e 0))]
    (if (> p20 0) p20 0.0001)))

(defn- p80
  "Returns the 80% confidence estimate"
  [high-confidence-index line]
  (let [p80 (try (Double/parseDouble (get line high-confidence-index)) (catch Exception e 0))]
    (if (> p80 0) p80 0.001)))

(defn trial-distribution
  "Return a distribution for this estimate"
  [low-confidence-index high-confidence-index line]
  (let [p20 (p20 low-confidence-index line)
        p80 (p80 high-confidence-index line)
        mean (math/mean p20 p80)
        stddev (math/standard-deviation p20 p80 60)]
    (LogNormalDistribution. mean stddev)))

(defn trials
  "Run the trials"
  [num-trials low-confidence-index high-confidence-index lines]
  (double-array (apply map + (for [line lines]
                               (let [distribution (trial-distribution low-confidence-index high-confidence-index line)]
                                    (repeatedly num-trials #(.sample distribution)))))))
(defn -main
  "Runs a Monte Carlo simulation against a set of estimates from a CSV file."
  [& args]
  (with-open [in-file (io/reader (first args))]
    (let [lines (csv/read-csv (slurp in-file))
          header (first lines)
          low-confidence-index (.indexOf header "Low Confidence Estimate")
          high-confidence-index (.indexOf header "High Confidence Estimate")
          lines (rest lines)
          percentile (Percentile. )
          trials (trials 100000 low-confidence-index high-confidence-index lines)]
      (println (format "Ran %d simulations against %d estimates" (count trials) (count lines)))
      (doseq [x [ 5 10 20 25 50 75 80 90 95]]
        (println (format "%d%% likely to be done after %d days" x (Math/round (.evaluate percentile trials x))))))))
