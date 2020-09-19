(ns nubank-authorizer.core
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [clojure.core.cache :as cache]))

(def account-data "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
(def transaction-data "{ \"transaction\": { \"merchant\": \"Habbib's\", \"amount\": 90, \"time\": \"2019-02-13T11:00:00.000Z\" } }")

(def user-account (atom { :account { :activeCard false :availableLimit 0}}))
(def user-transactions (atom []))
(def user-violations (atom []))

; (def user-account (cache/fifo-cache-factory { :account { :activeCard false :availableLimit 0}}))
; (def user-transactions (cache/fifo-cache-factory [{ :transaction { :activeCard false :availableLimit 0}}]))
; (def user-violations (cache/fifo-cache-factory []))

(defn process-acount-operation
  [account]
  (swap! user-account assoc :account account)
  (println (json/write-str @user-account)))

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


  ; (if
  ;   (> transaction-amount current-amount) (
  ;     (println (json/write-str @violations))
  ;     (swap! violations conj "insufficient-limit")
  ;     (swap! violations conj "teste")
  ;     ;(println (json/write-str { :account (@user-account :account) :violations @violations }))
  ;     )
  ;     (
  ;     (def final-amount (- current-amount transaction-amount))
  ;     (swap! user-account update-in [:account :availableLimit] (constantly final-amount))
  ;     ;(println (json/write-str @user-account))
  ;     )))

(defn main-workflow
  []
  (println "Insert your operations:")
  (def account-operation (json/read-str account-data :key-fn keyword))
  (println (get account-operation :account))
  (println account-operation)
  (process-acount-operation (account-operation :account))

  (def transaction-operation (json/read-str transaction-data :key-fn keyword))
  (process-transaction-operation (transaction-operation :transaction))
  (println (json/write-str { :account (@user-account :account) :violations @user-violations }))
  (println @user-transactions)
  (read-line))

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