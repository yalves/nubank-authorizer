(ns nubank-authorizer.utils.date
  (:require [clj-time
              [core :as t]]))

(defn get-interval-in-minutes
  [begin end]
  (t/in-minutes (t/interval begin end)))