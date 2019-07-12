(ns tests.all
  (:require
    [cljs.test :refer [deftest is testing run-tests async use-fixtures]]
    [day8.re-frame.test :refer [run-test-async run-test-sync wait-for]]
    [district.ui.service-workers.events :as events]
    [district.ui.service-workers.queries :as queries]
    [district.ui.service-workers.subs :as subs]
    [district.ui.service-workers]
    [mount.core :as mount]
    [re-frame.core :refer [reg-event-fx dispatch-sync subscribe reg-cofx reg-sub dispatch trim-v]]))

(set! day8.re-frame.test/*test-timeout* 15000)

(use-fixtures
  :each
  {:after
   (fn []
     (mount/stop))})


(reg-sub
  ::returned-message
  (fn [db]
    (get db :returned-message)))


(reg-event-fx
  ::return-message-success
  (fn [{:keys [:db]} [_ data]]
    {:db (assoc db :returned-message data)}))


(reg-event-fx
  ::return-message-error
  (constantly nil))


(reg-event-fx
  ::wait-for
  (fn [_ [_ ms]]
    {:dispatch-later [{:ms ms :dispatch [::wait-done]}]}))


(reg-event-fx
  ::wait-done
  (constantly nil))


(defn create-message-channel [{:keys [:on-success :on-error]}]
  (let [msg-chan (js/MessageChannel.)
        port1 (aget msg-chan "port1")]

    (aset port1 "onmessage" (fn [event]
                              (if-let [error (aget event "data" "error")]
                                (dispatch (conj on-error) error)
                                (dispatch (conj on-success (js->clj (aget event "data") :keywordize-keys true))))))

    (aget msg-chan "port2")))


(deftest service-workers
  (run-test-async
    (-> (mount/with-args
          {:service-workers {:workers [{:script-url "/my-service-worker.js" :scope "/"}]}})
      (mount/start))

    (let [returned-message (subscribe [::returned-message])
          workers (subscribe [::subs/workers])
          registration (subscribe [::subs/worker-registration "/"])]
     (wait-for [::events/service-worker-ready]
       (wait-for [::events/controller-changed]

         (is (= "/my-service-worker.js" (get-in @workers ["/" :script-url])))
         (is (= "[object ServiceWorkerRegistration]" (type->str @registration)))

         (let [port2 (create-message-channel {:on-success [::return-message-success]
                                              :on-error [::return-message-error]})]
           (dispatch [::events/post-message {:a 1 :b 2} [port2]])
           (wait-for [::return-message-success ::return-message-error]
             (is (= @returned-message {:a 1 :b 2}))

             (dispatch [::events/unregister {:scope "/"}])
             (dispatch [::wait-for 1000])
             (wait-for [::wait-done]))))))))


(deftest service-workers-without-config
  (run-test-async
    (-> (mount/with-args
          {})
      (mount/start))

    (let [returned-message (subscribe [::returned-message])
          workers (subscribe [::subs/workers])
          registration (subscribe [::subs/worker-registration "/"])]

      (dispatch [::events/register {:script-url "/my-service-worker.js" :scope "/"}])

      (wait-for [::events/worker-registered]

        (is (= "/my-service-worker.js" (get-in @workers ["/" :script-url])))
        (is (= "[object ServiceWorkerRegistration]" (type->str @registration)))

        (let [port2 (create-message-channel {:on-success [::return-message-success]
                                             :on-error [::return-message-error]})]
          (dispatch [::events/post-message {:x 5 :y 7} [port2]])
          (wait-for [::return-message-success ::return-message-error]
            (is (= @returned-message {:x 5 :y 7}))

            (dispatch [::events/unregister {:scope "/"}])
            (dispatch [::wait-for 1000])
            (wait-for [::wait-done])))))))
