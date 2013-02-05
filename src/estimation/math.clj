(ns estimation.math)

(def confidence-interval-base
  {60 1.6852
   90 3.2897})

(defn mean
  "Returns the mean for the ln distribution given the low and high points"
  [low high]
  (/ (+ (Math/log high) (Math/log low)) 2))

(defn standard-deviation
  "Returns the standard deviation for the ln distribution given the low and high points"
  [low high confidence-interval]
  (/ (- (Math/log high) (Math/log low)) (confidence-interval-base confidence-interval)))
