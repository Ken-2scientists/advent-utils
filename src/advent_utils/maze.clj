(ns advent-utils.maze
  (:require [advent-utils.graph :as g :refer [Graph ->MapGraph]]
            [advent-utils.core :as u]))

(def relative-dirs [:forward :left :backward :right])
(def cardinal-dirs [:north :west :south :east])

(defn relative-directions
  "Returns a map between relative directions (forward, left, backward,right)
   and the cardinal (compass) directions for given cardinal direction"
  [direction]
  (zipmap relative-dirs (u/rotate (u/index-of direction cardinal-dirs) cardinal-dirs)))

(defn next-direction
  "The next cardinal (compass) direction corresponding to a relative direction
   (forward, left, backward, right)"
  [direction turn]
  ((relative-directions direction) turn))

(defn adj-coords
  "Coordinates of four adjacent points, always in the order N W S E"
  [[x y]]
  [[x (dec y)] [(dec x) y] [x (inc y)] [(inc x) y]])

(defn one-step
  "The position one step away from pos in the cardinal direction"
  [pos direction]
  ((adj-coords pos) (u/index-of direction cardinal-dirs)))

(defn neighbors
  "The "
  [maze pos]
  (let [coords (adj-coords pos)
        vals (map maze coords)]
    (zipmap coords vals)))

(defn relative-neighbors
  [maze pos direction]
  (let [neighbor-vals (mapv maze (adj-coords pos))]
    (zipmap relative-dirs (u/rotate (u/index-of direction cardinal-dirs) neighbor-vals))))

(defn follow-left-wall
  [neighbors]
  (case (neighbors :left)
    :open :left
    nil :left
    :wall (if (= :wall (neighbors :forward))
            (if (= :wall (neighbors :right))
              :backward
              :right)
            :forward)))

(defn maze-mapper
  [maze position direction]
  (let [neighbors (relative-neighbors maze position direction)]
    (next-direction direction (follow-left-wall neighbors))))

(defn all-open
  [open? maze]
  (map first (filter #(open? (val %)) maze)))

(defrecord Maze [maze open?]
  Graph
  (vertices
    [_]
    (all-open open? maze))

  (edges
    [_ v]
    (all-open open? (neighbors maze v)))

  (distance
    [_ _ _]
    1)

  (without-vertex
    [_ v]
    (->Maze (assoc maze v :wall) open?)))

(defn Maze->Graph
  [maze]
  (->MapGraph (g/adjacencies maze)))

(defn spread-to-adjacent
  [maze [x y]]
  (let [thens (neighbors maze [x y])
        to-add (filter #(= :open (val %)) thens)]
    (keys to-add)))

(defn flood-fill
  [maze start]
  (loop [newmaze maze last-added [start] count 0]
    (if (= 0 (u/count-if newmaze #(= :open (val %))))
      count
      (let [changes (mapcat (partial spread-to-adjacent newmaze) last-added)
            updates (merge newmaze (zipmap changes (repeat :oxygen)))]
        (recur updates changes (inc count))))))

(defn find-target
  [maze target]
  (ffirst (filter #(= target (val %)) maze)))