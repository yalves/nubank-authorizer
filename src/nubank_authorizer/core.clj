(ns nubank-authorizer.core
  (:gen-class)
  (:require [clojure.data.json :as json]))

;(def data "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } } \n{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
(def account-data "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
(def transaction-data "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 110, \"time\": \"2019-02-13T11:00:00.000Z\" } }")

(def user-account (atom { :account { :activeCard false :availableLimit 0}}))

(defn process-acount-operation
  [account]
  (println (account :activeCard))
  (println (account :availableLimit))
  (swap! user-account assoc :account account)
  (println (json/write-str @user-account)))

(defn process-transaction-operation
  [transaction]
  (println (transaction :merchant))
  (println (transaction :amount))
  (println (transaction :time))
  (def transaction-amount (transaction :amount))
  (def current-amount ((@user-account :account) :availableLimit))
  (println current-amount)
  (def final-amount (- current-amount transaction-amount))
  (swap! user-account update-in [:account :availableLimit] (constantly final-amount))
  (println (json/write-str @user-account)))

(defn main-workflow
  []
  (println "Insert your operations:")
  (def account-operation (json/read-str account-data :key-fn keyword))
  (println account-operation)
  (println "\n")
  (println (account-operation :account))
  (println "\n")
  (process-acount-operation (account-operation :account))
  (println "\n")
  (println "\n")
  (println "\n")

  (def transaction-operation (json/read-str transaction-data :key-fn keyword))
  (println transaction-operation)
  (println "\n")
  (println (transaction-operation :transaction))
  (println "\n")
  (process-transaction-operation (transaction-operation :transaction))
  (println "\n")
  (println "\n")
  (println "\n")
  (read-line))
  ;(println (class operations))
  ;(println operations)
  ;(println (json/write-str {:a 1 :b 2})))

  (defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (while true (main-workflow)))
  



; { "account": { "activeCard": true, "availableLimit": 100 } }
; { "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }
; { "transaction": { "merchant": "Habbib's", "amount": 90, "time": "2019-02-13T11:00:00.000Z" } }



; (doseq [ln (line-seq (java.io.BufferedReader. *in*))]
;   (println "*************** \n\n\n")
;   (println (clojure.string/split-lines ln))))
;(def operations (json/read-str in))