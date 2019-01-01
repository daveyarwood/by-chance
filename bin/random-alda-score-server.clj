#!/usr/bin/env bash
"exec" "clojure" "-Sdeps" "{:deps {io.djy/alda-clj {:mvn/version \"0.1.4\"} io.pedestal/pedestal.service {:mvn/version \"0.5.5\"} io.pedestal/pedestal.jetty   {:mvn/version \"0.5.5\"}}}" "$0" "$@"

(require '[alda.core :refer :all]
         '[io.pedestal.http :as http]
         '[io.pedestal.http.body-params :refer (body-params)])

(defn random-rest-rate
  []
  (/ (rand-nth (range 0 70)) 100.0))

(def MS-LOWER 30)
(def MS-UPPER 3000)
(def MIN-OCTAVE 1)
(def MAX-OCTAVE 5)
(def NUMBER-OF-INSTRUMENTS 2)
(def CHROMATIC-SCALE-PROBABILITY 0.25)

(defn random-midi-instrument
  []
  (rand-nth
    ["midi-acoustic-grand-piano"
     "midi-bright-acoustic-piano"
     "midi-electric-grand-piano"
     "midi-honky-tonk-piano"
     "midi-electric-piano-1"
     "midi-electric-piano-2"
     "midi-harpsichord"
     "midi-clavi"
     "midi-celesta"
     "midi-glockenspiel"
     "midi-music-box"
     "midi-vibraphone"
     "midi-marimba"
     "midi-xylophone"
     "midi-tubular-bells"
     "midi-dulcimer"
     "midi-drawbar-organ"
     "midi-percussive-organ"
     "midi-rock-organ"
     "midi-church-organ"
     "midi-reed-organ"
     "midi-accordion"
     "midi-harmonica"
     "midi-tango-accordion"
     "midi-acoustic-guitar-nylon"
     "midi-acoustic-guitar-steel"
     "midi-electric-guitar-jazz"
     "midi-electric-guitar-clean"
     "midi-electric-guitar-palm-muted"
     "midi-electric-guitar-overdrive"
     "midi-electric-guitar-distorted"
     "midi-electric-guitar-harmonics"
     "midi-acoustic-bass"
     "midi-electric-bass-finger"
     "midi-electric-bass-pick"
     "midi-fretless-bass"
     "midi-bass-slap"
     "midi-bass-pop"
     "midi-synth-bass-1"
     "midi-synth-bass-2"
     "midi-violin"
     "midi-viola"
     "midi-cello"
     "midi-contrabass"
     "midi-tremolo-strings"
     "midi-pizzicato-strings"
     "midi-orchestral-harp"
     "midi-timpani"
     "midi-string-ensemble-1"
     "midi-string-ensemble-2"
     "midi-synth-strings-1"
     "midi-synth-strings-2"
     "midi-choir-aahs"
     "midi-voice-oohs"
     "midi-synth-voice"
     "midi-orchestra-hit"
     "midi-trumpet"
     "midi-trombone"
     "midi-tuba"
     "midi-muted-trumpet"
     "midi-french-horn"
     "midi-brass-section"
     "midi-synth-brass-1"
     "midi-synth-brass-2"
     "midi-soprano-saxophone"
     "midi-alto-saxophone"
     "midi-tenor-saxophone"
     "midi-baritone-saxophone"
     "midi-oboe"
     "midi-english-horn"
     "midi-bassoon"
     "midi-clarinet"
     "midi-piccolo"
     "midi-flute"
     "midi-recorder"
     "midi-pan-flute"
     "midi-bottle"
     "midi-shakuhachi"
     "midi-whistle"
     "midi-ocarina"
     "midi-square-lead"
     "midi-saw-wave"
     "midi-calliope-lead"
     "midi-chiffer-lead"
     "midi-charang"
     "midi-solo-vox"
     "midi-fifths"
     "midi-bass-and-lead"
     "midi-synth-pad-new-age"
     "midi-synth-pad-warm"
     "midi-synth-pad-polysynth"
     "midi-synth-pad-choir"
     "midi-synth-pad-bowed"
     "midi-synth-pad-metallic"
     "midi-synth-pad-halo"
     "midi-synth-pad-sweep"
     "midi-fx-rain"
     "midi-fx-soundtrack"
     "midi-fx-crystal"
     "midi-fx-atmosphere"
     "midi-fx-brightness"
     "midi-fx-goblins"
     "midi-fx-echoes"
     "midi-fx-sci-fi"
     "midi-sitar"
     "midi-banjo"
     "midi-shamisen"
     "midi-koto"
     "midi-kalimba"
     "midi-bagpipes"
     "midi-fiddle"
     "midi-shehnai"
     "midi-tinkle-bell"
     "midi-agogo"
     "midi-steel-drums"
     "midi-woodblock"
     "midi-taiko-drum"
     "midi-melodic-tom"
     "midi-synth-drum"
     "midi-reverse-cymbal"
     "midi-guitar-fret-noise"
     "midi-breath-noise"
     "midi-seashore"
     "midi-bird-tweet"
     "midi-telephone-ring"
     "midi-helicopter"
     "midi-applause"
     "midi-gunshot"
     "midi-percussion"]))

(defn random-instruments
  []
  (->> (map vector
            (repeatedly NUMBER-OF-INSTRUMENTS random-midi-instrument)
            (repeatedly NUMBER-OF-INSTRUMENTS #(rand-int 101)))
       (sort-by second)))

(defn random-scale
  []
  (if (< (rand) CHROMATIC-SCALE-PROBABILITY)
    []
    (remove nil?
      [(rand-nth [:a :b :c :d :e :f :g])
       (rand-nth [:flat nil :sharp])
       (rand-nth [:major :dorian :phrygian :lydian :mixolydian :minor :locrian])])))

(defn random-note
  [ms-value rest-rate scale]
  (let [root-note     (when-let [root-note (first scale)]
                        (first (name root-note)))
        random-letter #(rand-nth (if root-note
                                   (concat (remove #{root-note} "abcdefg")
                                           (repeat 4 root-note))
                                   "abcdefg"))]
    (if (< (rand) rest-rate)
      (pause (ms ms-value))
      (let [o (rand-nth (range MIN-OCTAVE (inc MAX-OCTAVE)))
            n (keyword (str (random-letter)))]
        [(octave o)
         (note (pitch n) (ms ms-value))]))))

(defn random-notes
  [score-length-ms rest-rate scale]
  (loop [notes [], ms-remaining score-length-ms]
    (let [ms (rand-nth (range MS-LOWER MS-UPPER))]
      (if (> ms ms-remaining)
        notes
        (recur (conj notes (random-note ms rest-rate scale))
               (- ms-remaining ms))))))

(defn random-score!
  [score-length-ms]
  (let [rest-rate   (random-rest-rate)
        instruments (random-instruments)
        scale       (random-scale)]
    (println "Score length:" (/ score-length-ms 1000) "seconds")
    (println "Rest rate:" (str (Math/round (* 100 rest-rate)) \%))
    (println "Scale:" (if (empty? scale) :chromatic scale))
    (println "Instruments:" instruments)

    (play!
      (if (empty? scale)
        ""
        [(key-sig! (vec scale)) "\n"])
      (for [[instrument pan-value] instruments]
        ["\n"
         (part instrument)
         (panning pan-value)
         (random-notes score-length-ms rest-rate scale)]))))

(defn random-score-response
  [{:keys [form-params] :as request}]
  (if-let [score-length-ms
           (try (* 1000 (Integer/parseInt (:length form-params)))
                (catch Throwable t
                  {:status 400
                   :body   "Invalid `length` parameter."}))]
    {:status 200
     :body   (random-score! score-length-ms)}
    {:status 400
     :body   "Request missing `length` parameter."}))

(.addShutdownHook
  (Runtime/getRuntime)
  (Thread. #(do
              (println (str \newline (stop!)))
              ;; give it a little time to run "alda stop" before exiting
              (Thread/sleep 500))))

(println "Starting server...")

(-> {::http/routes #{["/random-alda-score" :post `random-score-response]}
     ::http/port   8008
     ::http/type   :jetty}
    http/default-interceptors
    (update ::http/interceptors conj (body-params))
    http/create-server
    http/start)

