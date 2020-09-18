(ns nubank-authorizer.core
  (:gen-class)
  (:require [clojure.data.json :as json]))

(def account-data "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
(def transaction-data "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 110, \"time\": \"2019-02-13T11:00:00.000Z\" } }")

(def user-account (atom { :account { :activeCard false :availableLimit 0}}))
(def user-transactions (atom [{ :transaction { :activeCard false :availableLimit 0}}]))
(def violations (atom []))

(defn process-acount-operation
  [account]
  (swap! user-account assoc :account account)
  (println (json/write-str @user-account)))

(defn process-transaction-operation
  [transaction]
  (def transaction-amount (transaction :amount))
  (def current-amount ((@user-account :account) :availableLimit))
  (cond
    (> transaction-amount current-amount) (
      (println (json/write-str @violations))
      (swap! violations conj "insufficient-limit")
      (swap! violations conj "teste")
      (println (json/write-str { :account (@user-account :account) :violations @violations }))
      )
    :else (
      (def final-amount (- current-amount transaction-amount))
      (swap! user-account update-in [:account :availableLimit] (constantly final-amount))
      (println (json/write-str @user-account))))
  (println "aaaaaaaaaa"))

(defn main-workflow
  []
  (println "Insert your operations:")
  (def account-operation (json/read-str account-data :key-fn keyword))
  (println account-operation)
  (process-acount-operation (account-operation :account))

  (def transaction-operation (json/read-str transaction-data :key-fn keyword))
  (process-transaction-operation (transaction-operation :transaction)))

  (defn -main
    "Main operation workflow"
    [& args]
    (while true (main-workflow)))
  



; { "account": { "activeCard": true, "availableLimit": 100 } }
; { "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }
; { "transaction": { "merchant": "Habbib's", "amount": 90, "time": "2019-02-13T11:00:00.000Z" } }



; (doseq [ln (line-seq (java.io.BufferedReader. *in*))]
;   (println "*************** \n\n\n")
;   (println (clojure.string/split-lines ln))))
;(def operations (json/read-str in))