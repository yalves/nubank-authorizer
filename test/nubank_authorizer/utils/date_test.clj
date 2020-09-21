(ns nubank-authorizer.utils.date-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.utils.date :as date-util]
            [clj-time [format :as time-formatter]]
            [clj-time [core :as t]]))

(facts "Date util"
  (fact "Given a valid time, the result must be the time interval in minutes"
    (def brazil-time (t/minus (t/now) (t/hours 3)))
    (def time (t/minus brazil-time (t/minutes 2)))
    (date-util/get-interval-from-now-in-minutes time)
    => 2))