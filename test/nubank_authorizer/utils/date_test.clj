(ns nubank-authorizer.utils.date-test
  (:use midje.sweet) 
  (:require [clojure.test :refer :all]
            [nubank-authorizer.core :refer :all]
            [nubank-authorizer.utils.date :as date-util]
            [clj-time [format :as time-formatter]]
            [clj-time [core :as t]]))

(facts "Date util"
  (fact "Given a valid time, the result must be the time interval in minutes"
    (def begin (t/minus (t/now) (t/minutes 2)))
    (def end (t/now))
    (date-util/get-interval-in-minutes begin end)
    => 2))