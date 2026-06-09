(defproject com.optikpi/datapipeline-sdk "1.0.0"
  :description "Official Clojure SDK for OptikPI Data Pipeline API"
  :url "https://github.com/gamingcrafts/optikpi-datapipeline-sdk"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [clj-http "3.12.3"]
                 [cheshire "5.12.0"]]
  :source-paths ["src"]
  :test-paths ["test"]
  :profiles {:dev {:dependencies [[clojure-test-check "1.1.1"]]}})
