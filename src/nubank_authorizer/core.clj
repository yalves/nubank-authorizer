(ns nubank-authorizer.core
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [nubank-authorizer.processors.transaction :as transaction-processor]))

(def account-data "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
(def transaction-data "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 100, \"time\": \"2019-02-13T11:00:00.000Z\" } }")

(def user-account (atom {}))
(def user-transactions (atom []))
(def user-violations (atom []))

(defn validate-account-creation
  [account user-account]

  (def violations (atom []))
  (def success (atom true))

  (if
    (not (= user-account {}))      
    (do (swap! violations conj "account-already-initialized")
        (reset! success false)))

  { :success @success :violations @violations })

(defn process-acount-operation
  [account]
  (def validation-result (validate-account-creation account @user-account))
  (if 
    (validation-result :success)

    (swap! user-account assoc :account account)

    (do (reset! user-violations (validation-result :violations)))))

(defn validate-transaction
  [transaction account]
  (def transaction-amount (transaction :amount))
  (def current-amount ((account :account) :availableLimit))
  (def violations (atom []))
  (def success (atom true))

  (if (> transaction-amount current-amount)
    (do (swap! violations conj "insufficient-limit")
        (reset! success false)))

  (if (= ((account :account) :activeCard) false)
    (do (swap! violations conj "card-not-active")
        (reset! success false)))
  
  { :success @success :violations @violations })


(defn commit-transaction
  [transaction]
  (def transaction-amount (transaction :amount))
  (def current-amount ((@user-account :account) :availableLimit))
  (def final-amount (- current-amount transaction-amount))
  (swap! user-transactions conj transaction)
  (swap! user-account update-in [:account :availableLimit] (constantly final-amount)))

(defn process-transaction-operation
  [transaction]
  (def validation-result (validate-transaction transaction @user-account))
  (if 
    (validation-result :success)

    (commit-transaction transaction)

    (do (reset! user-violations (validation-result :violations)))))

(defn main-workflow
  []
  (println "Insert your operations:")
  ; (def account-operation (json/read-str account-data :key-fn keyword))
  ; (process-acount-operation (account-operation :account))

  (def transaction-operation (json/read-str (read-line) :key-fn keyword))
  (def transaction-result (transaction-processor/process-transaction-operation (transaction-operation :transaction) @user-account))
  (if-let [value (:account transaction-result)] 
    (do 
      (swap! user-account update-in [:account :availableLimit] (constantly ((:account transaction-result) :availableLimit)))
      (swap! user-transactions conj (transaction-operation :transaction)))
    (do (reset! user-violations transaction-result)))


  (println (json/write-str { :account (@user-account :account) :violations @user-violations }))
  (println @user-transactions))

  (defn -main
    "Main operation workflow"
    [& args]
    (def account-operation (json/read-str account-data :key-fn keyword))
    (process-acount-operation (account-operation :account))
    (while true (main-workflow)))
    
  



; { "account": { "activeCard": true, "availableLimit": 100 } }
; { "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }
; { "transaction": { "merchant": "Habbib's", "amount": 90, "time": "2019-02-13T11:00:00.000Z" } }



; (doseq [ln (line-seq (java.io.BufferedReader. *in*))]
;   (println "*************** \n\n\n")
;   (println (clojure.string/split-lines ln))))
;(def operations (json/read-str in))