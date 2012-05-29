/**
  * Copyright 2011 National ICT Australia Limited
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.nicta.scoobi

/** Global Scoobi functions and values. */
object Scoobi extends com.nicta.scoobi.WireFormatImplicits with com.nicta.scoobi.GroupingImplicits {

  /* Primary types */
  type WireFormat[A] = com.nicta.scoobi.WireFormat[A]
  val DList = com.nicta.scoobi.DList
  type DList[A] = com.nicta.scoobi.DList[A]
  implicit def travPimp[A : Manifest : WireFormat](trav: Traversable[A]) = DList.travPimp(trav)

  val DObject = com.nicta.scoobi.DObject
  type DObject[A] = com.nicta.scoobi.DObject[A]

  type DoFn[A, B, E] = com.nicta.scoobi.DoFn[A, B]
  type BasicDoFn[A, B] = com.nicta.scoobi.BasicDoFn[A, B]
  type EnvDoFn[A, B, E] = com.nicta.scoobi.EnvDoFn[A, B, E]

  val Grouping = com.nicta.scoobi.Grouping
  type Grouping[A] = com.nicta.scoobi.Grouping[A]

  type ScoobiApp = com.nicta.scoobi.ScoobiApp

  /* Persisting */
  def persist[P](p: P)(implicit conf: ScoobiConfiguration, persister: Persister[P]): persister.Out = Persist.persist(p)(conf, persister)
  def persist[P](conf: ScoobiConfiguration)(p: P)(implicit persister: Persister[P]): persister.Out = Persist.persist(p)(conf, persister)
  val Persister = com.nicta.scoobi.Persister


  /* Text file I/O */
  val TextOutput = com.nicta.scoobi.io.text.TextOutput
  val TextInput = com.nicta.scoobi.io.text.TextInput
  val AnInt = TextInput.AnInt
  val ALong = TextInput.ALong
  val ADouble = TextInput.ADouble
  val AFloat = TextInput.AFloat

  def fromTextFile(paths: String*) = TextInput.fromTextFile(paths: _*)
  def fromTextFile(paths: List[String]) = TextInput.fromTextFile(paths)
  def fromDelimitedTextFile[A : Manifest : WireFormat]
      (path: String, sep: String = "\t")
      (extractFn: PartialFunction[List[String], A]) = TextInput.fromDelimitedTextFile(path, sep)(extractFn)
  def toTextFile[A : Manifest](dl: DList[A], path: String, overwrite: Boolean = false) = TextOutput.toTextFile(dl, path, overwrite)
  def toDelimitedTextFile[A <: Product : Manifest](dl: DList[A], path: String, sep: String = "\t", overwrite: Boolean = false) = TextOutput.toDelimitedTextFile(dl, path, sep, overwrite)


  /* Sequence File I/O */
  val SequenceInput = com.nicta.scoobi.io.seq.SequenceInput
  val SequenceOutput = com.nicta.scoobi.io.seq.SequenceOutput
  type SeqSchema[A] = com.nicta.scoobi.io.seq.SeqSchema[A]

  import org.apache.hadoop.io.Writable
  def convertKeyFromSequenceFile[K : Manifest : WireFormat : SeqSchema](paths: String*): DList[K] = SequenceInput.convertKeyFromSequenceFile(paths: _*)
  def convertKeyFromSequenceFile[K : Manifest : WireFormat : SeqSchema](paths: List[String]): DList[K] = SequenceInput.convertKeyFromSequenceFile(paths)
  def convertValueFromSequenceFile[V : Manifest : WireFormat : SeqSchema](paths: String*): DList[V] = SequenceInput.convertValueFromSequenceFile(paths: _*)
  def convertValueFromSequenceFile[V : Manifest : WireFormat : SeqSchema](paths: List[String]): DList[V] = SequenceInput.convertValueFromSequenceFile(paths)
  def convertFromSequenceFile[K : Manifest : WireFormat : SeqSchema, V : Manifest : WireFormat : SeqSchema](paths: String*): DList[(K, V)] = SequenceInput.convertFromSequenceFile(paths: _*)
  def convertFromSequenceFile[K : Manifest : WireFormat : SeqSchema, V : Manifest : WireFormat : SeqSchema](paths: List[String]): DList[(K, V)] = SequenceInput.convertFromSequenceFile(paths)
  def fromSequenceFile[K <: Writable : Manifest : WireFormat, V <: Writable : Manifest : WireFormat](paths: String*): DList[(K, V)] = SequenceInput.fromSequenceFile(paths: _*)
  def fromSequenceFile[K <: Writable : Manifest : WireFormat, V <: Writable : Manifest : WireFormat](paths: List[String]): DList[(K, V)] = SequenceInput.fromSequenceFile(paths)

