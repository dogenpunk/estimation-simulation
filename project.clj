(defproject estimation-simulation "0.1.0-SNAPSHOT"
  :description "Runs a Monte Carlo simulation against a set of estimates from a CSV file."
  :url "https://github.com/redinger/estimation-simulation"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0-beta9"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.apache.commons/commons-math3 "3.1"]]
  :main estimation.simulation)
