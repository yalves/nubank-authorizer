(ns nubank-authorizer.utils.date
  (:require [clj-time
              [core :as t]]))

(defn get-interval-from-now-in-minutes
  [time]
  (t/in-minutes (t/interval time (t/minus (t/now) (t/hours 3)))))