  def convertKeyToSequenceFile[K : SeqSchema](dl: DList[K], path: String, overwrite: Boolean = false): DListPersister[K] = SequenceOutput.convertKeyToSequenceFile(dl, path, overwrite)
  def convertValueToSequenceFile[V : SeqSchema](dl: DList[V], path: String, overwrite: Boolean = false): DListPersister[V] = SequenceOutput.convertValueToSequenceFile(dl, path, overwrite)
  def convertToSequenceFile[K : SeqSchema, V : SeqSchema](dl: DList[(K, V)], path: String, overwrite: Boolean = false): DListPersister[(K, V)] = SequenceOutput.convertToSequenceFile(dl, path, overwrite)
  def toSequenceFile[K <: Writable : Manifest, V <: Writable : Manifest](dl: DList[(K, V)], path: String, overwrite: Boolean = false): DListPersister[(K, V)] = SequenceOutput.toSequenceFile(dl, path, overwrite)


  /* Avro I/O */
  val AvroInput = com.nicta.scoobi.io.avro.AvroInput
  val AvroOutput = com.nicta.scoobi.io.avro.AvroOutput
  val AvroSchema = com.nicta.scoobi.io.avro.AvroSchema
  type AvroSchema[A] = com.nicta.scoobi.io.avro.AvroSchema[A]

  def fromAvroFile[A : Manifest : WireFormat : AvroSchema](paths: String*) = AvroInput.fromAvroFile(paths: _*)
  def fromAvroFile[A : Manifest : WireFormat : AvroSchema](paths: List[String]) = AvroInput.fromAvroFile(paths)
  def toAvroFile[B : AvroSchema](dl: DList[B], path: String, overwrite: Boolean = false) = AvroOutput.toAvroFile(dl, path, overwrite)


  /* join and coGroup */
  val Join = com.nicta.scoobi.lib.Join

  def join[K : Manifest : WireFormat : Grouping,
           A : Manifest : WireFormat,
           B : Manifest : WireFormat]
      (d1: DList[(K, A)], d2: DList[(K, B)])
      = Join.join(d1, d2)


  def joinRight[K : Manifest : WireFormat : Grouping,
                A : Manifest : WireFormat,
                B : Manifest : WireFormat]
      (d1: DList[(K, A)], d2: DList[(K, B)], default: (K, B) => A)
      = Join.joinRight(d1, d2, default)


  def joinRight[K : Manifest : WireFormat : Grouping,
                A : Manifest : WireFormat,
                B : Manifest : WireFormat]
      (d1: DList[(K, A)], d2: DList[(K, B)])
      = Join.joinRight(d1, d2)


  def joinLeft[K : Manifest : WireFormat : Grouping,
               A : Manifest : WireFormat,
               B : Manifest : WireFormat]
      (d1: DList[(K, A)], d2: DList[(K, B)], default: (K, A) => B)
      = Join.joinLeft(d1, d2, default)


  def joinLeft[K : Manifest : WireFormat : Grouping,
               A : Manifest : WireFormat,
               B : Manifest : WireFormat]
      (d1: DList[(K, A)], d2: DList[(K, B)])
      = Join.joinLeft(d1, d2)

  def coGroup[K  : Manifest : WireFormat : Grouping,
              A : Manifest : WireFormat,
              B : Manifest : WireFormat]
      (d1: DList[(K, A)], d2: DList[(K, B)])
      = Join.coGroup(d1, d2)
}
